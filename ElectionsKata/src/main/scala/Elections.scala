import scala.collection.mutable

class Elections(val list: Map[String, List[String]], withDistrict: Boolean) {
  private val candidates: mutable.MutableList[String] = mutable.MutableList.empty
  private val officialCandidates: mutable.MutableList[String] = mutable.MutableList.empty
  private val votesWithoutDistricts: mutable.MutableList[Int] = mutable.MutableList.empty
  private val votesWithDistricts: mutable.Map[String, mutable.MutableList[Int]] = mutable.Map[String, mutable.MutableList[Int]](
    ("District 1" -> mutable.MutableList.empty),
    ("District 2" -> mutable.MutableList.empty),
    ("District 3" -> mutable.MutableList.empty)
  )

  def addCandidate(candidate: String): Unit = {
    officialCandidates += candidate
    candidates += candidate
    votesWithoutDistricts += 0
    votesWithDistricts("District 1") += 0
    votesWithDistricts("District 2") += 0
    votesWithDistricts("District 3") += 0
  }

  def voteFor(elector: String, candidate: String, electorDistrict: String): Unit = {
    if (!withDistrict) {
      if (candidates.contains(candidate)) {
        val index = candidates.indexOf(candidate)
        votesWithoutDistricts(index) = votesWithoutDistricts(index) + 1
      } else {
        candidates += candidate
        votesWithoutDistricts += 1
      }
    } else {
      if (votesWithDistricts.contains(electorDistrict)) {
        val districtVotes = votesWithDistricts(electorDistrict)

        if (candidates.contains(candidate)) {
          val index = candidates.indexOf(candidate)
          districtVotes(index) = districtVotes(index) + 1
        } else {
          candidates += candidate
          for ((_, votes) <- votesWithDistricts) votes += 0
          districtVotes(candidates.length - 1) = districtVotes(candidates.length - 1) + 1
        }
      }
    }
  }

  def results(): Map[String, String] = {
    val results: mutable.Map[String, String] = mutable.Map[String, String]()
    var nbVotes = 0
    var nullVotes = 0
    var blankVotes = 0
    var nbValidVotes = 0.0

    if (!withDistrict) {
      nbVotes = votesWithoutDistricts.sum
      for (i <- officialCandidates.indices) {
        val index = candidates.indexOf(officialCandidates(i))
        nbValidVotes += votesWithoutDistricts(index)
      }

      for (i <- 0 until votesWithoutDistricts.length) {
        val candidateResult = votesWithoutDistricts(i) * 100 / nbValidVotes
        val candidate = candidates(i)

        if (officialCandidates.contains(candidate)) {
          results(candidate) = "%.2f".format(candidateResult)
        } else {
          if (candidates(i) == "")
            blankVotes += votesWithoutDistricts(i)
          else nullVotes += votesWithoutDistricts(i)
        }
      }
    } else {
      for (entry <- votesWithDistricts) {
        val districtVotes = entry._2
        nbVotes += districtVotes.map(i => i).sum
      }

      for (i <- officialCandidates.indices) {
        val index = candidates.indexOf(officialCandidates(i))
        for (entry <- votesWithDistricts) {
          val districtVotes = entry._2
          nbValidVotes += districtVotes(index)
        }
      }

      val officialCandidatesResult: mutable.Map[String, Int] = mutable.Map[String, Int]()
      for (i <- 0 until officialCandidates.length) officialCandidatesResult(candidates(i)) = 0

      for (entry <- votesWithDistricts) {
        val districtResult: mutable.MutableList[Double] = mutable.MutableList.empty
        val districtVotes = entry._2

        for (i <- districtVotes.indices) {
          val candidateResult = if (nbValidVotes != 0) districtVotes(i) * 100 / nbValidVotes else 0d
          val candidate = candidates(i)

          if (officialCandidates.contains(candidate)) {
            districtResult += candidateResult
          } else {
            if (candidates(i) == "")
              blankVotes += districtVotes(i)
            else nullVotes += districtVotes(i)
          }
        }

        var districtWinnerIndex = 0
        for (i <- 1 until districtResult.length) {
          if (districtResult(districtWinnerIndex) < districtResult(i))
            districtWinnerIndex = i
        }

        officialCandidatesResult(candidates(districtWinnerIndex)) = officialCandidatesResult(candidates(districtWinnerIndex)) + 1
      }

      for (i <- 0 until officialCandidatesResult.size) {
        val ratioCandidate = officialCandidatesResult(candidates(i)).toDouble / officialCandidatesResult.size * 100
        results(candidates(i)) = "%.2f".format(ratioCandidate)
      }
    }

    val blankResult = blankVotes * 100d / nbVotes
    results("Blank") = "%.2f".format(blankResult)

    val nullResult = nullVotes * 100d / nbVotes
    results("Null") = "%.2f".format(nullResult)

    val nbElectors = list.values.map(res => res.length).sum
    val abstentionResult = 100 - nbVotes * 100d / nbElectors
    results("Abstention") = "%.2f".format(abstentionResult)

    results.toMap
  }
}

package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.user.User

import scala.util.{Failure, Success, Try}

class TripService(val tripDAO: TripDAO) {
  private def noTrips: Seq[Trip] = List.empty

  def getFriendTrips(user: User, loggedInUser: User): Try[Seq[Trip]] = {
    checkUser(loggedInUser) { loggedUser =>
      {
        if (user.isFriendWith(loggedUser))
          tripDAO.findTripsBy(user)
        else noTrips
      }
    }
  }

  private def checkUser(
      loggedInUser: User
  )(continueWith: User => Seq[Trip]): Try[Seq[Trip]] = {
    if (loggedInUser == null)
      Failure(new UserNotLoggedInException)
    else Success(continueWith(loggedInUser))
  }
}

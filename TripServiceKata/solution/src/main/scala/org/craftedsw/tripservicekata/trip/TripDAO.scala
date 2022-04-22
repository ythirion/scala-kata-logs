package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.CollaboratorCallException
import org.craftedsw.tripservicekata.user.User

class TripDAO() {
  def findTripsBy(user: User): Seq[Trip] = TripDAO.findTripsByUser(user)
}

object TripDAO {

  def findTripsByUser(user: User): Seq[Trip] = {
    throw new CollaboratorCallException(
      "TripDAO should not be invoked on an unit test."
    );
  }

}

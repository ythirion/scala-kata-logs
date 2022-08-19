package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.CollaboratorCallException
import org.craftedsw.tripservicekata.user.User

object TripDAO {
	def findTripsByUser(user: User): List[Trip] = {
		throw new CollaboratorCallException(
			"TripDAO should not be invoked on an unit test.");
	}
}

class TripDAO() {
	def findTripsBy(user: User): List[Trip] = TripDAO.findTripsByUser(user)
}

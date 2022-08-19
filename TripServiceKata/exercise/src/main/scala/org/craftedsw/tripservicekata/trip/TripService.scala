package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.user.{User, UserSession}

import scala.util.{Failure, Success, Try}

class TripService(val tripDAO: TripDAO) {
	private def emptyTrips: List[Trip] = List()

	def getFriendTrips(user: User): Try[List[Trip]] = {
		checkUser(getLoggedUser) { loggedUser =>
			if (user.isFriendWith(loggedUser))
				tripDAO.findTripsBy(user)
			else emptyTrips
		}
	}

	private def checkUser(loggedInUser: User)
											 (continueWith: User => List[Trip]): Try[List[Trip]] = {
		if (loggedInUser == null)
			Failure(new UserNotLoggedInException)
		else Success(continueWith(loggedInUser))
	}

	protected def getLoggedUser = UserSession getLoggedUser()
}
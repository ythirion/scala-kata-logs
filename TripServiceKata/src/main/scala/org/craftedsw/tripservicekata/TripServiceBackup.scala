package org.craftedsw.tripservicekata

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.trip.{Trip, TripDAO}
import org.craftedsw.tripservicekata.user.{User, UserSession}

import scala.util.control.Breaks._

class TripServiceBackup {

  def getTripsByUser(user: User): List[Trip] = {
    var tripList: List[Trip] = List()
    val loggedInUser = UserSession getLoggedUser ()
    var isFriend = false
    if (loggedInUser != null) {
      breakable {
        for (friend <- user.friends()) {
          if (friend == loggedInUser) {
            isFriend = true
            break
          }
        }
      }
      if (isFriend) {
        tripList = TripDAO.findTripsByUser(user)
      }
      tripList
    } else {
      throw new UserNotLoggedInException
    }
  }

}

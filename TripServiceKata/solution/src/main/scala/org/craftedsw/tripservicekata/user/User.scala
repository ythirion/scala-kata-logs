package org.craftedsw.tripservicekata.user

import org.craftedsw.tripservicekata.trip.Trip

class User(val trips: Seq[Trip], val friends: Seq[User]) {
  def addFriend(user: User): User =
    new User(trips, user +: friends)

  def addTrip(trip: Trip) =
    new User(trip +: trips, friends)

  def isFriendWith(anotherUser: User): Boolean =
    friends.contains(anotherUser)
}

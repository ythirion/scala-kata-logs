package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.user.User

class UserBuilder {
  private var friends: Seq[User] = Seq.empty
  private var trips: Seq[Trip] = Seq.empty

  def friendsWith(friends: User*): UserBuilder =
    assign(_ => this.friends = friends)

  def travelledTo(trips: Trip*): UserBuilder =
    assign(_ => this.trips = trips)

  private def assign(func: Any => Unit): UserBuilder = {
    func()
    this
  }

  def build(): User = {
    val user = new User()
    addFriendsTo(user)
    addTripsTo(user)
    user
  }

  private def addTripsTo(user: User): Unit = trips.foreach(user.addTrip)

  private def addFriendsTo(user: User): Unit = friends.foreach(user.addFriend)
}

object UserBuilder {
  def aUser(): UserBuilder = new UserBuilder
}
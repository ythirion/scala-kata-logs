package org.craftedsw.tripservicekata.user

import org.craftedsw.tripservicekata.trip.Trip

class UserBuilder {
  private var friends: Seq[User] = Seq.empty
  private var trips: Seq[Trip] = Seq.empty

  def friendsWith(friends: User*): UserBuilder =
    assign(_ => this.friends = friends)

  def travelledTo(trips: Trip*): UserBuilder =
    assign(_ => this.trips = trips)

  def build(): User = new User(trips, friends)

  private def assign(func: Any => Unit): UserBuilder = {
    func()
    this
  }
}

object UserBuilder {
  def aUser(): UserBuilder = new UserBuilder
}

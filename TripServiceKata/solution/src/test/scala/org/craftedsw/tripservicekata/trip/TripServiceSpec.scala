package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.infrastructure.UnitSpec
import org.craftedsw.tripservicekata.user.{User, UserBuilder}
import org.scalatest.TryValues

class TripServiceSpec extends UnitSpec with TryValues {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = UserBuilder.aUser().build()
  private val anotherUser: User = UserBuilder.aUser().build()

  private val portugal: Trip = new Trip()
  private val springfield: Trip = new Trip()

  private val tripDAOStub: TripDAO = stub[TripDAO]
  private val tripService: TripService = new TripService(tripDAOStub)

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    tripService
      .getFriendTrips(unusedUser, guest)
      .failure
      .exception shouldBe a[UserNotLoggedInException]
  }

  "Retrieving the trips by user" should "return no trips when users are not friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser)
      .travelledTo(portugal)
      .build()

    tripService
      .getFriendTrips(aUserWithTrips, registeredUser)
      .success
      .value
      .isEmpty should be(true)
  }

  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser, registeredUser)
      .travelledTo(portugal, springfield)
      .build()

    (tripDAOStub.findTripsBy _)
      .when(aUserWithTrips)
      .returns(aUserWithTrips.trips)

    tripService
      .getFriendTrips(aUserWithTrips, registeredUser)
      .success
      .value
      .size should be(2)
  }
}

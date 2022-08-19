package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.infrastructure.UnitSpec
import org.craftedsw.tripservicekata.user.User
import org.scalatest.{BeforeAndAfterEach, TryValues}

class TripServiceSpec extends UnitSpec with BeforeAndAfterEach with TryValues {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = UserBuilder.aUser().build()
  private val anotherUser: User = UserBuilder.aUser().build()
  private val tripDAOStub: TripDAO = stub[TripDAO]

  private val portugal: Trip = new Trip()
  private val springfield: Trip = new Trip()

  private val tripService: TripService = createTripService()
  private var loggedInUser: User = _


  override def beforeEach(): Unit = {
    loggedInUser = registeredUser
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedInUser = guest
    tripService.getFriendTrips(unusedUser)
      .failure
      .exception shouldBe a[UserNotLoggedInException]
  }

  "Retrieving the trips by user" should "return no trips when users are not friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser)
      .travelledTo(portugal)
      .build()

    assert(tripService
      .getFriendTrips(aUserWithTrips)
      .success
      .value
      .isEmpty)
  }

  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser, loggedInUser)
      .travelledTo(portugal, springfield)
      .build()

    (tripDAOStub.findTripsBy _)
      .when(aUserWithTrips)
      .returns(aUserWithTrips.trips())

    assert(tripService
      .getFriendTrips(aUserWithTrips)
      .success
      .value
      .size == 2)
  }

  private def createTripService() =
    new TripService(tripDAO = tripDAOStub) {
      override protected def getLoggedUser: User = loggedInUser
    }
}
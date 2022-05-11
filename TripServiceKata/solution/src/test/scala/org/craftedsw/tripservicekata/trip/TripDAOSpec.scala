package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.CollaboratorCallException
import org.craftedsw.tripservicekata.infrastructure.UnitSpec
import org.craftedsw.tripservicekata.user.UserBuilder

class TripDAOSpec extends UnitSpec {
  "Retrieving user trips" should "throw an exception" in {
    assertThrows[CollaboratorCallException] {
      new TripDAO().findTripsBy(UserBuilder.aUser().build())
    }
  }
}

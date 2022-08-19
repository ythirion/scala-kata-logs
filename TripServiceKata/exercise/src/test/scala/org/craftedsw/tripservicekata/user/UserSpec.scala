package org.craftedsw.tripservicekata.user

import org.craftedsw.tripservicekata.infrastructure.UnitSpec
import org.craftedsw.tripservicekata.trip.UserBuilder._

class UserSpec extends UnitSpec {

  "User" should "inform when users are not friends" in {
    val user = aUser()
      .build()
    val anotherUser = aUser().build()

    assert(!user.isFriendWith(anotherUser))
  }

  "User" should "inform when users are friends" in {
    val anotherUser = aUser().build()
    val user = aUser()
      .friendsWith(anotherUser)
      .build()

    assert(user.isFriendWith(anotherUser))
  }
}

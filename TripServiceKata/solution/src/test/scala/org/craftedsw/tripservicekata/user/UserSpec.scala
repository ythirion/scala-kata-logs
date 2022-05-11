package org.craftedsw.tripservicekata.user

import org.craftedsw.tripservicekata.infrastructure.UnitSpec

class UserSpec extends UnitSpec {
  private val rick: User = UserBuilder.aUser().build()
  private val morty: User = UserBuilder.aUser().build()

  "User" should "inform when users are not friends" in {
    val user = UserBuilder
      .aUser()
      .friendsWith(rick)
      .build()

    assert(!user.isFriendWith(morty))
  }

  "User" should "inform when users are friends" in {
    val user = UserBuilder
      .aUser()
      .friendsWith(rick, morty)
      .build()

    assert(user.isFriendWith(morty))
    assert(user.isFriendWith(rick))
  }
}

# Step by Step
> Challenge for today : refactor the `TripService` class to ensure Clean Code / SOLID principles

Legacy code golden rules :
`You cannot change any existing code if not covered by tests.`

The only exception is if we need to change the code to add unit tests, but in this case, just automated refactorings (via IDE) are allowed.

![Legacy code refactoring](img/legacy-code-refactoring.png)

## Tips
- Start testing from shortest to deepest branch
- Start refactoring from deepest to shortest branch

![Working with Legacy Code Tips](img/tips.png)

## Cover our code
- Identify code smells in the `TripService` class

```scala
class TripService {
	// Too many responsibilities
	// Hidden dependencies : Session / DAO
	def getTripsByUser(user: User): List[Trip] = {
		// Mutation everywhere
		var tripList: List[Trip] = List()
		// Object method like static method in other languages 
		val loggedInUser = UserSession getLoggedUser()
		var isFriend = false
		
		// CC too high
		if (loggedInUser != null) {
			// What is the logic here ?
			breakable { for (friend <- user.friends()) {
				if (friend == loggedInUser) {
					isFriend = true
					break
				}
			}}
			// 
			if (isFriend) {
				// Object method like static method in other languages
				tripList = TripDAO.findTripsByUser(user)
			}
			tripList
		} else {
			// Return a Try or an Either instead of throwing this exception
			throw new UserNotLoggedInException
		}
	}
}
// Why do we have a backup class in the repository ?
```

### Write a first test (naively)
```scala
"Retrieving the trips by user" should "throw an exception when user not logged in" in {
    val tripService = new TripService()
    
    assertThrows[UserNotLoggedInException] {
        tripService.getTripsByUser(null)
    }
}
```
- Here we have a problem
	- The exception thrown is not the expected one : `Expected exception org.craftedsw.tripservicekata.exception.UserNotLoggedInException to be thrown, but org.craftedsw.tripservicekata.exception.CollaboratorCallException was thrown`
	- This exception is thrown by a hidden collaborator : `UserSession`
- We will have the same problem with the other hidden dependencies

> How could we make it testable ?

### Seams
- Adding tests on the existing code can be challenging
- The code was not written to be testable in the first place
- 99% of the time, this is a dependency problem
- 	The code you want to test can’t run
	- It needs something hard to put in the test :
		- A database connection 
		- A third-party server
		- A parameter that’s complex to instantiate
	- Usually, it’s a complex mix of all that.

> To test your code, you need to break these dependencies in the tests.

Therefore, you need to identify `Seams`.

`A Seam is a place to alter program behavior, without changing the code.`

There are different types of Seams. 
The gist of it is to identify how you can change the code behavior without touching the source code.

If your language is Object-Oriented, the most common and convenient Seam is an object.

Consider this piece of JavaScript code :

```javascript
export class DatabaseConnector {
  // A lot of code…

  connect() {
    // Perform some calls to connect to the DB.
  }
}
```

Say the connect() method is causing you problems when you try to put code into tests. Well, the `whole class is a Seam you can alter`.

You can extend this class in tests to prevent it from connecting to an actual DB:

```javascript
class FakeDatabaseConnector extends DatabaseConnector {
  connect() {
    // Override the problematic calls to the DB
    console.log("Connect to the DB")
  }
}
```

### Isolate the Singleton dependencies
- Isolate the Singleton dependencies in their own method

```scala
UserSession getLoggedUser()
TripDAO.findTripsByUser(user)
```

- Do it via automated `extract method` automated refactoring

```scala
class TripService {
  def getTripsByUser(user: User): List[Trip] = {
    ...
    val loggedInUser = getLoggedUser
    ...    
    if (isFriend) {
        tripList = findTripsByUser(user).toList
    }
    ...
  }

  protected def findTripsByUser(user: User): Seq[Trip] =
    TripDAO.findTripsByUser(user)

  protected def getLoggedUser = UserSession getLoggedUser ()
}
```

### Refactor our first test
- In our test class, we can now extend the `TripService` class
- We can now override the `private` to `protected` methods we created making them return whatever we need for our unit tests

```scala
private def createTripService() =
    new TripService {
      override protected def getLoggedUser: User = loggedUser
    
      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
  }
```

- Refactor the test to use the `TripService` for tests class
```scala
class TripServiceSpec extends UnitSpec {
  private var loggedUser: User = new User()
  private val nonLoggedUser = null
  private val tripService: TripService = createTripService()

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedUser = nonLoggedUser
    assertThrows[UserNotLoggedInException] {
      tripService.getTripsByUser(null)
    }
  }

  private def createTripService() =
    new TripService {
      override protected def getLoggedUser: User = loggedUser

      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
}
```

### Coverage as a Driver
- Run your favorite `code coverage` tool
- Use the result as a driver for implementing/writing new tests
![Code coverage](img/coverage.png)

- `From shortest to deepest branch`, what is our next test to write ?

```scala
  "Retrieving the trips by user" should "return no trips when logged user is not a friend" in {
    val trips = tripService.getTripsByUser(targetUser)
    assert(trips.isEmpty)
  }
```
- This test is green when running alone
  - But red when ran with the rest of the test class...
  - We have introduced a strong dependency between our tests through the `loggedUser`
  - We must ensure to respect FIRST principle
    - `Fast` : Tests should be fast enough that you won't be discouraged from using them
    - `Isolated` : Tests should not depend on the state of another test
    - `Repeatable` : Tests should be repeatable in any environment without varying results
    - `Self validating` : Each test will have a single boolean output of pass or fail
    - `Thorough` : The tests we write should cover all happy paths/edge/corner/boundary cases
- Let's isolate our tests :
    - With `scalatest` we can use the trait `BeforeAndAfterEach` to set up our tests :
```scala
class TripServiceSpec extends UnitSpec with BeforeAndAfterEach {
  private var loggedUser: User = _
  private val targetUser: User = new User()
  private val nonLoggedUser = null
  private val tripService: TripService = createTripService()

  override def beforeEach(): Unit = {
    loggedUser = new User()
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedUser = nonLoggedUser
    assertThrows[UserNotLoggedInException] {
      tripService.getTripsByUser(null)
    }
  }

  "Retrieving the trips by user" should "return no trips when logged user is not a friend" in {
    val trips = tripService.getTripsByUser(targetUser)
    assert(trips.isEmpty)
  }
  ...
}
```

- We have improved our coverage
![Code coverage improved](img/coverage2.png)
- BUT do we do enough in our last test ?
  - The user on which we ask for trips does not contain any trips nor any friends...
  - `we can not be sure our feature is implemented well`
  - We need to improve our test setup
    - We clean up a little the code as well
      - Avoid having not named values like `null` or `new Trip()` in your tests
      - Give sense to those by naming them

```scala
class TripServiceSpec extends UnitSpec with BeforeAndAfterEach {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = new User()
  private val anotherUser: User = new User()
  private val toPortugal: Trip = new Trip()

  private val tripService: TripService = createTripService()
  private var loggedInUser: User = _

  override def beforeEach(): Unit = {
    loggedInUser = registeredUser
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedInUser = guest
    assertThrows[UserNotLoggedInException] {
      tripService.getTripsByUser(unusedUser)
    }
  }

  "Retrieving the trips by user" should "return no trips when users are not friends" in {
    val aUserWithTrips = new User()
    aUserWithTrips.addTrip(toPortugal)
    aUserWithTrips.addFriend(anotherUser)

    val trips = tripService.getTripsByUser(aUserWithTrips)

    assert(trips.isEmpty)
  }

  private def createTripService() =
    new TripService {
      override protected def getLoggedUser: User = loggedInUser

      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
}
```

![Code coverage improved again](img/coverage3.png)

- Let's cover the `happy path`
```scala 
class TripServiceSpec extends UnitSpec with BeforeAndAfterEach {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = new User()
  private val anotherUser: User = new User()

  private val toPortugal: Trip = new Trip()
  private val toSpringfield: Trip = new Trip()

  private val tripService: TripService = createTripService()
  private var loggedInUser: User = _

  override def beforeEach(): Unit = {
    loggedInUser = registeredUser
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedInUser = guest
    assertThrows[UserNotLoggedInException] {
      tripService.getTripsByUser(unusedUser)
    }
  }

  "Retrieving the trips by user" should "return no trips when users are not friends" in {
    val aUserWithTrips = new User()
    aUserWithTrips.addTrip(toPortugal)
    aUserWithTrips.addFriend(anotherUser)

    val trips = tripService.getTripsByUser(aUserWithTrips)

    assert(trips.isEmpty)
  }

  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = new User()
    aUserWithTrips.addTrip(toPortugal)
    aUserWithTrips.addTrip(toSpringfield)
    aUserWithTrips.addFriend(anotherUser)
    aUserWithTrips.addFriend(loggedInUser)

    val trips = tripService.getTripsByUser(aUserWithTrips)

    assert(trips.size == 2)
  }

  private def createTripService() =
    new TripService {
      override protected def getLoggedUser: User = loggedInUser

      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
} 
```

### What can be improved ?
- We have some duplications in our tests
  - And some test setups already require somne cognitive resources to understand what is going on :
```scala
val aUserWithTrips = new User()
aUserWithTrips.addTrip(toPortugal)
aUserWithTrips.addTrip(toSpringfield)
aUserWithTrips.addFriend(anotherUser)
aUserWithTrips.addFriend(loggedInUser)
```
- Let's use [Test Data Builders](http://www.natpryce.com/articles/000714.html) to improve it
  - It helps to hide / encapsulate / centralize the creation of your objects
    - Make it more flexible if your design change
  - Make your tests more readable and more business oriented
- Define your builder from your IDE
  - Type it directly in your test

```scala
val aUserWithTrips = UserBuilder.aUser()
  .friendsWith(anotherUser, loggedInUser)
  .travelledTo(toPortugal, toSpringfield)
  .build()
```
- Now let our IDE generates the code for us
```scala
class UserBuilder {
  private var friends: Seq[User] = Seq.empty
  private var trips: Seq[Trip] = Seq.empty

  def friendsWith(friends: User*): UserBuilder = {
    this.friends = friends
    this
  }

  def travelledTo(trips: Trip*): UserBuilder = {
    this.trips = trips
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
```
- Improve the builder with a Higher Order Function that avoid duplication
```scala
class UserBuilder {
  private var friends: Seq[User] = Seq.empty
  private var trips: Seq[Trip] = Seq.empty

  def friendsWith(friends: User*): UserBuilder =
    assign(_ => this.friends = friends)

  def travelledTo(trips: Trip*): UserBuilder =
    assign(_ => this.trips = trips)

  def build(): User = {
    val user = new User()
    addFriendsTo(user)
    addTripsTo(user)
    user
  }

  private def addTripsTo(user: User): Unit = trips.foreach(user.addTrip)

  private def addFriendsTo(user: User): Unit = friends.foreach(user.addFriend)

  private def assign(func: Any => Unit): UserBuilder = {
    func()
    this
  }
}
```

- Use it in all of our tests
```scala
package org.craftedsw.tripservicekata.trip

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException
import org.craftedsw.tripservicekata.infrastructure.UnitSpec
import org.craftedsw.tripservicekata.user.{User, UserBuilder}
import org.scalatest.BeforeAndAfterEach

class TripServiceSpec extends UnitSpec with BeforeAndAfterEach {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = UserBuilder.aUser().build()
  private val anotherUser: User = UserBuilder.aUser().build()

  private val portugal: Trip = new Trip()
  private val springfield: Trip = new Trip()

  private val tripService: TripService = createTripService()
  private var loggedInUser: User = _

  override def beforeEach(): Unit = {
    loggedInUser = registeredUser
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedInUser = guest
    assertThrows[UserNotLoggedInException] {
      tripService.getFriendTrips(unusedUser, )
    }
  }

  "Retrieving the trips by user" should "return no trips when users are not friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser)
      .travelledTo(portugal)
      .build()

    val trips = tripService.getFriendTrips(loggedInUser = )

    assert(trips.isEmpty)
  }

  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser, loggedInUser)
      .travelledTo(portugal, springfield)
      .build()

    val trips = tripService.getFriendTrips(loggedInUser = )

    assert(trips.size == 2)
  }

  private def createTripService() =
    new TripService() {
      override protected def getLoggedUser: User = loggedInUser

      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
}
```

> We are now ready to refactor

## Refactoring
- Start refactoring from deepest to shortest branch

### [Feature Envy](http://wiki.c2.com/?FeatureEnvySmell)
```scala
breakable {
    for (friend <- user.friends()) {
      if (friend == loggedInUser) {
        isFriend = true
        break
      }
    }
}
```

- A common code smell
- When a class gets data from another class in order to do some calculation or comparison on that data
  - it means that the client class envies the other class
  - In OO, data and the operations on that data should be on the same object

> The whole point of objects is that they are a technique to package data with the processes used on that data. A classic smell is a method that seems more interested in a class other than the one it is in. The most common focus of the envy is the data.

- Let's fix it by using TDD (Test Driven Development)
![img.png](img/tdd.png)
- Implement a new behavior on `User` : `isFriendWith` 

```scala
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
}

class User {
  ...
  // Implement the minimum to pass the test
  def isFriendWith(morty: User): Boolean = false
}
```

- We add a second test (the passing one)
```scala
"User" should "inform when users are friends" in {
    val user = UserBuilder
      .aUser()
      .friendsWith(rick, morty)
      .build()
    
    assert(user.isFriendWith(morty))
    assert(user.isFriendWith(rick))
}

class User {
  ...
  def isFriendWith(anotherUser: User): Boolean =
      friendList.contains(anotherUser)
}
```

- We can now focus on our TripService :
  - Run your tests at any change in your production code
```scala
  def getTripsByUser(user: User): List[Trip] = {
    var tripList: List[Trip] = List()
    val loggedInUser = getLoggedUser
    if (loggedInUser != null) {
      var isFriend = false
      user.isFriendWith(loggedInUser)

      if (isFriend) {
        tripList = findTripsByUser(user).toList
      }
      tripList
    } else {
      throw new UserNotLoggedInException
    }
  }
```
- Simplify the code :
```scala
def getTripsByUser(user: User): List[Trip] = {
    var tripList: List[Trip] = List()
    val loggedInUser = getLoggedUser
    if (loggedInUser != null) {
      if (user.isFriendWith(loggedInUser)) {
        tripList = findTripsByUser(user).toList
      }
      tripList
    } else {
      throw new UserNotLoggedInException
    }
  }
```

### Guard clause
- Move the guard clause to reduce complexity
```scala
  def getTripsByUser(user: User): List[Trip] = {
    val loggedInUser = getLoggedUser
    if (loggedInUser == null) {
      throw new UserNotLoggedInException
    }

    var tripList: List[Trip] = List()
    if (user.isFriendWith(loggedInUser)) {
      tripList = findTripsByUser(user).toList
    }
    tripList
  }
```

### Simplify the code
- We want to remove the `var tripList` now
```scala
  def getTripsByUser(user: User): List[Trip] = {
    val loggedInUser = getLoggedUser
    if (loggedInUser == null) {
      throw new UserNotLoggedInException
    }

    if (user.isFriendWith(loggedInUser))
      findTripsByUser(user).toList
    else List.empty
  }
```
- Use a business term for `List.empty`
```scala
  private def noTrips: List[Trip] = List.empty

  def getTripsByUser(user: User): List[Trip] = {
    val loggedInUser = getLoggedUser
    if (loggedInUser == null) {
      throw new UserNotLoggedInException
    }

    if (user.isFriendWith(loggedInUser))
      findTripsByUser(user).toList
    else noTrips
  }
```

- We can extract a method for the guard as well
```scala
  def getTripsByUser(user: User): List[Trip] = {
    checkUser(getLoggedUser)

    if (user.isFriendWith(getLoggedUser))
      findTripsByUser(user).toList
    else noTrips
  }

  private def checkUser(loggedInUser: User) = {
    if (loggedInUser == null) {
      throw new UserNotLoggedInException
    }
  }
```

### Be transparent in your contracts
- For this kind of guard we should favor continuation
  - If we do so we should change the return type of our method as well to represent the computation issue that could be raised
  - In other terms we should use a monad
  - Our methods should be as explicit as possible
- Let's decide to use a `Try`
```scala
class TripService {
  private def noTrips: List[Trip] = List.empty

  def getTripsByUser(user: User): Try[Seq[Trip]] = {
    checkUser(getLoggedUser) { loggedUser =>
      {
        if (user.isFriendWith(loggedUser))
          findTripsByUser(user).toList
        else noTrips
      }
    }
  }

  private def checkUser(loggedInUser: User)(continueWith: User => List[Trip])
      : Try[List[Trip]] = {
    if (loggedInUser == null)
      Failure(new UserNotLoggedInException)
    else Success(continueWith(loggedInUser))
  }

  protected def findTripsByUser(user: User): Seq[Trip] =
    TripDAO.findTripsByUser(user)

  protected def getLoggedUser = UserSession getLoggedUser ()
}
```
- We have changed the public contract of the class
  - We must adapt our tests
  - Use `TryValues` trait to simplify assertions
```scala
class TripServiceSpec extends UnitSpec with BeforeAndAfterEach with TryValues {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = UserBuilder.aUser().build()
  private val anotherUser: User = UserBuilder.aUser().build()

  private val portugal: Trip = new Trip()
  private val springfield: Trip = new Trip()

  private val tripService: TripService = createTripService()
  private var loggedInUser: User = _

  override def beforeEach(): Unit = {
    loggedInUser = registeredUser
  }

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    loggedInUser = guest
    tripService
      .getTripsByUser(unusedUser)
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
      .getTripsByUser(aUserWithTrips)
      .success
      .value
      .isEmpty should be(true)
  }

  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser, loggedInUser)
      .travelledTo(portugal, springfield)
      .build()

    tripService
      .getTripsByUser(aUserWithTrips)
      .success
      .value
      .size should be(2)
  }

  private def createTripService() =
    new TripService {
      override protected def getLoggedUser: User = loggedInUser

      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
}
```

### Make the implicit explicit
- We can now attack our dependency issues

#### LoggedInUser
- Break the internal dependency by injecting the `loggedInUser` in our method
- Let's use our IDE for that -> `Change signature...`
  - `⌘ + F6`
![Change signature](img/change-signature.png)
- Fix the tests by passing the `loggedInUser`
- Use the `loggedInUser` passed in method argument
```scala
  def getTripsByUser(user: User, loggedInUser: User): Try[Seq[Trip]] = {
    checkUser(loggedInUser) { loggedUser =>
      {
        if (user.isFriendWith(loggedUser))
          findTripsByUser(user).toList
        else noTrips
      }
    }
  }
```
- We can now remove the `getLoggedUser` method
  - Both in our production and test codes
```scala
class TripService {
  private def noTrips: List[Trip] = List.empty

  def getTripsByUser(user: User, loggedInUser: User): Try[Seq[Trip]] = {
    checkUser(loggedInUser) { loggedUser =>
      {
        if (user.isFriendWith(loggedUser))
          findTripsByUser(user).toList
        else noTrips
      }
    }
  }

  private def checkUser(
      loggedInUser: User
  )(continueWith: User => List[Trip]): Try[List[Trip]] = {
    if (loggedInUser == null)
      Failure(new UserNotLoggedInException)
    else Success(continueWith(loggedInUser))
  }

  protected def findTripsByUser(user: User): Seq[Trip] =
    TripDAO.findTripsByUser(user)
}
```
- Make some clean up in our tests
    - No need to set a loggedUser anymore
```scala
class TripServiceSpec extends UnitSpec with TryValues {
  private val guest = null
  private val unusedUser = null
  private val registeredUser: User = UserBuilder.aUser().build()
  private val anotherUser: User = UserBuilder.aUser().build()

  private val portugal: Trip = UserBuilder.aUser().build()
  private val springfield: Trip = UserBuilder.aUser().build()

  private val tripService: TripService = createTripService()

  "Retrieving the trips by user" should "throw an exception when user not logged in" in {
    tripService
      .getTripsByUser(unusedUser, guest)
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
      .getTripsByUser(aUserWithTrips, registeredUser)
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

    tripService
      .getTripsByUser(aUserWithTrips, registeredUser)
      .success
      .value
      .size should be(2)
  }

  private def createTripService() =
    new TripService {
      override protected def findTripsByUser(user: User): Seq[Trip] =
        user.trips()
    }
}
```

#### TripDAO
- We want to be able to mock the behavior of our `Data Access` layer :
    - We need an instance method to do so with `scalamock`
- First, let's write a test on the implemented behavior
```scala
class TripDAOSpec extends UnitSpec {
  "Retrieving user trips" should "throw an exception" in {
    assertThrows[CollaboratorCallException] {
      TripDAO.findTripsByUser(UserBuilder.aUser().build())
    }
  }
}
```
- Now let's change our test and create our code from it
```scala
class TripDAOSpec extends UnitSpec {
  "Retrieving user trips" should "throw an exception" in {
    assertThrows[CollaboratorCallException] {
      new TripDAO().findTripsBy(UserBuilder.aUser().build())
    }
  }
}

class TripDAO() {
  def findTripsBy(user: User): List[Trip] = ???
}
```
- Implement the `TripDAO` method
```scala
class TripDAO() {
  def findTripsBy(user: User): List[Trip] = TripDAO.findTripsByUser(user)
}
```
- We can now work from our `TripServiceSpec` to inject this dependency
  - Use the `Change Signature` refactoring tool for that
  - Clean the tests
    - Remove the `createTripService` method
```scala
class TripServiceSpec extends UnitSpec with TryValues {
  ...
  private val tripDAOStub: TripDAO = stub[TripDAO]
  private val tripService: TripService = new TripService(tripDAOMock)
  ...
}
```
- Use the new dependency in our Service
```scala
class TripService(val tripDAO: TripDAO) {
  private def noTrips: List[Trip] = List.empty

  def getTripsByUser(user: User, loggedInUser: User): Try[Seq[Trip]] = {
    checkUser(loggedInUser) { loggedUser =>
      {
        if (user.isFriendWith(loggedUser))
          tripDAO.findTripsBy(user)
        else noTrips
      }
    }
  }

  private def checkUser(
      loggedInUser: User
  )(continueWith: User => List[Trip]): Try[List[Trip]] = {
    if (loggedInUser == null)
      Failure(new UserNotLoggedInException)
    else Success(continueWith(loggedInUser))
  }
}
```
- Run the tests
  - We now receive an error because we have not configured the `TripDAO` stub
![Mock no set up](img/mock-error.png)
- Setup our stub
```scala
  "Retrieving the trips by user" should "return friend trips when users are friends" in {
    val aUserWithTrips = UserBuilder
      .aUser()
      .friendsWith(anotherUser, registeredUser)
      .travelledTo(portugal, springfield)
      .build()

    (tripDAOStub.findTripsBy _)
      .when(aUserWithTrips)
      .returns(aUserWithTrips.trips())

    tripService
      .getTripsByUser(aUserWithTrips, registeredUser)
      .success
      .value
      .size should be(2)
  }
```

## Surface refactoring
Make some clean up and renames :
- Remove `TripServiceBackup` class
- Rename `getTripsByUser` into `getFriendTrips` for example
- Use `Seq` everywhere
- Remove usage of `MutableList` in `User`
```scala
class User(val trips: Seq[Trip], val friends: Seq[User]) {
  def addFriend(user: User): User =
    new User(trips, user +: friends)

  def addTrip(trip: Trip) =
    new User(trip +: trips, friends)

  def isFriendWith(anotherUser: User): Boolean =
    friends.contains(anotherUser)
}
```
- What is the impact on our tests ?
  - `None` thanks to the usage of our `UserBuilder`
  - We just have to adapt it
```scala
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
```

## What did we use / learn ?
- Seams
- Automated refactoring via our IDE
  - Extract Method
  - Change Signature
  - Rename
- Use `Higher Order Functions` to avoid duplication
- Use code coverage as a driver
- Test Data Builder
- Feature Envy
- Intro to TDD

Sources : 
- [Nicolas Carlo - understand legacy code](https://understandlegacycode.com/blog/key-points-of-working-effectively-with-legacy-code/#identify-seams-to-break-your-code-dependencies)
- [Sandro Mancuso - Testing legacy with Hard-wired dependencies](https://www.codurance.com/publications/2011/07/16/testing-legacy-hard-wired-dependencies)
- [Micheal Feathers - Working Effectively with Legacy Code](https://www.oreilly.com/library/view/working-effectively-with/0131177052/)

<a href="https://youtu.be/LSqbXorkyfQ" rel="Sandro's video">![Sandro's video](img/video.png)</a>
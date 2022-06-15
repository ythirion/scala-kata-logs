package cycles.slice2

import cycles.slice1.A

class B() {
  private def createA(): A = A(this)
}
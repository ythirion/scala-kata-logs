package org.craftedsw.tripservicekata.infrastructure

import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class UnitSpec
    extends AnyFlatSpec
    with Matchers
    with OptionValues
    with Inside
    with Inspectors
    with MockFactory

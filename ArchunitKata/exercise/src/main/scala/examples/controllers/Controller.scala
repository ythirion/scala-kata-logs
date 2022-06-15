package examples.controllers

import examples.models.ApiResponse

class Controller {
  def matching(): ApiResponse[Int] = new ApiResponse(42)

  def notMatching(): Unit = {

  }

  def universe(): Int = 42
}

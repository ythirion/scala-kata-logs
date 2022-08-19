package banking.domain

import java.time.LocalDateTime

trait Clock {
  def now(): LocalDateTime
}

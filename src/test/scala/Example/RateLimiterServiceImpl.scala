package Example

import RateLimiter.RateLimiterService

import scala.concurrent.duration._

trait RateLimiterServiceImpl extends RateLimiterService {

  def dictLimit: Long = 5
  def dictExpiry: Duration = 1 day
  def dictBlacklist: Boolean = false

  def bruteLimit: Long = 10
  def bruteExpiry: Duration = 10 minutes
  def bruteBlacklist: Boolean = false

  def ipLimit: Long = 50
  def ipExpiry: Duration = 2 minutes
  def ipBlacklist: Boolean = false

}

package Example

import RateLimiter.RateLimiterService

import scala.concurrent.duration._

trait RateLimiterServiceImpl extends RateLimiterService {

  override def dictLimit: Long = 5
  override def dictExpiry: Duration = 1 day
  override def dictBlacklist: Boolean = false

  override def bruteLimit: Long = 10
  override def bruteExpiry: Duration = 10 minutes
  override def bruteBlacklist: Boolean = false

  override def ipLimit: Long = 50
  override def ipExpiry: Duration = 2 minutes
  override def ipBlacklist: Boolean = false

  override def tagLimit(tag: String): Long = ipLimit
  override def tagExpiry(tag: String): Duration = ipExpiry
  override def tagBlacklist(tag: String): Boolean = ipBlacklist

}

package RateLimiter

import RateLimiter.RateLimiters._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait RateLimiterService {
  implicit def storage: RateLimiterStorage

  def dictLimit: Long
  def dictExpiry: Duration
  def dictBlacklist: Boolean

  def bruteLimit: Long
  def bruteExpiry: Duration
  def bruteBlacklist: Boolean

  def ipLimit: Long
  def ipExpiry: Duration
  def ipBlacklist: Boolean

  def authLimiter(ip: String, userIdentifier: String)(implicit executionContext: ExecutionContext): AuthLimiter = {
    AuthLimiter(ip, userIdentifier, dictLimit, dictExpiry.toMillis, dictBlacklist, bruteLimit, bruteExpiry.toMillis, bruteBlacklist)
  }

  def ipLimiter(ip: String)(implicit executionContext: ExecutionContext) : IPLimiter = {
    IPLimiter(ip, ipLimit, ipExpiry.toMillis, ipBlacklist)
  }

  def tagLimiter(tag: String, ip: String, limit: Long, expiry: Duration, blacklist: Boolean)(implicit executionContext: ExecutionContext): TagLimiter = {
    TagLimiter(tag, ip, limit, expiry.toMillis, blacklist)
  }

  def globalTagLimiter(tag: String, limit: Long, expiry: Duration)(implicit executionContext: ExecutionContext): GlobalTagLimiter = {
    GlobalTagLimiter(tag, limit, expiry.toMillis)
  }
}

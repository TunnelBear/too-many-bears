package RateLimiter

import RateLimiter.RateLimiters._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

// TODO: change blacklistOnBlock field to enableBlacklisting?
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

  def tagLimit(tag: String): Long
  def tagExpiry(tag: String): Duration
  def tagBlacklist(tag: String): Boolean

  def authLimiter(ip: String, userIdentifier: String)(implicit executionContext: ExecutionContext): AuthLimiter = {
    AuthLimiter(ip, userIdentifier, dictLimit, dictExpiry.toMillis, dictBlacklist, bruteLimit, bruteExpiry.toMillis, bruteBlacklist)
  }

  def ipLimiter(ip: String)(implicit executionContext: ExecutionContext): IPLimiter = {
    IPLimiter(ip, ipLimit, ipExpiry.toMillis, ipBlacklist)
  }

  def tagLimiter(tag: String, ip: String)(implicit executionContext: ExecutionContext): TagLimiter = {
    TagLimiter(tag, ip, tagLimit(tag), tagExpiry(tag).toMillis, tagBlacklist(tag))
  }

  // TODO: define separate methods for this?
  def globalTagLimiter(tag: String)(implicit executionContext: ExecutionContext): GlobalTagLimiter = {
    GlobalTagLimiter(tag, tagLimit(tag), tagExpiry(tag).toMillis)
  }
}

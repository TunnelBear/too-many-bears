package RateLimiter

import RateLimiter.RateLimiters.{AuthLimiter, IPLimiter, TagLimiter}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait RateLimiterService {
  implicit def storage: RateLimiterStorage

  def dictLimit: Long
  def dictExpiry: Duration

  def bruteLimit: Long
  def bruteExpiry: Duration

  def ipLimit: Long
  def ipExpiry: Duration


  def authLimiter(ip: String, userIdentifier: String)(implicit executionContext: ExecutionContext): AuthLimiter = {
    AuthLimiter(ip, userIdentifier, dictLimit, dictExpiry.toMillis, bruteLimit, bruteExpiry.toMillis)
  }

  def ipLimiter(ip: String)(implicit executionContext: ExecutionContext) : IPLimiter = {
    IPLimiter(ip, ipLimit, ipExpiry.toMillis)
  }

  def tagLimiter(tag: String, ip: String, limit: Long, expiry: Duration)(implicit executionContext: ExecutionContext): TagLimiter = {
    TagLimiter(tag, ip, limit, expiry.toMillis)
  }
}

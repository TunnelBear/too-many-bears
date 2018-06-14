package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.IPStrategy

case class IPLimiter(ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseRateLimiter {
  private final val Identifier = "IPLimiter"

  override def allow: Boolean = {
    IPStrategy(Identifier, ip, limit, expiry).allow
  }

  override def increment: Unit = {
    IPStrategy(Identifier, ip, limit, expiry).increment()
  }

}


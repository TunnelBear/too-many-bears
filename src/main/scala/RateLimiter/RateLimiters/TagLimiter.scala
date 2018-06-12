package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.TagStrategy

case class TagLimiter(tag: String, ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseRateLimiter {

  private final val Identifier = "TagLimiter"

  override def allow: Boolean = {
    TagStrategy(Identifier, tag, ip, limit, expiry).allow
  }

  override def increment: Unit = {
    TagStrategy(Identifier, tag, ip, limit, expiry).increment()
  }

}


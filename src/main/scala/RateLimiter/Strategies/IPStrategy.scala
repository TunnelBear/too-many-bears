package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests for a single ip
 */
case class IPStrategy(identifier: String, ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override def storage = rateLimiterStorage
}

package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests for a single ip
 */
case class IPStrategy(identifier: String, ip: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseIPStrategy {
  override def storage = rateLimiterStorage
}

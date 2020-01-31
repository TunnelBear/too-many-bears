package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests with this tag for all users
  Can be used to ratelimit specific actions for example
 */
case class GlobalStrategy(identifier: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  // Should never blacklist globally (i.e., block all users)
  override def blacklistOnBlock = false

  override def key: String = s"$identifier"
}

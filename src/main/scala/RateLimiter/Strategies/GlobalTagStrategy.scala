package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests with this tag for all users
  Can be used to ratelimit specific actions for example
 */
case class GlobalTagStrategy(identifier: String, tag: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  def key: String = s"$identifier:$tag"

  // Should never blacklist since that would effectively block all users
  override def blacklistOnBlock = false
}

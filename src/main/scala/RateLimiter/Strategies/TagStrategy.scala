package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests with this tag for a single ip.
  Can be used to ratelimit specific actions for example
 */
case class TagStrategy(identifier: String, tag: String, ip: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  def key: String = s"$identifier:$tag:$ip"
}

package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests with this tag for a single ip.
  Can be used to ratelimit specific actions for example
 */
case class TagStrategy(identifier: String, tag: String, ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseIPStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  override def key: String = s"$identifier:$tag:$ip"
}

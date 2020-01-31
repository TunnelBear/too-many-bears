package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of attempts on a single user
 */
case class BruteForceStrategy(identifier: String, ip: String, userIdentifier: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseIPStrategy {
  override def storage = rateLimiterStorage

  override def key: String = s"$identifier:$userIdentifier"
}

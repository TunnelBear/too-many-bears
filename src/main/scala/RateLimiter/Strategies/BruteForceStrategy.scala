package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of attempts on a single user
 */
case class BruteForceStrategy(identifier: String, ip: String, userIdentifier: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  def key: String = s"$identifier:$userIdentifier"
}

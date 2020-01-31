package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

import scala.concurrent.Future

/*
  Ratelimits based on number of requests with this tag for all users
  Can be used to ratelimit specific actions for example
 */
case class GlobalStrategy(identifier: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  override def blacklistOnBlock: Boolean = true

  override def key: String = s"$identifier"
}

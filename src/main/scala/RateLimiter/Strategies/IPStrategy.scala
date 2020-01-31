package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

case class IPStrategy(identifier: String, ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseIPStrategy {
  override def storage = rateLimiterStorage
}

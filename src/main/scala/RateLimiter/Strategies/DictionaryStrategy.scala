package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

import scala.concurrent.Future

/*
  Ratelimits based on a single IP attempting many different users
 */
case class DictionaryStrategy(identifier: String, ip: String, userIdentifier: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseIPStrategy {
  override implicit def storage: RateLimiterStorage = rateLimiterStorage

  override def blacklistOnBlock: Boolean = true

  override def increment: Future[Unit] = {
    storage.incrementCount(key, userIdentifier, expiry)
  }

}

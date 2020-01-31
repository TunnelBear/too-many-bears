package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

import scala.concurrent.{ExecutionContext, Future}


trait BaseStrategy {
  implicit def storage: RateLimiterStorage

  def identifier: String
  def limit: Long
  def expiry: Long
  def key: String
  def blacklistOnBlock: Boolean

  def allow(implicit executionContext: ExecutionContext): Future[Boolean] = {
    storage.getCount(key, expiry).map(_ < limit)
  }

  def increment(): Future[Unit] = {
    storage.incrementCount(key, System.currentTimeMillis.toString, expiry)
  }

  def blacklist(implicit executionContext: ExecutionContext): Future[Boolean] = {
    allow.map(!_ && blacklistOnBlock)
  }
}

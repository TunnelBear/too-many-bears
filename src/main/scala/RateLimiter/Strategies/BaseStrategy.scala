package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage
import RateLimiter.RateLimiterStatus._

import scala.concurrent.{ExecutionContext, Future}


trait BaseStrategy {
  implicit def storage: RateLimiterStorage

  def identifier: String
  def limit: Long
  def expiry: Long
  def key: String
  def blacklistOnBlock: Boolean

  def status(implicit executionContext: ExecutionContext): Future[RateLimiterStatus] = {
    storage.getCount(key, expiry).map { count =>
      println(s"CHECKING: $identifier, $count")
      if (count < limit) Allow
      else if (!blacklistOnBlock) Block
      else Blacklist
    }
  }

  def increment(): Future[Unit] = {
    storage.incrementCount(key, System.currentTimeMillis.toString, expiry)
  }
}

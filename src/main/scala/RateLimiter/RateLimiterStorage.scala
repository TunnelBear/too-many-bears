package RateLimiter

import scala.concurrent.Future

trait RateLimiterStorage {
  def incrementCount(key: String, value: String, expiry: Long): Future[Unit]
  def getCount(key: String, expiry: Long): Future[Long]
}

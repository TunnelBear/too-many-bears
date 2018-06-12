package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage


trait BaseStrategy {
  implicit def storage: RateLimiterStorage

  def identifier: String
  def ip: String
  def limit: Long
  def expiry: Long

  def key: String = s"$identifier:$ip"

  def allow: Boolean = {
    storage.getCount(key, expiry) < limit
  }

  def increment(): Unit = {
    storage.incrementCount(key, System.currentTimeMillis.toString, expiry)
  }
}

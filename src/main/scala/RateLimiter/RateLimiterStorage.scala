package RateLimiter

trait RateLimiterStorage {
  def incrementCount(key: String, value: String, expiry: Long): Unit
  def getCount(key: String, expiry: Long): Long
}

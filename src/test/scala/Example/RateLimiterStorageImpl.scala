package Example

import RateLimiter.RateLimiterStorage

// Implementation using in memory cache
object RateLimiterStorageImpl extends RateLimiterStorage {
  var Storage = Map[String, Map[String, Long]]()

  def incrementCount(key: String, value: String, expiry: Long) = {
    // Add new entry
    val entries = Storage.getOrElse(key, Map[String, Long]()) + (value -> System.currentTimeMillis)
    // Update storage
    Storage += (key -> entries)
  }

  def getCount(key: String, expiry: Long): Long = {
    val expires: Long = System.currentTimeMillis - expiry
    // non-expired entries
    val entries = Storage.getOrElse(key, Map[String, Long]()).filter(_._2 > expires)
    // Update storage to remove expired
    Storage += (key -> entries)
    entries.size
  }
}

package Example

import RateLimiter.RateLimiterStorage

import scala.concurrent.Future

// Implementation using in memory cache
object RateLimiterStorageImpl extends RateLimiterStorage {
  private var Storage = Map[String, Map[String, Long]]()

  def incrementCount(key: String, value: String, expiry: Long): Future[Unit] = {
    // Add new entry
    val entries = Storage.getOrElse(key, Map[String, Long]()) + (value -> System.currentTimeMillis)
    // Update storage
    Storage += (key -> entries)
    Future.successful(())
  }

  def getCount(key: String, expiry: Long): Future[Long] = {
    val expires: Long = System.currentTimeMillis - expiry
    // non-expired entries
    val entries = Storage.getOrElse(key, Map[String, Long]()).filter(_._2 > expires)
    // Update storage to remove expired
    Storage += (key -> entries)
    Future.successful(entries.size)
  }
}

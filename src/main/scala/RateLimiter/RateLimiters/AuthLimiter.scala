package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.{BruteForceStrategy, DictionaryStrategy}

case class AuthLimiter(ip: String, userIdentifier: String, dictLimit: Long, dictExpiry: Long, bruteLimit: Long, bruteExpiry: Long)(implicit rateLimiterStorage: RateLimiterStorage) extends BaseRateLimiter {

  private final val DictIdentifier = "DictAuthLimiter"
  private final val BruteIdentifier = "BruteAuthLimiter"

  private final val Strategies = List(
    DictionaryStrategy(DictIdentifier, ip, userIdentifier, dictLimit, dictExpiry),
    BruteForceStrategy(BruteIdentifier, ip, userIdentifier, bruteLimit, bruteExpiry)
  )

  override def allow: Boolean = {
    Strategies.forall(strategy => strategy.allow)
  }

  override def increment: Unit = {
    Strategies.foreach(strategy => strategy.increment())
  }
}


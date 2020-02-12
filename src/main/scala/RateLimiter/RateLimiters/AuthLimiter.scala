package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.{BruteForceStrategy, DictionaryStrategy}

import scala.concurrent.ExecutionContext

case class AuthLimiter(
  ip: String,
  userIdentifier: String,
  dictLimit: Long,
  dictExpiry: Long,
  dictBlacklist: Boolean,
  bruteLimit: Long,
  bruteExpiry: Long,
  bruteBlacklist: Boolean
)(implicit rateLimiterStorage: RateLimiterStorage, override val executionContext: ExecutionContext) extends StrategyRateLimiter {

  private final val DictIdentifier = "DictAuthLimiter"
  private final val BruteIdentifier = "BruteAuthLimiter"

  protected final override def strategies = Seq(
    DictionaryStrategy(DictIdentifier, ip, userIdentifier, dictLimit, dictExpiry, dictBlacklist),
    BruteForceStrategy(BruteIdentifier, ip, userIdentifier, bruteLimit, bruteExpiry, bruteBlacklist)
  )
}

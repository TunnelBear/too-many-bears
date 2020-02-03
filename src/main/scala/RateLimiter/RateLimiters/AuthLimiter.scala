package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.{BruteForceStrategy, DictionaryStrategy}

import scala.concurrent.{ExecutionContext, Future}

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

  protected final def strategies = Seq(
    DictionaryStrategy(DictIdentifier, ip, userIdentifier, dictLimit, dictExpiry, dictBlacklist),
    BruteForceStrategy(BruteIdentifier, ip, userIdentifier, bruteLimit, bruteExpiry, bruteBlacklist)
  )
}

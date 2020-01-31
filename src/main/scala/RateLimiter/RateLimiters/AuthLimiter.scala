package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.{BruteForceStrategy, DictionaryStrategy}

import scala.concurrent.{ExecutionContext, Future}

case class AuthLimiter(ip: String,
  userIdentifier: String,
  dictLimit: Long,
  dictExpiry: Long,
  blacklistOnBlockDict: Boolean,
  bruteLimit: Long,
  bruteExpiry: Long,
  blacklistOnBlockBrute: Boolean,
)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {

  private final val DictIdentifier = "DictAuthLimiter"
  private final val BruteIdentifier = "BruteAuthLimiter"

  private final val Strategies = Seq(
    DictionaryStrategy(DictIdentifier, ip, userIdentifier, dictLimit, dictExpiry, blacklistOnBlockDict),
    BruteForceStrategy(BruteIdentifier, ip, userIdentifier, bruteLimit, bruteExpiry, blacklistOnBlockBrute)
  )

  override def allow: Future[Boolean] = {
    Future.traverse(Strategies)(strategy => strategy.allow)
      .map(_.forall(identity))
  }

  override def increment: Future[Unit] = {
    Future.traverse(Strategies)(strategy => strategy.increment())
      .map(_.tail)
  }

  override def blacklist: Future[Boolean] = {
    Future.traverse(Strategies)(strategy => strategy.blacklist)
      .map(_.exists(identity))
  }
}


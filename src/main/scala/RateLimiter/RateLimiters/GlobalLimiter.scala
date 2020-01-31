package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.GlobalStrategy

import scala.concurrent.{ExecutionContext, Future}

case class GlobalLimiter(limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {
  private final val Identifier = "GlobalLimiter"

  override def allow: Future[Boolean] = GlobalStrategy(Identifier, limit, expiry).allow
  override def increment: Future[Unit] = GlobalStrategy(Identifier, limit, expiry).increment()
  override def blacklist: Future[Boolean] = GlobalStrategy(Identifier, limit, expiry).blacklist

}


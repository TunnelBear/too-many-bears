package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.GlobalTagStrategy

import scala.concurrent.{ExecutionContext, Future}

case class GlobalTagLimiter(tag: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {
  private final val Identifier = "GlobalTagLimiter"

  override def allow: Future[Boolean] = GlobalTagStrategy(Identifier, tag, limit, expiry).allow
  override def increment(): Future[Unit] = GlobalTagStrategy(Identifier, tag, limit, expiry).increment()
  override def blacklist: Future[Boolean] = GlobalTagStrategy(Identifier, tag, limit, expiry).blacklist

}


package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.TagStrategy

import scala.concurrent.{ExecutionContext, Future}

case class TagLimiter(tag: String, ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {

  private final val Identifier = "TagLimiter"

  override def allow: Future[Boolean] = {
    TagStrategy(Identifier, tag, ip, limit, expiry).allow
  }

  override def increment: Future[Unit] = {
    TagStrategy(Identifier, tag, ip, limit, expiry).increment()
  }

}


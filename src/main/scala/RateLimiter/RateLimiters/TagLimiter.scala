package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.TagStrategy

import scala.concurrent.{ExecutionContext, Future}

case class TagLimiter(tag: String, ip: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {
  private final val Identifier = "TagLimiter"

  override def allow: Future[Boolean] = TagStrategy(Identifier, tag, ip, limit, expiry, blacklistOnBlock).allow
  override def increment(): Future[Unit] = TagStrategy(Identifier, tag, ip, limit, expiry, blacklistOnBlock).increment()
  override def blacklist: Future[Boolean] = TagStrategy(Identifier, tag, ip, limit, expiry, blacklistOnBlock).blacklist

}


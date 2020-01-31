package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.IPStrategy

import scala.concurrent.{ExecutionContext, Future}

case class IPLimiter(ip: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {
  private final val Identifier = "IPLimiter"

  override def allow: Future[Boolean] = IPStrategy(Identifier, ip, limit, expiry, blacklistOnBlock).allow
  override def increment(): Future[Unit] = IPStrategy(Identifier, ip, limit, expiry, blacklistOnBlock).increment()
  override def blacklist: Future[Boolean] = IPStrategy(Identifier, ip, limit, expiry, blacklistOnBlock).blacklist

}

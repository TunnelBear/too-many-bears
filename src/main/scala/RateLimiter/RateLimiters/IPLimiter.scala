package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.{BaseIPStrategy, IPStrategy}

import scala.concurrent.{ExecutionContext, Future}

case class IPLimiter(ip: String, limit: Long, expiry: Long)(implicit rateLimiterStorage: RateLimiterStorage, executionContext: ExecutionContext) extends BaseRateLimiter {
  private final val Identifier = "IPLimiter"

  override def allow: Future[Boolean] = {
    IPStrategy(Identifier, ip, limit, expiry).allow
  }

  override def increment: Future[Unit] = {
    IPStrategy(Identifier, ip, limit, expiry).increment()
  }

  override def blacklist: Future[Boolean] = Future.successful(false)

}


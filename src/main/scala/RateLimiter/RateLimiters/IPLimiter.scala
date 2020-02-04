package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.IPStrategy

import scala.concurrent.ExecutionContext

case class IPLimiter(ip: String, limit: Long, expiry: Long, blacklistOnBlock: Boolean)(implicit rateLimiterStorage: RateLimiterStorage, override val executionContext: ExecutionContext) extends StrategyRateLimiter {
  private final val Identifier = s"IPLimiter"

  protected final override def strategies = Seq(
    IPStrategy(Identifier, ip, limit, expiry, blacklistOnBlock)
  )
}

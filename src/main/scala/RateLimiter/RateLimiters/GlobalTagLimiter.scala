package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.GlobalTagStrategy

import scala.concurrent.ExecutionContext

case class GlobalTagLimiter(
  tag: String,
  limit: Long,
  expiry: Long
)(implicit rateLimiterStorage: RateLimiterStorage, override val executionContext: ExecutionContext) extends StrategyRateLimiter {

  private final val Identifier = "GlobalTagLimiter"

  protected final override def strategies = Seq(
    GlobalTagStrategy(Identifier, tag, limit, expiry)
  )
}

package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStorage
import RateLimiter.Strategies.TagStrategy

import scala.concurrent.ExecutionContext

case class TagLimiter(
  tag: String,
  ip: String,
  limit: Long,
  expiry: Long,
  blacklistOnBlock: Boolean
)(implicit rateLimiterStorage: RateLimiterStorage, override val executionContext: ExecutionContext) extends StrategyRateLimiter {
  private final val Identifier = "TagLimiter"

  protected final override def strategies = Seq(
    TagStrategy(Identifier, tag, ip, limit, expiry, blacklistOnBlock)
  )
}

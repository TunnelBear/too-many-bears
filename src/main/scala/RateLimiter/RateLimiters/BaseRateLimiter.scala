package RateLimiter.RateLimiters

import RateLimiter.Strategies.BaseStrategy

import scala.concurrent.Future

trait BaseRateLimiter {
  def allow: Future[Boolean]
  def increment(): Future[Unit]
  def blacklist: Future[Boolean]

  protected def strategies: Seq[BaseStrategy]
}

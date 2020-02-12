package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStatus.RateLimiterStatus

import scala.concurrent.Future

trait BaseRateLimiter {
  def status: Future[RateLimiterStatus]
  def increment(): Future[Unit]
  def statusWithIncrement(): Future[RateLimiterStatus]
}

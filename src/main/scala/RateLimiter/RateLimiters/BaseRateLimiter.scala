package RateLimiter.RateLimiters

import RateLimiter.RateLimitingStatus.RateLimitingStatus

import scala.concurrent.Future

trait BaseRateLimiter {
  def allow: Future[Boolean]
  def increment(): Future[Unit]
  def blacklist: Future[Boolean]
  def checkAndIncrement(): Future[RateLimitingStatus]
}

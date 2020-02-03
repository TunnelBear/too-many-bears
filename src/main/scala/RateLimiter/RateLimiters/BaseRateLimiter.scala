package RateLimiter.RateLimiters

import scala.concurrent.Future

trait BaseRateLimiter {
  def allow: Future[Boolean]
  def increment(): Future[Unit]
  def blacklist: Future[Boolean]
}

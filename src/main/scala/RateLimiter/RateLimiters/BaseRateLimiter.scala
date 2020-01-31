package RateLimiter.RateLimiters

import scala.concurrent.{ExecutionContext, Future}

trait BaseRateLimiter {
  def allow: Future[Boolean]
  def increment(): Future[Unit]
  def blacklist: Future[Boolean]

  // TODO: better name?
  def allowAndIncrement()(implicit executionContext: ExecutionContext): Future[Boolean] = {
    allow.map { allowed =>
      if (allowed) increment()
      allowed
    }
  }
}

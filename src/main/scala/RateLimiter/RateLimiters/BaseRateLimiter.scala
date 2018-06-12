package RateLimiter.RateLimiters

trait BaseRateLimiter {
  def allow: Boolean
  def increment: Unit
}

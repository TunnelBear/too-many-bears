package RateLimiter

object RateLimitingStatus extends Enumeration {
  type RateLimitingStatus = Value

  val Allowed: RateLimitingStatus = Value("Allowed")
  val Blacklisted: RateLimitingStatus = Value("Blacklisted")
  val RateLimited: RateLimitingStatus = Value("RateLimited")
}

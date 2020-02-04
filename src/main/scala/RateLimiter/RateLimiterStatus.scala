package RateLimiter

object RateLimiterStatus extends Enumeration {
  type RateLimiterStatus = Value

  val Allow: RateLimiterStatus = Value("Allow")
  val Block: RateLimiterStatus = Value("Block")
  val Blacklist: RateLimiterStatus = Value("Blacklist")
}

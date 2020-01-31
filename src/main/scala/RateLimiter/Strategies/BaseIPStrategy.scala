package RateLimiter.Strategies

import RateLimiter.RateLimiterStorage

/*
  Ratelimits based on number of requests for a single ip
 */
trait BaseIPStrategy extends BaseStrategy {

  def ip: String

  override def key: String = s"$identifier:$ip"

}

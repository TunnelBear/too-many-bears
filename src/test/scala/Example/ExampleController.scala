package Example

import RateLimiter.RateLimiters.TagLimiter

import scala.concurrent.duration._

class ExampleController extends RateLimitedController {

  def someAuthAction(ip: String, email: String): Int = {
    if (!authLimiter(ip, email).allow) return 429
    else authLimiter(ip, email).increment

    // do actions
    200
  }

  def someSpecificAction(ip: String): Int = {
    val limiter: TagLimiter = tagLimiter("specific", ip, 10, 1 minute)

    if (!limiter.allow) return 429
    else limiter.increment

    // do stuff
    200
  }
}

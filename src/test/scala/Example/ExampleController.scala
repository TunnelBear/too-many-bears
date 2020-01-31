package Example

import RateLimiter.RateLimiters.{AuthLimiter, TagLimiter}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExampleController extends RateLimitedController {

  def someAuthAction(ip: String, email: String): Future[Int] = {
    val limiter: AuthLimiter = authLimiter(ip, email)

    // Note that you wouldn't need to explicitly wrap your action with beforeAction if you were using one of Play's
    // action composition design patterns (e.g., Stackable Controller)
    beforeAction(ip) {
      limiter.allowAndIncrement().map { allowed =>
        if (!allowed) 429
        else {
          // do stuff
          200
        }
      }
    }
  }

  def someSpecificAction(ip: String): Future[Int] = {
    val limiter: TagLimiter = tagLimiter("specific", ip, 10, 1 minute, blacklist = false)

    beforeAction(ip) {
      limiter.allowAndIncrement().map { allowed =>
        if (!allowed) 429
        else {
          // do stuff
          200
        }
      }
    }
  }
}

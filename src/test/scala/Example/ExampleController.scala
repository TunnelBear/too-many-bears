package Example

import RateLimiter.RateLimiters.{AuthLimiter, TagLimiter}
import RateLimiter.RateLimiterStatus._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExampleController extends RateLimitedController {

  private val Action1 = "Action1"
  private val Action2 = "Action2"

  // Example of defining a rate limit depending on the action
  override def tagLimit(tag: String): Long = tag match {
    case `Action1` => 10
    case `Action2` => 20
    case _ => super.tagLimit(tag)
  }

  def someAuthAction(ip: String, email: String): Future[Int] = {
    val limiter: AuthLimiter = authLimiter(ip, email)

    // Note that you wouldn't need to explicitly wrap your action with beforeAction if you were using one of Play's
    // action composition design patterns (e.g., Stackable Controller)
    beforeAction(ip) {
      limiter.statusWithIncrement().map {
        case Allow =>
          // do stuff
          200
        case _ => 429
      }
    }
  }

  def someSpecificAction(ip: String): Future[Int] = {
    val limiter: TagLimiter = tagLimiter(Action1, ip)

    beforeAction(ip) {
      limiter.statusWithIncrement().map {
        case Allow =>
          // do stuff
          200
        case _ => 429
      }
    }
  }
}

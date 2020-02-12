package Example

import RateLimiter.RateLimiterStorage
import RateLimiter.RateLimiters.IPLimiter
import RateLimiter.RateLimiterStatus._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RateLimitedController extends RateLimiterServiceImpl {
  override implicit def storage: RateLimiterStorage = RateLimiterStorageImpl

  // this theoretical function would wrap all requests inherited from RateLimitedController
  def beforeAction(ip: String)(f: => Future[Int]): Future[Int] = {
    val limiter: IPLimiter = ipLimiter(ip)

    // TODO: note that if blacklisting is enabled, the case isn't handled.
    limiter.statusWithIncrement().flatMap {
      case Allow => f
      case Block => Future.successful(429)
    }
  }
}

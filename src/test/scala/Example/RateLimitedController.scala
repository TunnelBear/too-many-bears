package Example

import RateLimiter.RateLimiterStorage
import RateLimiter.RateLimiters.IPLimiter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RateLimitedController extends RateLimiterServiceImpl {
  override implicit def storage: RateLimiterStorage = RateLimiterStorageImpl

  // this theoretical function would wrap all requests inherited from RateLimitedController
  def beforeAction(ip: String)(f: => Future[Int]): Future[Int] = {
    val limiter: IPLimiter = ipLimiter(ip)

    limiter.allow.flatMap { allowed =>
      if (!allowed) Future.successful(429)
      else {
        limiter.increment()
        f
      }
    }
  }

}

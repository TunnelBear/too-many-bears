package RateLimiter.Example
import RateLimiter.RateLimiterStorage

class RateLimitedController extends RateLimiterServiceImpl {
  override implicit def storage: RateLimiterStorage = RateLimiterStorageImpl

  // this theoretical function would wrap all requests inherited from RateLimitedController
  def beforeAction(ip: String): Int = {
    if (!ipLimiter(ip).allow) return 429
    else ipLimiter(ip).increment

    200
  }

}

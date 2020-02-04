package RateLimiter.RateLimiters

import RateLimiter.RateLimiterStatus._
import RateLimiter.Strategies.BaseStrategy

import scala.concurrent.{ExecutionContext, Future}

trait StrategyRateLimiter extends BaseRateLimiter {
  protected def strategies: Seq[BaseStrategy]
  implicit val executionContext: ExecutionContext

  override def status: Future[RateLimiterStatus] = {
    Future
      .traverse(strategies)(strategy => strategy.status)
      .map(_.fold(Allow) {
        case (Allow, status) => status
        case (Block, status) => if (status != Allow) status else Block
        case (Blacklist, _) => Blacklist
      })
  }

  override def increment(): Future[Unit] = {
    Future.traverse(strategies)(strategy => strategy.increment())
      .map(_.tail)
  }

  // TODO: does this logic make sense, and is it intuitive? Should this logic live here?
  override def statusWithIncrement(): Future[RateLimiterStatus] = {
    status.map {
      case Allow =>
        increment()
        Allow
      case status => status
    }
  }
}

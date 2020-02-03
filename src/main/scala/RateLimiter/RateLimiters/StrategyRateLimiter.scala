package RateLimiter.RateLimiters

import RateLimiter.Strategies.BaseStrategy

import scala.concurrent.{ExecutionContext, Future}

trait StrategyRateLimiter extends BaseRateLimiter {
  protected def strategies: Seq[BaseStrategy]
  implicit val executionContext: ExecutionContext

  override def allow: Future[Boolean] = {
    Future.traverse(strategies)(strategy => strategy.allow)
      .map(_.forall(identity))
  }

  override def increment(): Future[Unit] = {
    Future.traverse(strategies)(strategy => strategy.increment())
      .map(_.tail)
  }

  override def blacklist: Future[Boolean] = {
    Future.traverse(strategies)(strategy => strategy.blacklist)
      .map(_.exists(identity))
  }
}

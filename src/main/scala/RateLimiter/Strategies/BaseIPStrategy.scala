package RateLimiter.Strategies


trait BaseIPStrategy extends BaseStrategy {

  def ip: String

  override def key: String = s"$identifier:$ip"

}

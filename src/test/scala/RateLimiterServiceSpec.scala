import RateLimiter.{RateLimiterService, RateLimiterStorage}
import RateLimiter.RateLimiterStatus._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable._

import scala.concurrent.Future
import scala.concurrent.duration._

class RateLimiterServiceSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  val ip = "123.123.123.123"
  val userIdentifier = "michael@tunnelbear.com"

  val ipLimiterKey = s"IPLimiter:$ip"
  val dictLimiterKey = s"DictAuthLimiter:$ip"
  val bruteLimiterKey = s"BruteAuthLimiter:$userIdentifier"

  val tag = "tag1"
  val tagWithBlacklist = "tag2"
  val tagLimiterKey = s"TagLimiter:$tag:$ip"
  val tagLimiterKeyBlacklist = s"TagLimiter:$tagWithBlacklist:$ip"

  val globalTag1 = "tag"
  val globalTag2 = "globalTag2"
  val globalTagLimiterKey1 = s"GlobalTagLimiter:$globalTag1"
  val globalTagLimiterKey2 = s"GlobalTagLimiter:$globalTag2"

  trait RateLimiterServiceImpl extends RateLimiterService {

    override def dictLimit: Long = 10
    override def dictExpiry: Duration = 1 minute
    override def dictBlacklist: Boolean = false

    override def bruteLimit: Long = 20
    override def bruteExpiry: Duration = 2 minutes
    override def bruteBlacklist: Boolean = false

    override def ipLimit: Long = 30
    override def ipExpiry: Duration = 3 minutes
    override def ipBlacklist: Boolean = false

    override def tagLimit(t: String): Long = t match {
      case `tag` => 40
      case `tagWithBlacklist` => 50
      case _ => 60
    }
    override def tagExpiry(t: String): Duration = t match {
      case `tag` => 4 minutes
      case `tagWithBlacklist` => 5 minutes
      case _ => 6 minutes
    }
    override def tagBlacklist(t: String): Boolean = t match {
      case `tag` => false
      case `tagWithBlacklist` => true
      case _ => false
    }

  }

  // TODO: test status and statusWithIncrement
  "RateLimiterServiceTestImpl provides an IPLimiter" should {

    "allows request that does not exceed given rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(1)

      rls.ipLimiter(ip).status must be_==(Allow).await
    }

    "blocks request that exceeds given rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

      rls.ipLimiter(ip).status must be_==(Block).await
    }

    "blacklist user if request is blocked and blacklisting is enabled" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl {
        override implicit def storage: RateLimiterStorage = mockStorage
        override def ipBlacklist = true
      }
      mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

      rls.ipLimiter(ip).status must be_==(Blacklist).await
    }

  }



  "RateLimiterServiceTestImpl provides an AuthLimiter" should {

    "allows request that does not exceed given rates" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
      mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

      rls.authLimiter(ip, userIdentifier).status must be_==(Allow).await
    }

    "blocks request that exceeds dict rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
      mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

      rls.authLimiter(ip, userIdentifier).status must be_==(Block).await
    }

    "blocks request that exceeds brute rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
      mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(rls.bruteLimit)

      rls.authLimiter(ip, userIdentifier).status must be_==(Block).await
    }

    "blacklist a user if request is blocked and blacklisting is enabled" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl {
        override implicit def storage: RateLimiterStorage = mockStorage
        override def dictBlacklist = true
      }
      mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
      mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

      rls.authLimiter(ip, userIdentifier).status must be_==(Blacklist).await
    }

  }



  "RateLimiterServiceTestImpl provides a TagLimiter" should {

    "allows request that does not exceed given rates" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }

      mockStorage.getCount(tagLimiterKey, rls.tagExpiry(tag).toMillis) returns Future.successful(1)
      mockStorage.getCount(tagLimiterKeyBlacklist, rls.tagExpiry(tagWithBlacklist).toMillis) returns Future.successful(1)

      rls.tagLimiter(tag, ip).status must be_==(Allow).await
      rls.tagLimiter(tagWithBlacklist, ip).status must be_==(Allow).await
    }

    "blocks request that exceeds given rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(tagLimiterKey, rls.tagExpiry(tag).toMillis) returns Future.successful(rls.tagLimit(tag))
      mockStorage.getCount(tagLimiterKeyBlacklist, rls.tagExpiry(tagWithBlacklist).toMillis) returns Future.successful(1)

      rls.tagLimiter(tag, ip).status must be_==(Block).await
      rls.tagLimiter(tagWithBlacklist, ip).status must be_==(Allow).await
    }

    "blacklist a user if request is blocked and blacklisting is enabled" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(tagLimiterKey, rls.tagExpiry(tag).toMillis) returns Future.successful(1)
      mockStorage.getCount(tagLimiterKeyBlacklist, rls.tagExpiry(tagWithBlacklist).toMillis) returns Future.successful(rls.tagLimit(tagWithBlacklist))

      rls.tagLimiter(tag, ip).status must be_==(Allow).await
      rls.tagLimiter(tagWithBlacklist, ip).status must be_==(Blacklist).await
    }

  }



  "RateLimiterServiceTestImpl provides a GlobalTagLimiter" should {

    "allows request that does not exceed given rates" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(globalTagLimiterKey1, rls.tagExpiry(globalTag1).toMillis) returns Future.successful(1)
      mockStorage.getCount(globalTagLimiterKey2, rls.tagExpiry(globalTag2).toMillis) returns Future.successful(1)

      rls.globalTagLimiter(globalTag1).status must be_==(Allow).await
      rls.globalTagLimiter(globalTag2).status must be_==(Allow).await
    }

    "blocks request that exceeds given rate" in {
      val mockStorage = mock[RateLimiterStorage]
      val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
      mockStorage.getCount(globalTagLimiterKey1, rls.tagExpiry(globalTag1).toMillis) returns Future.successful(rls.tagLimit(globalTag1))
      mockStorage.getCount(globalTagLimiterKey2, rls.tagExpiry(globalTag2).toMillis) returns Future.successful(1)

      rls.globalTagLimiter(globalTag1).status must be_==(Block).await
      rls.globalTagLimiter(globalTag2).status must be_==(Allow).await
    }

  }

}

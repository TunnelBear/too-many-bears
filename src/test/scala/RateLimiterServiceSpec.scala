import RateLimiter.{RateLimiterService, RateLimiterStorage}
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

  val tag1 = "tag1"
  val tag2 = "tag2"
  val tagLimiterKey1 = s"TagLimiter:$tag1:$ip"
  val tagLimiterKey2 = s"TagLimiter:$tag2:$ip"
  val globalTagLimiterKey1 = s"GlobalTagLimiter:$tag1"
  val globalTagLimiterKey2 = s"GlobalTagLimiter:$tag2"

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

    override def tagLimit(tag: String): Long = tag match {
      case `tag1` => 40
      case `tag2` => 50
    }
    override def tagExpiry(tag: String): Duration = tag match {
      case `tag1` => 4 minutes
      case `tag2` => 5 minutes
    }
    override def tagBlacklist(tag: String): Boolean = tag match {
      case `tag1` => true
      case `tag2` => false
    }

  }


    "RateLimiterServiceTestImpl provides an IPLimiter" should {

      "allows request that does not exceed given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(1)

        rls.ipLimiter(ip).allow must be_==(true).await // (allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).allow must be_==(true).await // (allow => assert(!allow))
      }

      "not blacklist user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).blacklist must be_==(true).await // (blacklist => assert(!blacklist))
      }

      "blacklist user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl {
          override implicit def storage: RateLimiterStorage = mockStorage
          override def ipBlacklist = true
        }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).blacklist must be_==(true).await // (blacklist => assert(blacklist))
      }

    }



    "RateLimiterServiceTestImpl provides an AuthLimiter" should {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).allow must be_==(true).await // (allow => assert(allow))
      }

      "blocks request that exceeds dict rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).allow must be_==(true).await // (allow => assert(!allow))
      }

      "blocks request that exceeds brute rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(rls.bruteLimit)

        rls.authLimiter(ip, userIdentifier).allow must be_==(true).await // (allow => assert(!allow))
      }

      "not blacklist a user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).blacklist must be_==(true).await // (blacklist => assert(!blacklist))
      }

      "blacklist a user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl {
          override implicit def storage: RateLimiterStorage = mockStorage
          override def dictBlacklist = true
        }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).blacklist must be_==(true).await // (blacklist => assert(blacklist))
      }

    }



    "RateLimiterServiceTestImpl provides a TagLimiter" should {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }

        mockStorage.getCount(tagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(1)
        mockStorage.getCount(tagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip).allow must be_==(true).await // (allow => assert(allow))
        rls.tagLimiter(tag2, ip).allow must be_==(true).await // (allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(rls.tagLimit(tag1))
        mockStorage.getCount(tagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip).allow must be_==(true).await // (allow => assert(!allow))
        rls.tagLimiter(tag2, ip).allow must be_==(true).await // (allow => assert(allow))
      }

      "not blacklist a user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(rls.tagLimit(tag1))
        mockStorage.getCount(tagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip).blacklist must be_==(true).await // (blacklist => assert(!blacklist))
        rls.tagLimiter(tag2, ip).blacklist must be_==(true).await // (blacklist => assert(!blacklist))
      }

      "blacklist a user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(rls.tagLimit(tag1))
        mockStorage.getCount(tagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip).blacklist must be_==(true).await // (blacklist => assert(blacklist))
        rls.tagLimiter(tag2, ip).blacklist must be_==(true).await // (blacklist => assert(!blacklist))
      }

    }



    "RateLimiterServiceTestImpl provides a GlobalTagLimiter" should {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(globalTagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(1)
        mockStorage.getCount(globalTagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.globalTagLimiter(tag1).allow must be_==(true).await // (allow => assert(allow))
        rls.globalTagLimiter(tag2).allow must be_==(true).await // (allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(globalTagLimiterKey1, rls.tagExpiry(tag1).toMillis) returns Future.successful(rls.tagLimit(tag1))
        mockStorage.getCount(globalTagLimiterKey2, rls.tagExpiry(tag2).toMillis) returns Future.successful(1)

        rls.globalTagLimiter(tag1).allow must be_==(true).await // (allow => assert(!allow))
        rls.globalTagLimiter(tag2).allow must be_==(true).await // (allow => assert(allow))
      }

    }

  }

}

import RateLimiter.{RateLimiterService, RateLimiterStorage}
import org.mockito.IdiomaticMockito
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.concurrent.duration._

class RateLimiterServiceSpec extends AsyncWordSpec with IdiomaticMockito {

  val ip = "123.123.123.123"
  val userIdentifier = "michael@tunnelbear.com"

  val ipLimiterKey = s"IPLimiter:$ip"
  val dictLimiterKey = s"DictAuthLimiter:$ip"
  val bruteLimiterKey = s"BruteAuthLimiter:$userIdentifier"

  val tagExpiry: Duration = 4 minutes
  val tagLimit = 40
  val tag1 = "tag1"
  val tag2 = "tag2"

  val tagLimiterKeyA = s"TagLimiter:$tag1:$ip"
  val tagLimiterKeyB = s"TagLimiter:$tag2:$ip"
  val globalTagLimiterKeyA = s"GlobalTagLimiter:$tag1"
  val globalTagLimiterKeyB = s"GlobalTagLimiter:$tag2"

  trait RateLimiterServiceImpl extends RateLimiterService {

    def dictLimit: Long = 10
    def dictExpiry: Duration = 1 minute
    def dictBlacklist: Boolean = false

    def bruteLimit: Long = 20
    def bruteExpiry: Duration = 2 minutes
    def bruteBlacklist: Boolean = false

    def ipLimit: Long = 30
    def ipExpiry: Duration = 3 minutes
    def ipBlacklist: Boolean = false

  }

  "RateLimiterServiceImpl" should {

    "provide an IPLimiter" which {

      "allows request that does not exceed given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(1)

        rls.ipLimiter(ip).allow.map(allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).allow.map(allow => assert(!allow))
      }

      "not blacklist user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).blacklist.map(blacklist => assert(!blacklist))
      }

      "blacklist user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl {
          override implicit def storage: RateLimiterStorage = mockStorage
          override def ipBlacklist = true
        }
        mockStorage.getCount(ipLimiterKey, rls.ipExpiry.toMillis) returns Future.successful(rls.ipLimit)

        rls.ipLimiter(ip).blacklist.map(blacklist => assert(blacklist))
      }

    }



    "provide an AuthLimiter" which {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).allow.map(allow => assert(allow))
      }

      "blocks request that exceeds dict rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).allow.map(allow => assert(!allow))
      }

      "blocks request that exceeds brute rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(rls.bruteLimit)

        rls.authLimiter(ip, userIdentifier).allow.map(allow => assert(!allow))
      }

      "not blacklist a user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).blacklist.map(blacklist => assert(!blacklist))
      }

      "blacklist a user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl {
          override implicit def storage: RateLimiterStorage = mockStorage
          override def dictBlacklist = true
        }
        mockStorage.getCount(dictLimiterKey, rls.dictExpiry.toMillis) returns Future.successful(rls.dictLimit)
        mockStorage.getCount(bruteLimiterKey, rls.bruteExpiry.toMillis) returns Future.successful(1)

        rls.authLimiter(ip, userIdentifier).blacklist.map(blacklist => assert(blacklist))
      }

    }



    "provide a TagLimiter" which {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(tagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip, tagLimit, tagExpiry, blacklist = false).allow.map(allow => assert(allow))
        rls.tagLimiter(tag2, ip, tagLimit, tagExpiry, blacklist = false).allow.map(allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(tagLimit)
        mockStorage.getCount(tagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip, tagLimit, tagExpiry, blacklist = false).allow.map(allow => assert(!allow))
        rls.tagLimiter(tag2, ip, tagLimit, tagExpiry, blacklist = false).allow.map(allow => assert(allow))
      }

      "not blacklist a user if request is blocked and blacklisting is disabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(tagLimit)
        mockStorage.getCount(tagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip, tagLimit, tagExpiry, blacklist = false).blacklist.map(blacklist => assert(!blacklist))
        rls.tagLimiter(tag2, ip, tagLimit, tagExpiry, blacklist = false).blacklist.map(blacklist => assert(!blacklist))
      }

      "blacklist a user if request is blocked and blacklisting is enabled" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(tagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(tagLimit)
        mockStorage.getCount(tagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.tagLimiter(tag1, ip, tagLimit, tagExpiry, blacklist = false).blacklist.map(blacklist => assert(blacklist))
        rls.tagLimiter(tag2, ip, tagLimit, tagExpiry, blacklist = false).blacklist.map(blacklist => assert(!blacklist))
      }

    }



    "provide a GlobalTagLimiter" which {

      "allows request that does not exceed given rates" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(globalTagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(1)
        mockStorage.getCount(globalTagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.globalTagLimiter(tag1, tagLimit, tagExpiry).allow.map(allow => assert(allow))
        rls.globalTagLimiter(tag2, tagLimit, tagExpiry).allow.map(allow => assert(allow))
      }

      "blocks request that exceeds given rate" in {
        val mockStorage = mock[RateLimiterStorage]
        val rls = new RateLimiterServiceImpl { override implicit def storage: RateLimiterStorage = mockStorage }
        mockStorage.getCount(globalTagLimiterKeyA, tagExpiry.toMillis) returns Future.successful(tagLimit)
        mockStorage.getCount(globalTagLimiterKeyB, tagExpiry.toMillis) returns Future.successful(1)

        rls.globalTagLimiter(tag1, tagLimit, tagExpiry).allow.map(allow => assert(!allow))
        rls.globalTagLimiter(tag2, tagLimit, tagExpiry).allow.map(allow => assert(allow))
      }

    }

  }

}

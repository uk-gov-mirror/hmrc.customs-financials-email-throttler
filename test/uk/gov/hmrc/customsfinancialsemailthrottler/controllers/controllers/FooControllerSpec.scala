package uk.gov.hmrc.customsfinancialsemailthrottler.controllers.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.customsfinancialsemailthrottler.config.AppConfig
import uk.gov.hmrc.customsfinancialsemailthrottler.controllers.FooController
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}

class FooControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest("GET", "/")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  private val controller = new FooController(appConfig, Helpers.stubControllerComponents())

  "GET /" should {
    "return 200" in {
      val result = controller.scheduleEmail()(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

//  "POST /" should {
//    "return 200" in {
//      val result = controller.scheduleEmail()(fakeRequest)
//      status(result) shouldBe Status.OK
//    }
//  }
}

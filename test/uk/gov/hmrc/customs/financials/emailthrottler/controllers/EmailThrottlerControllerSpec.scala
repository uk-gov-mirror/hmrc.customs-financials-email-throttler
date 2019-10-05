/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customs.financials.emailthrottler.controllers

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.verify
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, Helpers}
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.services.EmailQueue

//noinspection TypeAnnotation
class EmailThrottlerControllerSpec extends WordSpec with Matchers with MockitoSugar {

  class EmailThrottlerScenario() {
    val requestBody = Json.parse(
      """{
        | "to": ["email1@example.co.uk", "email1@example.co.uk"],
        | "templateId": "template_for_duty_deferment_email",
        | "parameters": {
        |   "param1": "value1",
        |   "param2": "value2"
        | },
        | "force": false,
        | "eventUrl": "event.url.co.uk",
        | "onSendUrl": "on.send.url.co.uk"
        |}""".stripMargin)
    val fakeRequest = FakeRequest("POST", "/", FakeHeaders(), requestBody)
    val mockAppconfig = mock[AppConfig]
    val mockEmailQueue = mock[EmailQueue]

    val controller = new EmailThrottlerController(mockEmailQueue, mockAppconfig, Helpers.stubControllerComponents())
  }


  "the controller" should {

    "handle enqueue request" in new EmailThrottlerScenario {
      val result = controller.enqueueEmail()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "ask EmailQueue service to store emails" in new EmailThrottlerScenario {
      await(controller.enqueueEmail()(fakeRequest))
      verify(mockEmailQueue).enqueue(ArgumentMatchers.any())
    }
  }

}

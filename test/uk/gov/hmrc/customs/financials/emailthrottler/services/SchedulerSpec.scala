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

package uk.gov.hmrc.customs.financials.emailthrottler.services

import akka.actor.ActorSystem
import org.mockito.Mockito.{verify, when}
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig

class SchedulerSpec extends WordSpec with MockitoSugar with Matchers {

  "Scheduler" should {

    import scala.concurrent.ExecutionContext.Implicits.global

    "schedule email sending" in {

      val mockAppConfig = mock[AppConfig]
      when(mockAppConfig.numberOfEmailsPerSecond).thenReturn(0.2)
      val mockEmailJobHandler = mock[EmailJobHandler]
      val mockActorSystem = mock[ActorSystem]
      val mockScheduler = mock[akka.actor.Scheduler]
      when(mockActorSystem.scheduler).thenReturn(mockScheduler)

      new Scheduler(mockAppConfig, mockEmailJobHandler, mockActorSystem)

      // TODO: fix test, mockito fail to match call by name arguments :(
//      verify(mockScheduler.schedule(
//        ArgumentMatchers.eq(0 seconds),
//        ArgumentMatchers.eq(5 second))
//      (ArgumentMatchers.anyObject())
//      (ArgumentMatchers.any())
//      )

      verify(mockActorSystem).scheduler
    }

  }

}

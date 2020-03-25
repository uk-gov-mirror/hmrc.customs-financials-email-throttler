/*
 * Copyright 2020 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{times, verify, when}

import scala.concurrent.duration._
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits

class SchedulerSpec extends WordSpec with MockitoSugar with Matchers {

  "Scheduler" should {

    "schedule email sending" in {
      implicit val ec = Implicits.global
      val mockAppConfig = mock[AppConfig]
      when(mockAppConfig.emailsPerInstancePerSecond).thenReturn(0.2)
      val mockEmailJobHandler = mock[EmailJobHandler]
      val mockActorSystem = mock[ActorSystem]
      val mockScheduler = mock[akka.actor.Scheduler]

      when(mockActorSystem.scheduler).thenReturn(mockScheduler)
      when(mockEmailJobHandler.processJob()).thenReturn(Future[Unit]({}))

      new Scheduler(mockAppConfig, mockEmailJobHandler, mockActorSystem)

      verify(mockScheduler).schedule(ArgumentMatchers.eq(0 seconds), ArgumentMatchers.eq(5 seconds), ArgumentMatchers.any[Runnable]())(ArgumentMatchers.eq(ec))
    }
  }
}

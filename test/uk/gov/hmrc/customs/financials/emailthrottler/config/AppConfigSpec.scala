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

package uk.gov.hmrc.customs.financials.emailthrottler.config

import org.scalatest.{Matchers, WordSpec}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}

class AppConfigSpec extends WordSpec with Matchers {

  val environment: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(environment)
  val runMode = new RunMode(configuration, environment.mode)
  val servicesConfig = new ServicesConfig(configuration, runMode)
  val appConfig = new AppConfig(configuration, servicesConfig)

  "AppConfig" should {

    "have settings for auditing" in {
      appConfig.auditingEnabled shouldBe true
    }

    "have settings for graphite" in {
      appConfig.graphiteHost shouldBe "graphite"
    }

  }

}

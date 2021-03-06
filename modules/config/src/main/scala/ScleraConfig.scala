/**
* Sclera - Configuration Manager
* Copyright 2012 - 2020 Sclera, Inc.
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

package com.scleradb.config

import java.io.File

import org.slf4j.{Logger, LoggerFactory}
import com.typesafe.config._

import scala.jdk.CollectionConverters._

object ScleraConfig {
    private val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)

    private val defaultConfig: Config = ConfigFactory.defaultReference()

    val configFileOpt: Option[File] =
        try Option(defaultConfig.getString("sclera.app.conf")).map(new File(_))
        catch { case (_: ConfigException.Missing) => None }

    private val config: Config = configFileOpt match {
        case Some(f) if f.exists =>
            ConfigFactory.load(ConfigFactory.parseFile(f))
        case _ => ConfigFactory.load("sclera")
    }

    def configFile: String = config.origin().description()

    private def getStringList(key: String): List[String] =
        try config.getStringList(key).asScala.toList
        catch { case (_: ConfigException.Missing) => Nil }

    private def getStringOpt(key: String): Option[String] =
        try Option(config.getString(key))
        catch { case (_: ConfigException.Missing) => None }

    private def getBooleanOpt(key: String): Option[Boolean] =
        try Option(config.getBoolean(key))
        catch { case (_: ConfigException.Missing) => None }

    private def getIntOpt(key: String): Option[Int] =
        try Option(config.getInt(key))
        catch { case (_: ConfigException.Missing) => None }

    private def getDbConfig(path: String): List[(String, String)] =
        try config.getConfig(path).entrySet.asScala.toList.map { e =>
            val key: String = e.getKey()
            val value: String = e.getValue().unwrapped().toString

            (key, value)
        } catch { case (_: ConfigException.Missing) => Nil }

    def versionOpt: Option[String] = getStringOpt("sclera.version")
    def version: String = versionOpt.getOrElse("(Unspecified version)")

    def encryptKey: String = getStringOpt("sclera.storage.encryptkey").get

    def rootDir: File = new File(getStringOpt("sclera.storage.rootdir").get)
    def homeDir: File = new File(getStringOpt("sclera.storage.homedir").get)
    def assetDir: File = new File(getStringOpt("sclera.storage.assetdir").get)
    def dataDir: File = new File(getStringOpt("sclera.storage.datadir").get)
    def objectDir: File = new File(getStringOpt("sclera.storage.objectdir").get)

    def serviceAssetDir: File =
        new File(getStringOpt("sclera.service.assetdir").get)

    def defaultMLService: String =
        getStringOpt("sclera.service.default.mlservice").get
    def defaultNlpService: String =
        getStringOpt("sclera.service.default.nlpservice").get
    def defaultPredLabelerService: String =
        getStringOpt("sclera.service.default.predicaterowlabelerservice").get
    def defaultSequenceMatcherService: String =
        getStringOpt("sclera.service.default.sequencematcherservice").get
    def defaultDisplayService: String =
        getStringOpt("sclera.service.default.displayservice").get

    def historyPath: String = getStringOpt("sclera.shell.history").get

    def schemaVersion: String =
        getStringOpt("sclera.location.schema.version") getOrElse {
            Console.err.println(
                "Sclera configuration: Cannot find schema version, " +
                " using Sclera version"
            )

            versionOpt getOrElse {
                throw new IllegalArgumentException(
                    "Sclera configuration: Cannot find Sclera version"
                )
            }
        }

    def schemaDbms: String =
        getStringOpt("sclera.location.schema.dbms").get
    def schemaDb: String =
        getStringOpt("sclera.location.schema.database").get
    def schemaDbConfig: List[(String, String)] =
        getDbConfig("sclera.location.schema.config")

    def tempDbms: String =
        getStringOpt("sclera.location.tempdb.dbms") getOrElse "H2MEM"
    def tempDb: String =
        getStringOpt("sclera.location.tempdb.database") getOrElse "tempdb"
    def tempDbConfig: List[(String, String)] =
        getDbConfig("sclera.location.tempdb.config")

    def dataCacheLocationIdOpt: Option[String] =
        getStringOpt("sclera.location.datacache")

    def defaultLocationIdOpt: Option[String] =
        getStringOpt("sclera.location.default")

    def batchSize: Int = getIntOpt("sclera.exec.batchsize") getOrElse 100

    def inputToken: String =
        getStringOpt("sclera.shell.inputtoken") getOrElse "?"

    def prompt: String =
        getStringOpt("sclera.shell.prompt") getOrElse "Sclera$ "

    def partPrompt: String = " "*prompt.size

    private var isExplainOverrideOpt: Option[Boolean] = None
    def setExplain(v: Boolean): Unit = { isExplainOverrideOpt = Option(v) }

    def isExplain: Boolean = isExplainOverrideOpt.getOrElse {
        getBooleanOpt("sclera.shell.explain") getOrElse false
    }

    def configValPairs: List[(String, String)] = List(
        "sclera.exec.batchsize" -> batchSize.toString,
        "sclera.location.datacache" ->
            dataCacheLocationIdOpt.getOrElse("[Not Specified]"),
        "sclera.location.default" ->
            defaultLocationIdOpt.getOrElse("[Not Specified]"),
        "sclera.location.schema.config" -> schemaDbConfig.mkString(", "),
        "sclera.location.schema.database" -> schemaDb,
        "sclera.location.schema.dbms" -> schemaDbms,
        "sclera.location.schema.version" -> schemaVersion,
        "sclera.location.tempdb.config" -> tempDbConfig.mkString(", "),
        "sclera.location.tempdb.database" -> tempDb,
        "sclera.location.tempdb.dbms" -> tempDbms,
        "sclera.service.assetdir" -> serviceAssetDir.getCanonicalPath,
        "sclera.service.default.mlservice" -> defaultMLService,
        "sclera.service.default.nlpservice" -> defaultNlpService,
        "sclera.service.default.predicaterowlabelerservice" ->
            defaultPredLabelerService,
        "sclera.service.default.sequencematcherservice" ->
            defaultSequenceMatcherService,
        "sclera.service.default.displayservice" -> defaultDisplayService,
        "sclera.shell.explain" -> isExplain.toString,
        "sclera.shell.history" -> historyPath,
        "sclera.shell.prompt" -> prompt,
        "sclera.storage.encryptkey" -> encryptKey,
        "sclera.storage.rootdir" -> rootDir.getCanonicalPath,
        "sclera.storage.homedir" -> homeDir.getCanonicalPath,
        "sclera.storage.assetdir" -> assetDir.getCanonicalPath,
        "sclera.storage.datadir" -> dataDir.getCanonicalPath,
        "sclera.storage.objectdir" -> objectDir.getCanonicalPath,
        "sclera.version" -> versionOpt.getOrElse("[Not Specified]")
    )
}

import com.typesafe.config._
import scala.language.postfixOps


val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()


name := """play-scala-angularjs"""
organization in ThisBuild := "some.package.info"
version := conf.getString("app.version")


lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.11.8"


// Scala Compiler Options
scalacOptions in ThisBuild ++= Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code"
)


libraryDependencies ++= Seq(
    jdbc,
    cacheApi,
    ws,
    filters,
    guice,
    "com.typesafe.play" %% "play-json" % "2.6.+",
    "net.codingwell" %% "scala-guice" % "4.+",
    // silhouette
    "com.iheart" %% "ficus" % "1.4+",
    "com.atlassian.jwt" % "jwt-api" % "1.6.1" % "provided",
    "com.atlassian.jwt" % "jwt-core" % "1.6.1" % "provided",
    "com.mohiva" %% "play-silhouette" % "5.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
    "com.mohiva" %% "play-silhouette-testkit" % "5.0.0" % Test,
    // persistence technologies
    "org.mongodb.scala" %% "mongo-scala-driver" % "1.+",
    // testing
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.+" % Test
)

//
// Repositories
//
resolvers := (Resolver.mavenLocal) +:
             // Required by specs2 to get scalaz-stream
             ("scalaz-bintray" at "http://dl.bintray.com/scalaz/releases") +:
             ("Nexus Releases Repository" at "https://oss.sonatype.org/content/repositories/releases") +:
             ("Atlassian Repository" at "https://maven.atlassian.com/repository/public") +:
             resolvers.value

fork in run := true

//
// UI Build Scripts
//

val Success = 0 // 0 exit code
val Error = 1 // 1 exit code

PlayKeys.playRunHooks += baseDirectory.map(UIBuild.apply).value

val isWindows = System.getProperty("os.name").toLowerCase().contains("win")

def runScript(script: String)(implicit dir: File): Int = {
if(isWindows){ Process("cmd /c " + script, dir) } else { Process(script, dir) } }!

def uiWasInstalled(implicit dir: File): Boolean = (dir / "node_modules").exists()

def runNpmInstall(implicit dir: File): Int =
  if (uiWasInstalled) Success else runScript("npm install")

def ifUiInstalled(task: => Int)(implicit dir: File): Int =
  if (runNpmInstall == Success) task
  else Error

def runProdBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build-prod"))

def runDevBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build"))

def runUiTests(implicit dir: File): Int = ifUiInstalled(runScript("npm run test-no-watch"))

lazy val `ui-dev-build` = TaskKey[Unit]("Run UI build when developing the application.")

`ui-dev-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runDevBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

lazy val `ui-prod-build` = TaskKey[Unit]("Run UI build when packaging the application.")

`ui-prod-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runProdBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

lazy val `ui-test` = TaskKey[Unit]("Run UI tests when testing application.")

`ui-test` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runUiTests != 0) throw new Exception("UI tests failed!")
}

`ui-test` := (`ui-test` dependsOn `ui-dev-build`).value

dist := (dist dependsOn `ui-prod-build`).value

stage := (stage dependsOn `ui-prod-build`).value

test := ((test in Test) dependsOn `ui-test`).value

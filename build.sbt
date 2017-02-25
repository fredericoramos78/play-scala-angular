import com.typesafe.config._
//import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()


name := """play-scala-angularjs"""
organization in ThisBuild := "some.package.info"

version := conf.getString("app.version")


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

// Scala Compiler Options
//"-target:jvm-1.8",
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

//
// Repositories
//
resolvers += "Nexus Releases Repository" at "https://oss.sonatype.org/content/repositories/releases"


libraryDependencies ++= Seq(
  cache,
  ws,
  filters,
  // WebJars
  "org.webjars" %% "webjars-play" % "2.5.+",
  "org.webjars" % "jquery" % "2.+" intransitive(),     // jQuery's bower/npm release includes the 'src' folder, which has some invalid files for UglifyJS2
  "org.webjars" % "requirejs" % "2.1.+",
  "org.webjars" % "requirejs-domready" % "2.+" intransitive(),
  "org.webjars" % "font-awesome" % "4.+" intransitive(),
  "org.webjars" % "bootstrap" % "3.+" intransitive(),
  "org.webjars" % "angularjs" % "1.4.+",
  "org.webjars.bower" % "angular-ui-router" % "0.+" intransitive(),
  "org.webjars" % "momentjs" % "2.+" intransitive(),
  "org.webjars" % "modernizr" % "2.8.3",
  "org.webjars.bower" % "satellizer" % "0.+" intransitive(),
  "org.webjars.bower" % "angular-bootstrap" % "1.+" intransitive(),
  "org.webjars.bower" % "angular-toastr" % "1.+" intransitive(),  
  "org.webjars" % "metisMenu" % "2.5.+",
  "org.webjars.bower" % "ng-table" % "1.0.0",
  "org.webjars.bower" % "angular-input-masks" % "2.3.0",
  "org.webjars.bower" % "angular-br-filters" % "0.5.0",
  "org.webjars.bower" % "string-mask" % "0.3.0",
  "org.webjars.bower" % "br-validations" % "0.3.0",
  //
  "org.julienrf" %% "play-jsmessages" % "2.0.0",
  "net.codingwell" %% "scala-guice" % "4.+",
  // persistence technologies  
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


//
// Eclipse IDE settings
//
// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)
// Always include src files
EclipseKeys.withSource := true

// LESS plugin config
LessKeys.cleancss in Assets := true
LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"
excludeFilter in (Assets, LessKeys.less) := "_*.less"


pipelineStages := Seq(rjs, digest, gzip)

// RequireJS with sbt-rjs (https://github.com/sbt/sbt-rjs#sbt-rjs)
// ~~~
RjsKeys.paths ++= Map(
    "jsRoutes" -> ("/jsroutes" -> "empty:")
)
RjsKeys.webJarCdns := Map.empty
RjsKeys.mainModule := "main"

// Use NodeJS to process javascript files
JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

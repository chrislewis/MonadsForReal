import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val specs = "org.scala-tools.testing" %% "specs" %"1.6.8" % "test"
  
  val tools_snapshots = "Snapshots" at "http://scala-tools.org/repo-snapshots/"
}

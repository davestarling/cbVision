name := "cbVision"

version := "1.0"

mainClass := Some("me.d10g.vision.CBVision")

resolvers += "ReactiveCouchbase Releases" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases/"

libraryDependencies ++= Seq(
	"org.reactivecouchbase" %% "reactivecouchbase-core" % "0.2"
)

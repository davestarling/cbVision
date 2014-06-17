package me.d10g.vision

trait CliUtils {

	case class Options(quiet : Boolean = false,
	                   inputDir : Option[String]  = None,
	                   outputDir : Option[String] = None,
	                   imageExt : Option[String]  = Some("jpg"),
			           cbBucket : Option[String] = None)

	def parseArgs(map: Map[String, Any], args: List[String]): Map[String, Any] = {
		args match {
			case Nil => map
			case "--quiet" :: tail =>
				parseArgs(map ++ Map("quiet" -> true), tail)
			case "--input-dir" :: value :: tail =>
				parseArgs(map ++ Map("inputDir" -> value), tail)
			case "--output-dir" :: value :: tail =>
				parseArgs(map ++ Map("outputDir" -> value), tail)
			case "--image-ext" :: value :: tail =>
				parseArgs(map ++ Map("imageExt" -> value), tail)
			case "--bucket" :: value :: tail =>
				parseArgs(map ++ Map("cbBucket" -> value), tail)
			case option :: tail =>
				Console.err.println("Unknown option " + option)
				sys.exit()
		}
	}

	def getOptions(optionMap: Map[String, _]): Options = {
		val default = Options()
		Options(
			quiet     = optionMap.getOrElse("quiet", default.quiet).asInstanceOf[Boolean],
			inputDir  = optionMap.get("inputDir") match {
				case None => default.inputDir
				case Some(value) => Some(value.asInstanceOf[String])
			},
			outputDir = optionMap.get("outputDir") match {
				case None => default.outputDir
				case Some(value) => Some(value.asInstanceOf[String])
			},
			imageExt  = optionMap.get("imageExt") match {
				case None => default.imageExt
				case Some(value) => Some(value.asInstanceOf[String])
			},
			cbBucket  = optionMap.get("cbBucket") match {
				case None => default.cbBucket
				case Some(value) => Some(value.asInstanceOf[String])
			}
		)
	}

}
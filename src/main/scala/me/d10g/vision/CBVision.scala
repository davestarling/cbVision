package me.d10g.vision

import java.io.File
import org.opencv.highgui.Highgui
import org.reactivecouchbase.ReactiveCouchbaseDriver
import scala.concurrent.ExecutionContext.Implicits.global

object CBVision extends App with OpenCVUtils with JsonUtils with CliUtils {

	override def main(args : Array[String]) = {
		val optionMap = parseArgs(Map(), args.toList)
		val options   = getOptions(optionMap)


		if (options.inputDir == None) {
			Console.err.println(s"Missing Input Directory")
			sys.exit()
		}

		if (options.outputDir == None) {
			Console.err.println(s"Missing Input Directory")
			sys.exit()
		}

		if (options.cbBucket == None) {
			Console.err.println(s"Missing Couchbase Bucket")
			sys.exit()
		}

		loadLibrary()

		val list = new File(options.inputDir.get).listFiles.filter(_.getName.endsWith("." + options.imageExt.get))

		val driver = ReactiveCouchbaseDriver()
		val bucket = driver.bucket(options.cbBucket.get)

		for (file <- list) {
			if (!options.quiet) println(file.getPath)

			val image = Highgui.imread(file.getPath)

			val hist = Analysis.getRGBHistogram(image)

			val histogram =
				for {
					row <- 0 to hist.rows() - 1
					col <- 0 to hist.cols() - 1
				} yield {
					hist.get(row, col)(0).toInt
				}

			val photo = Photo(
				file.getName,
				image.size().width.toInt,
				image.size.height.toInt,
				histogram.toArray,
				Analysis.getFaces(image)
			)

			bucket.set[Photo](file.getName, photo).onSuccess {
				case status => if (!options.quiet) println(s"Operation status : ${status.getMessage}")
			}

			val filename = options.outputDir.get + file.getName
			if (!options.quiet) println(s"Writing ${filename}")
			Highgui.imwrite(filename, image)
		}

		driver.shutdown()
	}
}
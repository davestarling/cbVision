package me.d10g.vision

import java.io.File
import org.opencv.core.Mat
import org.opencv.highgui.Highgui
import org.reactivecouchbase.ReactiveCouchbaseDriver
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

object CBVision extends App with OpenCVUtils with JsonUtils with CliUtils {

	def time[R](block: => R): R = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()
		println("Elapsed time: " + (t1 - t0) / 1000000000 + "s")
		result
	}

	override def main(args : Array[String]) = {
		time {
			val optionMap = parseArgs(Map(), args.toList)
			val options = getOptions(optionMap)


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

			def processResult (hist: Mat, faces: Array[Face], image: Mat, file: File) = {
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
					faces
				)

				bucket.set[Photo](file.getName, photo).onSuccess {
					case status => if (!options.quiet) println(s"Couchbase write for ${file.getName} : ${status.getMessage}")
				}

				val filename = options.outputDir.get + file.getName
				if (!options.quiet) println(s"Writing ${file.getName}")
				Highgui.imwrite(filename, image)
			}

			for (file <- list) {
				if (!options.quiet) println(s"Opening ${file.getName}")

				val image = Highgui.imread(file.getPath)

				val histF = future {
					Analysis.getRGBHistogram(image)
				}

				val facesF = future {
					Analysis.getFaces(image)
				}

				val result = for {
					histResult <- histF
					facesResult <- facesF
				} yield processResult(histResult, facesResult, image, file)
			}

			driver.shutdown()

		}
	}
}
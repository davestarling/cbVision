package me.d10g.vision

import java.util
import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier

case class Photo(filename: String,
                 x: Int,
                 y: Int,
                 histogram: Array[Double],
                 faces: List[Face])

case class Face(x: Int, y: Int, w: Int, h: Int)

object Analysis {

	def getRGBHistogram(image: Mat): Mat = {
		val cvtImage = new Mat()

		Imgproc.cvtColor(image, cvtImage, Imgproc.COLOR_RGB2GRAY)

		val bgr_planes = new util.ArrayList[Mat]
		Core.split(cvtImage, bgr_planes)

		val histSize  = new MatOfInt(256)
		val histRange = new MatOfFloat(0f, 256f)
		val b_hist    = new Mat

		Imgproc.calcHist(bgr_planes, new MatOfInt(0), new Mat, b_hist, histSize, histRange, false)

		b_hist
	}

	def getFaces(image: Mat): List[Face] = {

		val faceDetector = new CascadeClassifier(getClass.getResource("/lbpcascade_frontalface.xml").getPath)

		val faceDetections = new MatOfRect
		faceDetector.detectMultiScale(image, faceDetections)

		var faceList: List[Face] = List()

		for (rect <- faceDetections.toArray) {

			Core.rectangle(
				image,
				new Point(rect.x, rect.y),
				new Point(rect.x + rect.width,
					rect.y + rect.height),
				new Scalar(0, 255, 0)
			)

			faceList ::= new Face(
				rect.x,
				rect.y,
				rect.width,
				rect.height
			)
		}

		faceList
	}
}
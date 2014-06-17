package me.d10g.vision

import org.opencv.core.Core

trait OpenCVUtils {

	def loadLibrary() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
	}

}

cbVision
========

Experimental Couchbase/OpenCV biometric/metadata store using OpenCV's Java bindings and Reactive Couchbase

Initially will just scan a directory of images, generate a histogram, and run through a face detection/drawing process, outputting to another directory. It will store the faces, their locations, and the histogram in Couchbase.

There's a ton of stuff to tidy up: proper error checking and exception handling, asynchronous futures for analysis, etc.
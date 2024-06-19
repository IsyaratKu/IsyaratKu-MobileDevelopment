package com.isyaratku.app.ui.main.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ASLSignLanguageDetectorHelper(
    private val context: Context,
    private val interpreter: Interpreter,
    private val detectorListener: DetectorListener?
) {

    fun detectSign(image: ImageProxy) {
        val bitmap = imageProxyToBitmap(image)
        val inputBuffer = convertBitmapToByteBuffer(bitmap)
        val outputBuffer = Array(1) { FloatArray(27) }  // Sesuaikan output shape jika diperlukan

        // Run inference
        interpreter.run(inputBuffer, outputBuffer)

        // Handle results
        val results = outputBuffer[0]
        detectorListener?.onResults(results)
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val nv21Buffer = yuv420ToNv21(image)
        val yuvImage = YuvImage(nv21Buffer, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun yuv420ToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputShape = intArrayOf(1, 200, 200, 3)
        val byteBuffer = ByteBuffer.allocateDirect(1 * 200 * 200 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(200 * 200)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
        scaledBitmap.getPixels(intValues, 0, 200, 0, 0, 200, 200)

        var pixel = 0
        for (i in 0 until 200) {
            for (j in 0 until 200) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16 and 0xFF) - 127) / 128.0f)
                byteBuffer.putFloat(((value shr 8 and 0xFF) - 127) / 128.0f)
                byteBuffer.putFloat(((value and 0xFF) - 127) / 128.0f)
            }
        }
        return byteBuffer
    }


    interface DetectorListener {
        fun onResults(results: FloatArray)
    }
}
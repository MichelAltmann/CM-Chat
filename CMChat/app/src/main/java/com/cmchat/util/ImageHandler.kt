package com.cmchat.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.IOException

object ImageHandler {

    const val IMAGE_GETTER_URL = "http://10.147.17.129:8081/image?imageId="
    fun uriToByteArray(uri: Uri, activity: Activity): ByteArray? {
        val contentResolver = activity.contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        val orientation = getExifOrientation(activity, uri)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val imageWidth = options.outWidth
        val imageHeight = options.outHeight

        // Calculate the sample size to reduce the image size while maintaining aspect ratio
        var sampleSize = 1
        while ((imageWidth / sampleSize) * (imageHeight / sampleSize) > 500000) {
            sampleSize *= 2
        }

        // Decode the image with the calculated sample size
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = sampleSize

        val compressedBitmap = BitmapFactory.decodeStream(
            contentResolver.openInputStream(uri),
            null,
            decodeOptions
        )

        val rotatedBitmap = rotateBitmap(compressedBitmap, orientation)

        val outputStream = ByteArrayOutputStream()
        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return outputStream.toByteArray()
    }

    fun getExifOrientation(applicationContext: Context, uri: Uri): Int {
        var orientation = ExifInterface.ORIENTATION_UNDEFINED
        try {
            val inputStream = applicationContext.contentResolver.openInputStream(uri)
            inputStream?.use {
                val exifInterface = ExifInterface(it)
                orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return orientation
    }

    fun rotateBitmap(bitmap: Bitmap?, orientation: Int): Bitmap? {
        if (bitmap == null) return null

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

}
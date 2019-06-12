package com.experienceconnect.qrscanner.function.camera

import android.content.Context
import android.graphics.Point
import android.util.Rational
import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig

class CameraPreview(val width:Int,val height:Int) {
     val frontPreview: Preview
     val backPreview: Preview

    init {
        val frontConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.FRONT)
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(width, height))
        }.build()
        val backConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(width, height))
        }.build()

        frontPreview = Preview(frontConfig)
        backPreview = Preview(backConfig)
    }
}
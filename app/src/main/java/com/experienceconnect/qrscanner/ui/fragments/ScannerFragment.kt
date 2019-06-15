package com.experienceconnect.qrscanner.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.experienceconnect.qrscanner.databinding.ScannerFragmentBinding
import com.experienceconnect.qrscanner.ui.viewmodels.ScannerViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import android.os.Handler
import android.os.HandlerThread
import android.util.*
import android.widget.TextView
import androidx.camera.core.*
import com.experienceconnect.qrscanner.R
import java.lang.Exception
import java.nio.ByteBuffer
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata


class ScannerFragment : Fragment() {

    private val vm: ScannerViewModel by viewModel()

    private var lensFacing = CameraX.LensFacing.BACK
    private lateinit var viewFinder: TextureView
    private lateinit var logTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = ScannerFragmentBinding.inflate(inflater, container, false)
        binding.btnStop.setOnClickListener {
            Navigation.findNavController(it).popBackStack(R.id.mainFragment, false)
        }
        binding.btnSwitchCamera.setOnClickListener {
            switchCamera()
        }
        binding.btnPause.setOnClickListener {
            vm.pause = !vm.pause
        }
        viewFinder = binding.cameraPreview
        viewFinder.post {
            startCamera()
        }
        logTextView = binding.tvLog
        return binding.root
    }


    @SuppressLint("RestrictedApi")
    private fun switchCamera() {
        lensFacing = if (CameraX.LensFacing.FRONT == lensFacing) {
            CameraX.LensFacing.BACK
        } else {
            CameraX.LensFacing.FRONT
        }
        try {
            // Only bind use cases if we can query a camera with this orientation
            CameraX.getCameraWithLensFacing(lensFacing)
            startCamera()
        } catch (exc: Exception) {
            // Do nothing
        }
    }

    private fun startCamera() {
        // Make sure that there are no other use cases bound to CameraX
        CameraX.unbindAll()

        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }

        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
        }.build()

        val preview = Preview(previewConfig)
        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
        }

        // Setup image analysis pipeline that computes average pixel luminance in real time
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            // Use a worker thread for image analysis to prevent preview glitches
            val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            // In our analysis, we care more about the latest image than analyzing *every* image
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        val imageAnalyzer = ImageAnalysis(analyzerConfig).also {
            it.analyzer = ImageAnalysis.Analyzer { image, rotationDegrees ->
                if (vm.pause) return@Analyzer

                if (!vm.isTimeout()) return@Analyzer

                image.image?.let { img ->
                    val visionImg = FirebaseVisionImage.fromMediaImage(
                        img,
                        FirebaseVisionImageMetadata.ROTATION_90
                    )
                    detector.detectInImage(visionImg)
                        .addOnSuccessListener { barcodes ->
                            Log.d("TEST", "addOnSuccessListener ${barcodes.size}")
                            barcodes.forEach { barcode ->
                                Log.d("TEST", "raw: ${barcode.rawValue}")
                                logTextView.text = barcode.rawValue
                            }
                        }
                        .addOnFailureListener {
                            Log.d("TEST", "addOnFailureListener ${it.message}")
                        }
                }
            }
        }

        CameraX.bindToLifecycle(this, preview, imageAnalyzer)
    }


    private val options = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(
            FirebaseVisionBarcode.FORMAT_QR_CODE,
            FirebaseVisionBarcode.FORMAT_AZTEC
        )
        .build()
    private val detector = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(options)
}

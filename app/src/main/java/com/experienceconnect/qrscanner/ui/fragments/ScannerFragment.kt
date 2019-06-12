package com.experienceconnect.qrscanner.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
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
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.util.*
import androidx.camera.core.*
import com.experienceconnect.qrscanner.R
import com.experienceconnect.qrscanner.function.camera.CameraPreview
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata


class ScannerFragment : Fragment() {

    private val vm: ScannerViewModel by viewModel()

    companion object {
        fun newInstance() = ScannerFragment()
    }

    private var lensFacing = CameraX.LensFacing.BACK
    private lateinit var viewFinder: TextureView
    private lateinit var cameraPreview: CameraPreview

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
        viewFinder = binding.cameraPreview
        viewFinder.post {
            startCamera()
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

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
            updateTransform()
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

        val imageAnalyzer = ImageAnalysis(analyzerConfig)
        imageAnalyzer.analyzer = LuminosityAnalyzer(metrics.widthPixels, metrics.heightPixels)

        CameraX.bindToLifecycle(this, preview, imageAnalyzer)
    }

    private fun updateTransform() {
        // TODO: Implement camera viewfinder transformations
    }


    /**
     * Our custom image analysis class.
     *
     * <p>All we need to do is override the function `analyze` with our desired operations. Here,
     * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
     */
    private class LuminosityAnalyzer(val w:Int,val h:Int) : ImageAnalysis.Analyzer {
        val TEMPTS = TimeUnit.SECONDS.toMillis(1)
        private var lastTimeStamp = 0L
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_QR_CODE,
                FirebaseVisionBarcode.FORMAT_AZTEC
            )
            .build()
        val detector = FirebaseVision.getInstance()
            .getVisionBarcodeDetector(options)


        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }
        override fun analyze(image: ImageProxy, rotationDegrees: Int) {

            val currentTimeStamp = System.currentTimeMillis()
            if (currentTimeStamp - lastTimeStamp > TEMPTS) {
                Log.d("TEST", "analyze")
                val buffer = image.planes[0].buffer

                // Extract image data from callback object
                val data = buffer.toByteArray()


                image.image?.let {img->
                
                    Log.d("TEST", "w ${img.width} h: ${img.height}")
                    val metadata = FirebaseVisionImageMetadata.Builder()
                        .setWidth(img.width)
                        .setHeight(img.height)
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                        .build()
                    val mm =
                        FirebaseVisionImage.fromByteArray(data,metadata)
                    detector.detectInImage(mm).addOnSuccessListener { barcodes ->
                            Log.d("TEST", "addOnSuccessListener")
                            barcodes.forEach { barcode ->
                                val valueType = barcode.getValueType();
                                when (valueType) {
                                    FirebaseVisionBarcode.TYPE_WIFI ->
                                        Log.d("TEST", "addOnSuccessListener $barcode")
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.d("TEST", "addOnFailureListener ${it.message}")
                        }
                }
                lastTimeStamp = currentTimeStamp
            }

        }
    }


}

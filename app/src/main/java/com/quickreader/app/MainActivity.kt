package com.quickreader.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.quickreader.app.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var readingSpeed = 250
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, getString(R.string.camera_permission_required), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.captureButton.setOnClickListener { takePhoto() }
        
        setupSpeedControl()
        
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupSpeedControl() {
        binding.speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                readingSpeed = progress + 50
                binding.speedValue.text = readingSpeed.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        binding.speedValue.text = readingSpeed.toString()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.statusText.visibility = android.view.View.VISIBLE
        binding.statusText.text = getString(R.string.processing_image)

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    processImage(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.statusText.visibility = android.view.View.GONE
                    Toast.makeText(this@MainActivity, "Photo capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.statusText.visibility = android.view.View.GONE
                    
                    if (visionText.text.isNotEmpty()) {
                        val intent = Intent(this, SpeedReaderActivity::class.java)
                        intent.putExtra("text", visionText.text)
                        intent.putExtra("speed", readingSpeed)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, getString(R.string.no_text_found), Toast.LENGTH_SHORT).show()
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.statusText.visibility = android.view.View.GONE
                    Toast.makeText(this, "Text recognition failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    imageProxy.close()
                }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Toast.makeText(this, "Use case binding failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()
    }
}
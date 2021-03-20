package com.example.openglsample

import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import com.example.openglsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val TOUCH_SCALE_FACTOR = 180.0f / 320f
    private var previousX = 0f
    private var previousY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val renderer = MyRenderer()
        binding.glSurfaceView.setEGLContextClientVersion(3)
        binding.glSurfaceView.setRenderer(renderer)
        binding.glSurfaceView.renderMode = RENDERMODE_WHEN_DIRTY

        binding.glSurfaceView.setOnTouchListener { _, event ->
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    var dx = x - previousX
                    var dy = x - previousY

                    // reverse direction of rotation above the mid-line
                    if (y > binding.glSurfaceView.height / 2) {
                        dx *= -1
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < binding.glSurfaceView.width / 2) {
                        dy *= -1
                    }

                    renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                    binding.glSurfaceView.requestRender()
                }
            }

            previousX = x
            previousY = y

            true
        }
    }
}
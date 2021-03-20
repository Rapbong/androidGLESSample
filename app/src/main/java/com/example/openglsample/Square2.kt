package com.example.openglsample

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

var squareCoords = floatArrayOf(
    -0.7f, 0.75f, 0.0f,      // top left
    -0.7f, 0.65f, 0.0f,      // bottom left
    -0.6f, 0.65f, 0.0f,      // bottom right
    -0.6f, 0.75f, 0.0f       // top right
)

class Square2 {
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    val color = floatArrayOf(1f, 1f, 0f, 1f)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    private val drawListBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    private var program = 0
    private var positionHandle = 0
    private var colorHandle = 0

    private val vertexCount = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4

    init {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }
    }

    fun draw() {
        GLES30.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(program, "vPosition").also {
            // Enable a handle to the triangle vertices
            GLES30.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES30.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
            )

            // get handle to fragment shader's vColor member
            colorHandle = GLES30.glGetUniformLocation(program, "vColor").also { colorHandle ->
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, drawListBuffer)

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
        }
    }
}
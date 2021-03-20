package com.example.openglsample

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {
    // R, G, B, alpha
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(triangleCoords)
                position(0)
            }
        }

    // This matrix member variable provides a hook to manipulate
    // the coordinates of the objects that use this vertex shader
    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "   gl_FragColor = vColor;" +
            "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle = 0

    private var program: Int
    private var positionHandle = 0
    private var colorHandle = 0

    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
        }
    }

    // pass in the calculated transformation matrix
    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(program)
        positionHandle = GLES30.glGetAttribLocation(program, "vPosition").also {
            GLES30.glEnableVertexAttribArray(it)

            GLES30.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES30.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
            )

            colorHandle = GLES30.glGetUniformLocation(program, "vColor").also { colorHandle ->
                GLES30.glUniform4fv(colorHandle, 1, color, 0)
            }

            //get handle to shape's transformation matrix
            vPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            // Draw the triangle
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(it)
        }
    }

    fun draw() {
        // Add program to OpenGL ES environment
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

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(it)
        }
    }
}
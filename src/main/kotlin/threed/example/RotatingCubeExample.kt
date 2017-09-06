package threed.example

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_TEST
import org.khronos.webgl.WebGLRenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAGMENT_SHADER
import org.khronos.webgl.WebGLRenderingContext.Companion.LEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.STATIC_DRAW
import org.khronos.webgl.WebGLRenderingContext.Companion.TRIANGLES
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_SHORT
import org.khronos.webgl.WebGLRenderingContext.Companion.VERTEX_SHADER
import threed.fitDrawingBufferIntoCanvas
import kotlin.browser.window
import kotlin.js.Math

fun rotateCube(gl: WebGLRenderingContext) {

    val vertices = arrayOf(
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,

            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,

            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,

            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,

            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f
    )

    val colors = arrayOf(
            5.0f, 3.0f, 7.0f, 5.0f, 3.0f, 7.0f, 5.0f, 3.0f, 7.0f, 5.0f, 3.0f, 7.0f,
            1.0f, 1.0f, 3.0f, 1.0f, 1.0f, 3.0f, 1.0f, 1.0f, 3.0f, 1.0f, 1.0f, 3.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
    )

    val indices = arrayOf<Short>(
            0, 1, 2,
            0, 2, 3,

            4, 5, 6,
            4, 6, 7,

            8, 9, 10,
            8, 10, 11,

            12, 13, 14,
            12, 14, 15,

            16, 17, 18,
            16, 18, 19,

            20, 21, 22,
            20, 22, 23
    )

    // Create and store data into vertex buffer
    val vertexBuffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, vertexBuffer)
    gl.bufferData(ARRAY_BUFFER, Float32Array(vertices), STATIC_DRAW)

    // Create and store data into color buffer
    val colorBuffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, colorBuffer)
    gl.bufferData(ARRAY_BUFFER, Float32Array(colors), STATIC_DRAW)

    // Create and store data into index buffer
    val indexBuffer = gl.createBuffer()
    gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.bufferData(ELEMENT_ARRAY_BUFFER, Uint16Array(indices), STATIC_DRAW)

/*=================== Shaders =========================*/

    val vertexShaderCode =
            """
            attribute vec3 position;
            uniform mat4 Pmatrix;
            uniform mat4 Vmatrix;
            uniform mat4 Mmatrix;
            attribute vec3 color;
            varying vec3 vColor;

            void main(void) {
                gl_Position = Pmatrix*Vmatrix*Mmatrix*vec4(position, 1.);
                vColor = color;
            }
            """

    val fragmentShaderCode =
            """
            precision mediump float;
            varying vec3 vColor;
            void main(void) {
                gl_FragColor = vec4(vColor, 1.);
            }
            """

    val vertexShader = gl.createShader(VERTEX_SHADER)
    gl.shaderSource(vertexShader, vertexShaderCode)
    gl.compileShader(vertexShader)

    val fragmentShader = gl.createShader(FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderCode)
    gl.compileShader(fragmentShader)

    val shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, vertexShader)
    gl.attachShader(shaderProgram, fragmentShader)
    gl.linkProgram(shaderProgram)

/* ====== Associating attributes to vertex shader =====*/
    val Pmatrix = gl.getUniformLocation(shaderProgram, "Pmatrix")
    val Vmatrix = gl.getUniformLocation(shaderProgram, "Vmatrix")
    val Mmatrix = gl.getUniformLocation(shaderProgram, "Mmatrix")

    gl.bindBuffer(ARRAY_BUFFER, vertexBuffer)
    val position = gl.getAttribLocation(shaderProgram, "position")
    gl.vertexAttribPointer(position, 3, FLOAT, false, 0, 0)

// Position
    gl.enableVertexAttribArray(position)
    gl.bindBuffer(ARRAY_BUFFER, colorBuffer)
    val color = gl.getAttribLocation(shaderProgram, "color")
    gl.vertexAttribPointer(color, 3, FLOAT, false, 0, 0)

// Color
    gl.enableVertexAttribArray(color)
    gl.useProgram(shaderProgram)

/*==================== MATRIX =====================*/

    fun get_projection(angle: Float, aspectRatio: Float, zMin: Float, zMax: Float): Array<Float> {
        val ang = Math.tan((angle * .5) * Math.PI / 180).toFloat()//angle*.5
        return arrayOf(
                0.5f / ang, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f * aspectRatio / ang, 0.0f, 0.0f,
                0.0f, 0.0f, -(zMax + zMin) / (zMax - zMin), -1.0f,
                0.0f, 0.0f, (-2 * zMax * zMin) / (zMax - zMin), 0.0f
        )
    }

    val proj_matrix = get_projection(40.0f, (gl.canvas.width.toFloat() / gl.canvas.height.toFloat()), 1.0f, 100.0f)

    val mov_matrix = arrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val view_matrix = arrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)

// translating z
    view_matrix[14] = view_matrix[14] - 6.0f //zoom

/*==================== Rotation ====================*/

    fun rotateZ(m: Array<Float>, angle: Double) {
        val c = Math.cos(angle).toFloat()
        val s = Math.sin(angle).toFloat()
        val mv0 = m[0]
        val mv4 = m[4]
        val mv8 = m[8]

        m[0] = c * m[0] - s * m[1]
        m[4] = c * m[4] - s * m[5]
        m[8] = c * m[8] - s * m[9]

        m[1] = c * m[1] + s * mv0
        m[5] = c * m[5] + s * mv4
        m[9] = c * m[9] + s * mv8
    }

    fun rotateX(m: Array<Float>, angle: Double) {
        val c = Math.cos(angle).toFloat()
        val s = Math.sin(angle).toFloat()
        val mv1 = m[1]
        val mv5 = m[5]
        val mv9 = m[9]

        m[1] = m[1] * c - m[2] * s
        m[5] = m[5] * c - m[6] * s
        m[9] = m[9] * c - m[10] * s

        m[2] = m[2] * c + mv1 * s
        m[6] = m[6] * c + mv5 * s
        m[10] = m[10] * c + mv9 * s
    }

    fun rotateY(m: Array<Float>, angle: Double) {
        val c = Math.cos(angle).toFloat()
        val s = Math.sin(angle).toFloat()
        val mv0 = m[0]
        val mv4 = m[4]
        val mv8 = m[8]

        m[0] = c * m[0] + s * m[2]
        m[4] = c * m[4] + s * m[6]
        m[8] = c * m[8] + s * m[10]

        m[2] = c * m[2] - s * mv0
        m[6] = c * m[6] - s * mv4
        m[10] = c * m[10] - s * mv8
    }

/*================= Drawing ===========================*/
    var time_old = 0.0

    fun animate(time: Double) {

        gl.fitDrawingBufferIntoCanvas()

        val dt = (time - time_old) / 10.0
        rotateZ(mov_matrix, dt * 0.005)//time
        rotateY(mov_matrix, dt * 0.002)
        rotateX(mov_matrix, dt * 0.003)
        time_old = time

        gl.enable(DEPTH_TEST)
        gl.depthFunc(LEQUAL)
        gl.clearColor(0.5f, 0.5f, 0.5f, 0.9f)
        gl.clearDepth(1.0f)

        gl.clear(COLOR_BUFFER_BIT.or(DEPTH_BUFFER_BIT))
        gl.uniformMatrix4fv(Pmatrix, false, proj_matrix)
        gl.uniformMatrix4fv(Vmatrix, false, view_matrix)
        gl.uniformMatrix4fv(Mmatrix, false, mov_matrix)
        gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)
        gl.drawElements(TRIANGLES, indices.size, UNSIGNED_SHORT, 0)

        window.requestAnimationFrame { t -> animate(t) }
    }
    window.requestAnimationFrame { time -> animate(time) }

}


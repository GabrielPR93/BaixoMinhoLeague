package com.example.baixominholeague
import android.content.Context
import android.graphics.*
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import com.squareup.picasso.Transformation



class CircleTransformation(private val context: Context, val borderWidth: Int, private val borderColor: Int) : Transformation {

    override fun key(): String {
        return "circleTransformation"
    }

    override fun transform(source: Bitmap): Bitmap {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val size = (displayMetrics.widthPixels * 0.95).toInt() / 2

        if (size <= 0) {
            return source
        }

        val squaredBitmap = Bitmap.createScaledBitmap(source, size, size, false)
        if (squaredBitmap !== source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Dibujar el borde circular
        if (borderWidth > 0) {
            val borderPaint = Paint()
            borderPaint.color = borderColor
            borderPaint.style = Paint.Style.STROKE
            val borderWidthPx = ((borderWidth/2.5) * context.resources.displayMetrics.density).toInt()
            borderPaint.strokeWidth = borderWidthPx.toFloat()
            borderPaint.isAntiAlias = true
            canvas.drawCircle(radius, radius, radius - borderWidth / 2f, borderPaint)
        }

        squaredBitmap.recycle()
        return bitmap
    }
}


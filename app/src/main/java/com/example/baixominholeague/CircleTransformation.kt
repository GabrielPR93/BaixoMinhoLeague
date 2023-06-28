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
        val maxSize = (displayMetrics.widthPixels * 0.95).toInt() / 2

        if (maxSize <= 0) {
            return source
        }

        val width = source.width
        val height = source.height

        val scaledWidth: Int
        val scaledHeight: Int

        if (width > height) {
            val ratio = width.toFloat() / height.toFloat()
            scaledWidth = maxSize
            scaledHeight = (maxSize / ratio).toInt()
        } else {
            val ratio = height.toFloat() / width.toFloat()
            scaledWidth = (maxSize / ratio).toInt()
            scaledHeight = maxSize
        }

        val scaledBitmap = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
        if (scaledBitmap !== source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(maxSize, maxSize, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        val paint = Paint()
        val shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val radius = maxSize / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Dibujar el borde circular
        if (borderWidth > 0) {
            val borderPaint = Paint()
            borderPaint.color = borderColor
            borderPaint.style = Paint.Style.STROKE
            val borderWidthPx = ((borderWidth / 2.5) * context.resources.displayMetrics.density).toInt()
            borderPaint.strokeWidth = borderWidthPx.toFloat()
            borderPaint.isAntiAlias = true
            canvas.drawCircle(radius, radius, radius - borderWidth / 2f, borderPaint)
        }

        scaledBitmap.recycle()
        return bitmap
    }
}



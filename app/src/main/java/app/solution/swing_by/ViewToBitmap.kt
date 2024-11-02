package app.solution.swing_by

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object ViewToBitmap {
    fun createBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        val background = view.background
        if (background != null) {
            background.draw(canvas)
            view.draw(canvas)
        }
        return bitmap
    }
}
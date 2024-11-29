package app.solution.swing_by.common

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import app.solution.swing_by.R

class ProgressView @JvmOverloads constructor(
    context: Context,
    activity: Activity,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.progress, this, true)
        orientation = VERTICAL
        this.visibility = GONE
    }

    fun create(context: Context, activity: Activity): ProgressView {
        return ProgressView(context, activity).apply {
            gravity = Gravity.CENTER
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    fun show() {
        this.visibility = VISIBLE

        /*activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )*/
    }

    fun hide() {
        this.visibility = GONE

//        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
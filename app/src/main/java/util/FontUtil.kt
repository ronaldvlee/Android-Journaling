package util

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

object FontUtil {

    fun setFont(context: Context, textView: TextView, fontResID: Int) {
        val typeface = ResourcesCompat.getFont(context, fontResID)
        textView.typeface = typeface
    }
}
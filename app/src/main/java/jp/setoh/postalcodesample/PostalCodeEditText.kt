package jp.setoh.postalcodesample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.AppCompatEditText
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.ReplacementSpan
import android.util.AttributeSet

private const val SPACER = "000"
private const val HYPHEN = "-"

class PostalCodeEditText
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = R.attr.editTextStyle)
    : AppCompatEditText(context, attrs, style) {

    private val span = HyphenSpan()

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                s.removeSpan(span)
                if (s.length >= 3) {
                    s.setSpan(span, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (text.isNotEmpty()) {
            val measureText = if (text.length < 3) {
                text.toString() + SPACER.substring(0, 3 - text.length)
            } else {
                text.substring(0, 3)
            }
            val width = paint.measureText(measureText)
            val x = paddingStart + width
            canvas.drawText(HYPHEN, x, baseline.toFloat(), paint)
        }
    }

    private class HyphenSpan : ReplacementSpan() {

        override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
            val drawText = text.substring(start, end) + HYPHEN
            return paint.measureText(drawText).toInt()
        }

        override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
            drawBackground(canvas, text, start, end, x, top, y, bottom, paint)

            val drawText = text.substring(start, end)
            canvas.drawText(drawText, x, y.toFloat(), paint)
        }

        fun drawBackground(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
            val fm = paint.fontMetricsInt
            val ascent: Int
            val descent: Int
            if (y == bottom && canvas.clipBounds.top == 0) {
                ascent = fm.top - fm.ascent
                descent = 0
            } else if (y == bottom) {
                ascent = fm.top - fm.ascent
                descent = ascent
            } else {
                ascent = 0
                descent = 0
            }

            if (paint is TextPaint && text is Spannable) {
                text.getSpans<CharacterStyle>(start, end, CharacterStyle::class.java).forEach {
                    it.updateDrawState(paint)
                }

                if (paint.bgColor != Color.TRANSPARENT) {
                    val color = paint.color
                    paint.color = paint.bgColor
                    val width = paint.measureText(text, start, end)
                    canvas.drawRect(x, (top + ascent).toFloat(), x + width, (bottom - descent).toFloat(), paint)
                    paint.color = color
                }
            }
        }
    }
}
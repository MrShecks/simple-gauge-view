package ie.justonetech.simplegaugeview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import kotlin.math.abs

///////////////////////////////////////////////////////////////////////////////////////////////////
// SimpleGaugeView
// Simple gauge view custom control
///////////////////////////////////////////////////////////////////////////////////////////////////

class SimpleGaugeView : View {

    interface OnValueChangeListener {
        fun onValueChanged(view: SimpleGaugeView, value: Int)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties configurable at runtime
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    var value: Int
        set(value) {
            if(value in minValue..maxValue) {
                field = value
                invalidate()

                onValueChangeListener?.onValueChanged(this, field)
            }
        }

    @ColorInt
    var barColor: Int
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var fillColor: Int
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var textColor: Int
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var labelColor: Int
        set(value) {
            field = value
            invalidate()
        }

    var labelText = String()
        set(value) {
            field = value
            invalidate()
        }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Properties configurable via XML
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    var minValue: Int
        private set

    var maxValue: Int
        private set

    private var showValue: Boolean

    private var startAngle: Float
    private var sweepAngle: Float

    private var barWidth: Float
    private var fillBarWidth: Float

    private var textSize: Float
    private var labelSize: Float

    private var textOffset: Int

    private var barStrokeCap: Paint.Cap

    @ColorInt
    private var fillColorStart: Int

    @ColorInt
    private var fillColorEnd: Int

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private val painter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val drawingRect = RectF()
    private val textBounds = Rect()

    private var isInitialising = false
    private var isAnimating = false

    private var gradientShader: Shader? = null
    private var onValueChangeListener: OnValueChangeListener? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SimpleGaugeView, defStyleAttr, 0).apply {
            try {
                isInitialising = true

                barWidth = getDimension(R.styleable.SimpleGaugeView_gaugeView_barWidth, DEFAULT_BAR_WIDTH)
                fillBarWidth = getDimension(R.styleable.SimpleGaugeView_gaugeView_fillBarWidth, barWidth * 2)

                barColor = getColor(R.styleable.SimpleGaugeView_gaugeView_barColor, DEFAULT_BAR_COLOR)
                fillColor = getColor(R.styleable.SimpleGaugeView_gaugeView_fillColor, DEFAULT_FILL_COLOR)

                fillColorStart = getColor(R.styleable.SimpleGaugeView_gaugeView_fillColorStart, Color.TRANSPARENT)
                fillColorEnd = getColor(R.styleable.SimpleGaugeView_gaugeView_fillColorEnd, Color.TRANSPARENT)

                getInt(R.styleable.SimpleGaugeView_gaugeView_strokeCap, 0).also {
                    barStrokeCap = when(it) {
                        1       -> Paint.Cap.ROUND
                        2       -> Paint.Cap.SQUARE
                        else    -> Paint.Cap.BUTT
                    }
                }

                minValue = getInt(R.styleable.SimpleGaugeView_gaugeView_minValue, 0)
                maxValue = getInt(R.styleable.SimpleGaugeView_gaugeView_maxValue, DEFAULT_MAX_VALUE)
                showValue = getBoolean(R.styleable.SimpleGaugeView_gaugeView_showValue, true)
                value = getInt(R.styleable.SimpleGaugeView_gaugeView_value, minValue)

                startAngle = getFloat(R.styleable.SimpleGaugeView_gaugeView_startAngle, DEFAULT_START_ANGLE)
                sweepAngle = getFloat(R.styleable.SimpleGaugeView_gaugeView_sweepAngle, DEFAULT_SWEEP_ANGLE)

                textSize = getTextSize(R.styleable.SimpleGaugeView_gaugeView_textSize, barWidth * DEFAULT_VALUE_TEXT_SCALE)
                textColor = getColor(R.styleable.SimpleGaugeView_gaugeView_textColor, fillColor)
                textOffset = getDimensionPixelOffset(R.styleable.SimpleGaugeView_gaugeView_textOffset, 0)

                labelSize = getTextSize(R.styleable.SimpleGaugeView_gaugeView_labelSize, barWidth * DEFAULT_LABEL_TEXT_SCALE)
                labelColor = getColor(R.styleable.SimpleGaugeView_gaugeView_labelColor, barColor)
                labelText = getString(R.styleable.SimpleGaugeView_gaugeView_labelText) ?: String()

                isInitialising = false
                invalidate()

            } finally {
                recycle()
            }

            isSaveEnabled = true
        }
    }

    override fun onDraw(canvas: Canvas) {

        drawBackground(canvas, drawingRect)
        drawForeground(canvas, drawingRect)
        drawTextLabels(canvas, drawingRect)
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)

        drawingRect.set(
            paddingLeft + barWidth,
            paddingTop + barWidth,
            w - paddingRight - barWidth,
            h - paddingBottom - barWidth
        )

        //
        // If the user has specified start and end fill colors in the XML layout
        // then we will use a linear gradient shader to fill the gauge foreground bar
        //

        if(fillColorStart != Color.TRANSPARENT && fillColorEnd != Color.TRANSPARENT) {
            gradientShader = LinearGradient(
                0f,
                0f,
                w.toFloat(),
                h.toFloat(),
                fillColorStart,
                fillColorEnd,
                Shader.TileMode.CLAMP
            )
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        //
        // Note: Only saving the state that can change at runtime.
        // The parameters defined in XML are not saved as they will
        // be read when the view is recreated.
        //

        return Bundle().apply {
            putParcelable("superState", superState)
            putInt("value", value)
            putInt("maxValue", maxValue)
            putInt("barColor", barColor)
            putInt("fillColor", fillColor)
            putInt("textColor", textColor)
            putInt("labelColor", labelColor)
            putString("labelText", labelText)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        isInitialising = true

        with(state as Bundle) {
            super.onRestoreInstanceState(getParcelable("superState"))

            value = getInt("value")
            maxValue = getInt("maxValue")
            barColor = getInt("barColor")
            fillColor = getInt("fillColor")
            textColor = getInt("textColor")
            labelColor = getInt("labelColor")
            labelText = getString("labelText", "")
        }

        isInitialising = false
        invalidate()
    }

    override fun invalidate() {
        if(!isInitialising)
            super.invalidate()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    fun animateTo(toValue: Int, animateTime: Long = 0) {

        if(value != toValue) {
            if (animateTime == 0L) {
                value = toValue

            } else if (!isAnimating) {
                ValueAnimator.ofInt(value, toValue).apply {
                    addUpdateListener {
                        value = it.animatedValue as Int
                    }

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            isAnimating = true
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            isAnimating = false
                        }
                    })

                    //
                    // Note: 'animateTime' is the total time it should take to sweep the full
                    // range of the gauge. The duration of the animation for this move should
                    // be a percentage of the total time based on much we are moving. E.g a
                    // move across 25% of the range should only take 25% of 'animateTime' etc.
                    //

                    duration = (animateTime * (abs(toValue - value) / (maxValue - minValue).toFloat())).toLong()
                    interpolator = LinearInterpolator()
                    start()
                }
            }
        }
    }

    fun setOnValueChangeListener(onValueChangeListener: OnValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun drawForeground(canvas: Canvas, rect: RectF) {
        val sweepAmount = (value.toFloat() / (maxValue - minValue)) * sweepAngle

        painter.strokeCap = barStrokeCap
        painter.strokeWidth = fillBarWidth

        //
        // If there is a gradient shader available then use it to paint
        // the gauge foreground arc, otherwise we paint it with a solid fill color
        //

        if(gradientShader != null)
            painter.shader = gradientShader
        else
            painter.color = fillColor

        canvas.drawArc(rect, startAngle, sweepAmount, false, painter)

        painter.shader = null
    }

    private fun drawTextLabels(canvas: Canvas, rect: RectF) {

        painter.style = Paint.Style.FILL

        if(showValue) {
            val valueText = String.format("%d", value)

            painter.color = textColor
            painter.textSize = textSize
            painter.getTextBounds(valueText, 0, valueText.length, textBounds)

            val textPosX = rect.centerX() - (textBounds.width() * 0.5f)
            var textPosY = textOffset + rect.centerY()

            if (labelText.isEmpty())
                textPosY += textBounds.height() * 0.5f

            canvas.drawText(valueText, textPosX, textPosY, painter)
        }

        if(labelText.isNotEmpty()) {
            painter.color = labelColor
            painter.textSize = labelSize
            painter.getTextBounds(labelText, 0, labelText.length, textBounds)

            val labelPosX = rect.centerX() - (textBounds.width() * 0.5f)
            val labelPosY = textOffset + rect.centerY() + textBounds.height() + paddingTop

            canvas.drawText(labelText, labelPosX, labelPosY, painter)
        }

        //
        // DEBUG
        //

//        painter.strokeWidth = 1f
//        painter.color = Color.BLACK
//        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, painter)
//        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, painter)
    }

    private fun drawBackground(canvas: Canvas, rect: RectF) {
        background?.draw(canvas)

        painter.color = barColor
        painter.style = Paint.Style.STROKE
        painter.strokeCap = barStrokeCap
        painter.strokeWidth = barWidth

        canvas.drawArc(rect, startAngle, sweepAngle, false, painter)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private fun TypedArray.getTextSize(index: Int, defaultSize: Float): Float {
        return getDimensionPixelSize(index, defaultSize.toInt()).toFloat()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        val TAG = SimpleGaugeView::class.java.simpleName

        private const val DEFAULT_VALUE_TEXT_SCALE: Float   = 4.0f
        private const val DEFAULT_LABEL_TEXT_SCALE: Float   = 3.0f

        private const val DEFAULT_START_ANGLE: Float        = 135f
        private const val DEFAULT_SWEEP_ANGLE: Float        = 270f

        private const val DEFAULT_FILL_COLOR: Int           = 0xff4d6ea3.toInt()
        private const val DEFAULT_BAR_COLOR: Int            = 0xffc2c2c2.toInt()

        private const val DEFAULT_BAR_WIDTH: Float          = 12f

        private const val DEFAULT_MAX_VALUE: Int            = 100
    }
}
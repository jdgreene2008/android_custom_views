package com.jarvis.dragdropresearch.funwithshapes

import com.jarvis.dragdropresearch.funwithshapes.enums.ColorPalette
import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator
import java.util.*

abstract class FlashShape(type: Type) {

    private val mType = type

    private var mXOffset: Int = 0

    private var mYOffset: Int = 0

    private var mColorInterpolator: ColorInterpolator? = null

    private var mAlphaInterpolator: AlphaInterpolator? = null

    private var mAllowMulticoloredComponents: Boolean = false

    private var mComponentColorPool = COMPONENT_COLOR_POOL_DEFAULT

    private var mComponentColors: IntArray? = null

    init {
        mComponentColors = mComponentColorPool
    }

    fun getType(): Type {
        return mType
    }

    fun getXOffset(): Int {
        return mXOffset
    }

    fun setXOffset(XOffset: Int) {
        mXOffset = XOffset
    }

    fun getYOffset(): Int {
        return mYOffset
    }

    fun setYOffset(YOffset: Int) {
        mYOffset = YOffset
    }

    fun getComponentColors(): IntArray? {
        return mComponentColors
    }

    fun getColorInterpolator(): ColorInterpolator? {
        return mColorInterpolator
    }

    fun setColorInterpolator(colorInterpolator: ColorInterpolator) {
        mColorInterpolator = colorInterpolator
    }

    fun getAlphaInterpolator(): AlphaInterpolator? {
        return mAlphaInterpolator
    }

    fun setAlphaInterpolator(alphaInterpolator: AlphaInterpolator) {
        mAlphaInterpolator = alphaInterpolator
    }

    /**
     * @param componentColors Array of colors to be used for coloring each segment. The segments will be
     * colored in order of componentColors[segmentIndex % componentColors.length] to account for the
     * total segment count being greater than the number of segment colors.
     */
    fun setComponentColors(componentColors: IntArray) {
        mComponentColors = componentColors
    }

    fun getComponentColorPool(): IntArray {
        return mComponentColorPool
    }

    /**
     * @return Maximum number of components that this shape can be composed of.
     */
    abstract fun getMaxComponents(): Int

    fun allowMultiColoredComponents(): Boolean {
        return mAllowMulticoloredComponents
    }

    /**
     * @param allowMulticoloredComponents Set to true if individual segment colors should be respected.
     * If false,the default color used to draw the spiral will be used for all segments.
     */
    fun setAllowMulticoloredComponents(allowMulticoloredComponents: Boolean) {
        mAllowMulticoloredComponents = allowMulticoloredComponents
    }

    /**
     * @param componentColorPool Array of colors to be used in conjunction with
     * [SpiralShape.generateRandomComponentColors]. If null or the length is
     * equal to 0, the default color pool will be used.
     *
     * @see .COMPONENT_COLOR_POOL_DEFAULT
     */
    fun setComponentColorPool(componentColorPool: IntArray?) {
        if (componentColorPool != null && componentColorPool.isNotEmpty()) {
            mComponentColorPool = componentColorPool
        } else {
            mComponentColorPool = COMPONENT_COLOR_POOL_DEFAULT
        }
    }

    /**
     * Generate a random array of colors of size.
     * This array will be used to color the segments, with array[i] coloring segment[i].
     */
    fun generateRandomComponentColors() {
        val componentColors = IntArray(getMaxComponents())

        val random = Random(System.currentTimeMillis())

        for (i in componentColors.indices) {
            componentColors[i] =
                mComponentColorPool[random.nextInt(300) % mComponentColorPool.size]
        }

        mComponentColors = componentColors
    }

    companion object {
        private val COMPONENT_COLOR_POOL_DEFAULT = ColorPalette.ONE.values
    }

    enum class Type {
        ARC,
        RECTANGLE,
        SPIRAL,
        STAR,
        TRIANGLE
    }
}

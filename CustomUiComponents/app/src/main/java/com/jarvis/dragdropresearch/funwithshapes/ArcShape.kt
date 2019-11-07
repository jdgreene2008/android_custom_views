package com.jarvis.dragdropresearch.funwithshapes

import com.jarvis.dragdropresearch.interpolators.AngleInterpolator

class ArcShape : FlashShape(Type.ARC) {

    var angleInterpolator:AngleInterpolator? = null

    override fun getMaxComponents(): Int = MAX_COMPONENT_COUNT

    companion object {
        private val MAX_COMPONENT_COUNT: Int = 10
    }
}

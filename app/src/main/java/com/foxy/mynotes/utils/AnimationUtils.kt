package com.foxy.mynotes.utils

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.foxy.mynotes.R
import kotlin.math.hypot
import kotlin.math.sqrt

fun View.registerAnimation(context: Context?) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)

            val radius = hypot(right.toDouble(), bottom.toDouble()).toFloat()
            val reveal: Animator =
                ViewAnimationUtils.createCircularReveal(view, right, bottom, 0f, radius).apply {
                    interpolator = DecelerateInterpolator(2f)
                    duration = getMediumDuration(context)
                }
            reveal.start()
            startBackgroundColorAnimation(
                view,
                getColor(context, R.color.colorPrimary),
                Color.WHITE, getMediumDuration(context)
            )
        }

    })
}

fun registerExitAnimation(context: Context?, view: View) {
    val width = view.width.toDouble()
    val height = view.height.toDouble()
    val radius = sqrt(width * width + height * height).toFloat()
    val anim: Animator =
        ViewAnimationUtils.createCircularReveal(view, width.toInt(), height.toInt(), radius, 0f)
            .apply {
                duration = getMediumDuration(context)
                interpolator = FastOutSlowInInterpolator()
            }
    anim.start()
    startBackgroundColorAnimation(
        view,
        Color.WHITE,
        getColor(context, R.color.colorPrimary),
        getMediumDuration(context)
    )
}

private fun getMediumDuration(context: Context?): Long {
    return context!!.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
//    return 3000
}

private fun getColor(context: Context?, colorId: Int): Int {
    return ContextCompat.getColor(context!!, colorId)
}

private fun startBackgroundColorAnimation(view: View, startColor: Int, endColor: Int, duration: Long) {
    val anim = ValueAnimator().apply {
        setIntValues(startColor, endColor)
        setEvaluator(ArgbEvaluator())
        setDuration(duration)
        addUpdateListener { view.setBackgroundColor(it.animatedValue as Int) }
    }
    anim.start()
}
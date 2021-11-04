package com.example.mybooks.View.Animations

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.mybooks.R


fun View.animAppear(context: Context, duration: Int): Unit {
    val animations = AnimationUtils.loadAnimation(context, R.anim.anim_appear)
    animations.duration = duration.toLong()

    this.visibility = View.VISIBLE
    this.animation = animations

}

fun View.animVanish(context: Context, duration: Int): Unit {
    val animations = AnimationUtils.loadAnimation(context, R.anim.anim_vanish)
    this.visibility = View.INVISIBLE
    animations.duration = duration.toLong()
    this.animation = animations
}

fun View.animTraslateToLeft(context: Context, duration: Int): Unit {
    val animations = AnimationUtils.loadAnimation(context, R.anim.anim_traslate_toleft)
    this.visibility = View.VISIBLE
    animations.duration = duration.toLong()
    this.animation = animations
}

fun View.animTraslateToBottomOrUp(isUp: Boolean , duration: Long) {
    val moveY = if (isUp)  0  else  this.height * 2
    this.animate().setStartDelay(0).setDuration(duration).translationY(moveY.toFloat()).start();

}
fun View.animatioTraslateToLeftOrRight(show:Boolean,duration :Long){
    val moveX=if (show) 0 else this.width*-2
    this.animate().translationX(moveX.toFloat()).setStartDelay(0).setDuration(duration).start()
}

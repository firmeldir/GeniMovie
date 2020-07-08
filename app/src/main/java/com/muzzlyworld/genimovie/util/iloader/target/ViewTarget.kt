package com.muzzlyworld.genimovie.util.iloader.target

import android.graphics.drawable.Drawable
import android.view.View

interface ViewTarget<out T : View> {

    val view: T

    fun onStart(placeholder: Drawable?) { }

    fun onSuccess(result: Drawable) { }

    fun onError(error: Drawable?) { }
}

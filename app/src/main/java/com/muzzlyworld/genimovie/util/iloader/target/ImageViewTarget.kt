package com.muzzlyworld.genimovie.util.iloader.target

import android.graphics.drawable.Drawable
import android.widget.ImageView

class ImageViewTarget(
    override val view: ImageView
) : ViewTarget<ImageView> {

    override fun onStart(placeholder: Drawable?) = placeholder?.let { view.setImageDrawable(it) } ?: Unit

    override fun onSuccess(result: Drawable) = view.setImageDrawable(result)

    override fun onError(error: Drawable?) = view.setImageDrawable(error)
}
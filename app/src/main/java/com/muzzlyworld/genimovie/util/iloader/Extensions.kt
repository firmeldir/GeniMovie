package com.muzzlyworld.genimovie.util.iloader

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.muzzlyworld.genimovie.util.iloader.target.ImageViewTarget

@JvmSynthetic
inline fun ImageView.load(
    uri: Uri,

    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) = ImageLoader.get(context).process(
    ImageLoader.Request(
        imageUri = uri,
        target = ImageViewTarget(this),

        placeholder = placeholder,
        error = error
    )
)

@JvmSynthetic
inline fun ImageView.fill(
    uri: Uri?,

    @DrawableRes error: Int,
    @DrawableRes placeholder: Int? = null
) = uri?.let { load(uri, placeholder, error) } ?: kotlin.run {
    this.setImageResource(error)
}
package com.muzzlyworld.genimovie.util.iloader

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.muzzlyworld.genimovie.util.iloader.target.ImageViewTarget

@JvmSynthetic
fun ImageView.load(
    uri: Uri,

    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,

    extraOptions: List<ExtraLoaderOptions> = emptyList()
) = ImageLoader.get(context).process(
    ImageRequest(
        imageUri = uri,
        target = ImageViewTarget(this),

        placeholder = placeholder,
        error = error,

        extraOptions = extraOptions
    )
)

@JvmSynthetic
fun ImageView.fill(
    uri: Uri?,

    @DrawableRes error: Int,
    @DrawableRes placeholder: Int? = null,

    extraOptions: List<ExtraLoaderOptions> = emptyList()
) = uri?.let { load(uri, placeholder, error, extraOptions) } ?: kotlin.run {
    this.setImageResource(error)
}
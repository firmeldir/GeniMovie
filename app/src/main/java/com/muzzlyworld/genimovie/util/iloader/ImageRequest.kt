package com.muzzlyworld.genimovie.util.iloader

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.muzzlyworld.genimovie.util.iloader.target.ViewTarget

enum class ExtraLoaderOptions{ Blur }

data class ImageRequest @JvmOverloads constructor(
    val imageUri: Uri,
    val target: ViewTarget<View>,

    @DrawableRes val placeholder: Int? = null,
    @DrawableRes val error: Int? = null,

    val extraOptions: List<ExtraLoaderOptions> = emptyList()
){

    val requestLifecycle = target.view.findViewTreeLifecycleOwner()?.lifecycle ?: findLifecycle() ?: GlobalLifecycle

    private fun findLifecycle(): Lifecycle? {
        var context: Context? = target.view.context
        while (true) {
            when (context) {
                is LifecycleOwner -> return context.lifecycle
                !is ContextWrapper -> return null
                else -> context = context.baseContext
            }
        }
    }
}
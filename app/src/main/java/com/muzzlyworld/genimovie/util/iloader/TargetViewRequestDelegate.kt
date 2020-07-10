package com.muzzlyworld.genimovie.util.iloader

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

internal class TargetViewRequestDelegate(
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest,
    private val lifecycle: Lifecycle,
    private val job: Job
) : DefaultLifecycleObserver {

    fun restart() {
        imageLoader.process(imageRequest)
    }

    fun dispose() {
        job.cancel()
        if (imageRequest.target is LifecycleObserver) {
            lifecycle.removeObserver(imageRequest.target)
        }
        lifecycle.removeObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) = dispose()
}
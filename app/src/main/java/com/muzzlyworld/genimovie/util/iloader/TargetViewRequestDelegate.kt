package com.muzzlyworld.genimovie.util.iloader

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

internal class TargetViewRequestDelegate(
    private val imageLoader: ImageLoader,
    private val request: ImageLoader.Request,
    private val lifecycle: Lifecycle,
    private val job: Job
) : DefaultLifecycleObserver {

    fun restart() {
        imageLoader.process(request)
    }

    fun dispose() {
        job.cancel()
        if (request.target is LifecycleObserver) {
            lifecycle.removeObserver(request.target)
        }
        lifecycle.removeObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) = dispose()
}
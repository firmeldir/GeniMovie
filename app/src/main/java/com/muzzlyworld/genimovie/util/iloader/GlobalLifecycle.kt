package com.muzzlyworld.genimovie.util.iloader

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
*  A [Lifecycle] implementation that is always resumed and never destroyed.
*/

internal object GlobalLifecycle : Lifecycle() {

    private val owner = LifecycleOwner { this }

    override fun addObserver(observer: LifecycleObserver) {
        if(observer !is DefaultLifecycleObserver) return
        observer.onCreate(owner)
        observer.onStart(owner)
        observer.onResume(owner)
    }

    override fun removeObserver(observer: LifecycleObserver) { }

    override fun getCurrentState() = State.RESUMED
}

package com.muzzlyworld.genimovie.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.muzzlyworld.genimovie.model.ViewState
import com.muzzlyworld.genimovie.util.model.Event

abstract class StatefulViewModel <S : ViewState> : ViewModel(){

    abstract fun idleViewState(): S

    private val _viewState = MediatorLiveData<S>().apply { value = idleViewState() }
    val viewState: LiveData<S> get() = _viewState

    private val _errorMessage = MediatorLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage


    protected fun withState(block: (state: S) -> Unit){
        block(_viewState.value!!)
    }

    protected fun setState(reducer: S.() -> S){
        _viewState.value = reducer(_viewState.value!!)
    }


    protected fun sendErrorMessage(){ _errorMessage.value =
        Event("Unknown Error. Check Internet connection")
    }
}
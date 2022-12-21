package com.perennial.movieapp.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

fun Activity.toast(activity: Activity, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(activity, message, duration).show()
}
fun Context.lifecycleOwner(): LifecycleOwner? {
    var curContext = this
    var maxDepth = 20
    while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
        curContext = (curContext as ContextWrapper).baseContext
    }
    return if (curContext is LifecycleOwner) {
        curContext as LifecycleOwner
    } else {
        null
    }
}

inline fun <IN : Any, OUT : Any> Context.observeStateOf  (
    liveData: ActionStateLiveData<IN, OUT>,
    crossinline onChanged: (UIState<OUT?>) -> Unit
) {
    lifecycleOwner()?.let {
        liveData.state.observe(it) {
        onChanged(it)
    }
    }
}


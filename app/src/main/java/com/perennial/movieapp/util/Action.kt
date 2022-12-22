package com.perennial.movieapp.util

sealed class Action {
    object Load : Action()
    object Refresh : Action()
    object Retry : Action()
}
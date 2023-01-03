package design_pattern.observer

import kotlin.properties.Delegates

class TextView {

    val listeners = mutableListOf<TextChangedListener>()

    var text: String by Delegates.observable("empty") { _, old, new ->
        listeners.forEach { it.onTextChanged(old, new) }
    }
}
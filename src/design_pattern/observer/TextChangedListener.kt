package design_pattern.observer

interface TextChangedListener {
    fun onTextChanged(oldText: String, newText: String)
}
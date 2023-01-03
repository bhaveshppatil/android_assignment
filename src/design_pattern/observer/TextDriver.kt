package design_pattern.observer

fun main() {
    val listener = PrintingTextChangedListener()

    val textView = TextView().apply {
        listeners.add(listener)
    }

    with(textView) {
        text = "Year 2022"
        text = "Year 2023"
    }
}
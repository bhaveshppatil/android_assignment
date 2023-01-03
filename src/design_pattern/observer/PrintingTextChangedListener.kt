package design_pattern.observer

class PrintingTextChangedListener: TextChangedListener {

    private var text = ""
    override fun onTextChanged(oldText: String, newText: String) {
        text = "Text is changed $oldText to $newText"
        println(text)
    }
}
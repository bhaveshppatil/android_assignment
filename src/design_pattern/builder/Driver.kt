package design_pattern.builder

fun main() {
    val laptop1 = PersonalComputer.Builder("i7", "1TB SSD")
        .setBrandName("Asus ROG")
        .os("Windows 11")
        .setRam("16GB")
        .setScreenSize("14inch")
        .keyboardType("BackLite RGB")
        .setBattery("3 cell")
        .build()
    println(laptop1)
    val laptop2 = PersonalComputer.Builder("i3", "1TB HDD")
        .setBrandName("Asus Vivobook")
        .os("Windows 11")
        .setRam("16GB")
        .setScreenSize("14inch")
        .keyboardType("BackLite RGB")
        .setBattery("3 cell")
        .build()
    println(laptop2)
    val laptop3 = PersonalComputer.Builder("i5", "1TB SSD")
        .setBrandName("Asus Zenbook")
        .os("Windows 11")
        .setRam("16GB")
        .setScreenSize("14inch")
        .keyboardType("BackLite RGB")
        .setBattery("3 cell")
        .build()
    println(laptop3)

}


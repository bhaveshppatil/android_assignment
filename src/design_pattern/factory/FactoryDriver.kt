package design_pattern.factory

fun main() {
    val tataFactory = TataFactory()
    val altrozCar = tataFactory.getCarInstance("AltrozCar".toUpperCase())
    altrozCar?.enginePower()
    altrozCar?.airBags()
    altrozCar?.seat()
    val harrierCar = tataFactory.getCarInstance("HarrierCar".toUpperCase())
    harrierCar?.enginePower()
    harrierCar?.airBags()
    harrierCar?.seat()
    val nexonCar = tataFactory.getCarInstance("NexonCar".toUpperCase())
    nexonCar?.enginePower()
    nexonCar?.airBags()
    nexonCar?.seat()
}
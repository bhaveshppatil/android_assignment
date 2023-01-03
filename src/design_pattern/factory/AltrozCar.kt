package design_pattern.factory

class AltrozCar: Specification {
    override fun enginePower() {
        println("Altroz has 1199.90cc power")
    }
    override fun airBags() {
        println("Altroz has 3 airbags")
    }
    override fun seat() {
        println("Altroz is a 5 seater car")
    }
}
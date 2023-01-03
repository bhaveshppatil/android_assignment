package design_pattern.factory

class NexonCar: Specification {
    override fun enginePower() {
        println("Nexon has 999.96cc power")
    }

    override fun airBags() {
        println("Nexon has 4 airbags")
    }

    override fun seat() {
        println("Nexon is 4 seater car")
    }
}
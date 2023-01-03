package design_pattern.factory

class HarrierCar: Specification {
    override fun enginePower() {
        println("Harrier has 1499.98cc power")
    }

    override fun airBags() {
        println("Harrier has 6 air bags")
    }

    override fun seat() {
        println("Harrier is 6 seater car")
    }
}
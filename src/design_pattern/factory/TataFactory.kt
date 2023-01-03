package design_pattern.factory

import java.awt.Rectangle

import java.awt.Shape


class TataFactory {
    fun getCarInstance(type: String?): Specification? {
        when(type){
            "NexonCar".uppercase() -> return NexonCar()
            "HarrierCar".uppercase() -> return HarrierCar()
            "AltrozCar".uppercase() -> return AltrozCar()
        }
        return null
    }
}
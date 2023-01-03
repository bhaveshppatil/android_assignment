package design_pattern.builder

class PersonalComputer(builder: Builder) {
    private val brand: String = builder.brand
    private val processor: String = builder.processor
    private val storage: String = builder.storage
    private val keyBoardType: String = builder.keyboardType
    private val os: String = builder.os
    private val ram: String = builder.ram
    private val batteryCell: String = builder.batteryCell
    private val screenSize: String = builder.screenSize

    override fun toString(): String {
        return "$brand - $processor, $ram, $os, $storage, $keyBoardType $batteryCell $screenSize"
    }
    class Builder(processor: String, storage: String) {
        var processor: String = processor
        var storage: String = storage
        var brand: String = "Asus"
        var ram: String = "8G"
        var keyboardType: String = "BackLite"
        var os: String = "Windows 10"
        var batteryCell: String = "2 cell"
        var screenSize: String = "17inch"
        fun setRam(ram: String): Builder {
            this.ram = ram
            return this
        }

        fun  setBrandName(brandName: String): Builder {
            this.brand = brandName
            return this
        }
        fun keyboardType(keyboardType: String): Builder {
            this.keyboardType = keyboardType
            return this
        }
        fun os(os: String): Builder {
            this.os = os
            return this
        }
        fun setBattery(batteryCapacity: String): Builder {
            this.batteryCell = batteryCapacity
            return this
        }

        fun setScreenSize(screenSize: String): Builder {
            this.screenSize = screenSize
            return this
        }

        fun build(): PersonalComputer {
            return PersonalComputer(this)
        }
    }
}

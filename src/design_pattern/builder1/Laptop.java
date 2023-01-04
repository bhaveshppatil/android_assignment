package design_pattern.builder1;

public class Laptop {
    private final String brand;
    private final String processor;
    private final String ram;
    private final String storage;
    private final String operatingSys;
    private final String battery;
    private final String keyboard;

    public String getBrand() {
        return brand;
    }

    public String getProcessor() {
        return processor;
    }

    public String getRam() {
        return ram;
    }

    public String getStorage() {
        return storage;
    }

    public String getBattery() {
        return battery;
    }

    public String getKeyboard() {
        return keyboard;
    }
    public String getOperatingSys() {
        return operatingSys;
    }
    private Laptop(LaptopBuilder builder) {
        this.brand = builder.brand;
        this.processor = builder.processor;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.battery = builder.battery;
        this.keyboard = builder.keyboard;
        this.operatingSys = builder.operatingSys;
    }

    public static class LaptopBuilder {
        private String brand;
        private String processor;
        private String ram;
        private String storage;
        private String operatingSys;
        private String battery;
        private String keyboard;

        public LaptopBuilder(String brand){
            this.brand = brand;
        }
        public LaptopBuilder setProcessor(String processor) {
            this.processor = processor;
            return this;
        }

        public LaptopBuilder setRam(String ram) {
            this.ram = ram;
            return this;
        }

        public LaptopBuilder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public LaptopBuilder setOperatingSys(String operatingSys) {
            this.operatingSys = operatingSys;
            return this;
        }
        public LaptopBuilder setBattery(String battery) {
            this.battery = battery;
            return this;
        }

        public LaptopBuilder setKeyboard(String keyboard) {
            this.keyboard = keyboard;
            return this;
        }
        public Laptop build() {
            return new Laptop(this);
        }
    }
}
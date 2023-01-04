package design_pattern.builder1;

public class PersonDriver {
    public static void main(String[] args) {
        Laptop.LaptopBuilder builder = new Laptop.LaptopBuilder("Mackbook Pro")
                .setProcessor("M2")
                .setRam("16GB")
                .setStorage("512GB SSD");

        Laptop laptop = builder.build();
        System.out.println(laptop.getBrand());
        System.out.println(laptop.getProcessor());
        System.out.println(laptop.getRam());
        System.out.println(laptop.getStorage());
    }
}

package design_pattern.builder1;

public class Person {
    private final String name;
    private final String degree;
    private final String car;
    private final String house;

    public String getName() {
        return name;
    }

    public String getDegree() {
        return degree;
    }

    public String getCar() {
        return car;
    }

    public String getHouse() {
        return house;
    }

    private Person(PersonBuilder builder) {
        this.name = builder.name;
        this.degree = builder.degree;
        this.car = builder.car;
        this.house = builder.house;
    }

    public static class PersonBuilder {
        private String name;
        private String degree;
        private String car;
        private String house;

        public PersonBuilder(String name) {
            this.name = name;
        }

        public PersonBuilder setDegree(String degree) {
            this.degree = degree;
            return this;
        }

        public PersonBuilder setCar(String car) {
            this.car = car;
            return this;
        }

        public PersonBuilder setHouse(String house) {
            this.house = house;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
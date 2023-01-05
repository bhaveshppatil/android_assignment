package design_pattern.facade;

public class FoodOrderDriver {

    public static void main(String[] args) {
        OrderManagerFacade facade = new OrderManagerFacade();
        facade.startOrderProcess();
    }
}

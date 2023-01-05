package design_pattern.facade;

public class Waiter {

    public void takeOrder(){
        System.out.println("Take order from customer");
    }
    public void placeOrderToCook(){
        System.out.println("Update order to cook");
    }
    public void serveOrder(){
        System.out.println("Serve Order to customer");
    }
}

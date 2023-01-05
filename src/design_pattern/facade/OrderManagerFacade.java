package design_pattern.facade;

public class OrderManagerFacade {

    private Waiter waiter;
    private Kitchen kitchen;
    public void startOrderProcess(){
        waiter = new Waiter();
        kitchen = new Kitchen();
        waiter.takeOrder();
        waiter.placeOrderToCook();
        kitchen.cookFood();
        kitchen.signalReady();
        waiter.serveOrder();
    }
}

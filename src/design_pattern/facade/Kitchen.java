package design_pattern.facade;

public class Kitchen {
    public void cookFood(){
        System.out.println("Cook food for customer");
    }
    public void signalReady(){
        System.out.println("Order ready signal to waiter");
    }
}

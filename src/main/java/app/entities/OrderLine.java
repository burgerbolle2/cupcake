package app.entities;

public class OrderLine {
    private int orderLineId;
    private int ordersId;
    private int cupcakeId;
    private int quantity; // Mængde af cupcakes
    private double linePrice; // Pris for denne cupcake linje i ordren

    // Constructor

    public OrderLine(int orderLineId, int ordersId, int cupcakeId, int quantity, double linePrice) {
        this.orderLineId = orderLineId;
        this.ordersId = ordersId;
        this.cupcakeId = cupcakeId;
        this.quantity = quantity;
        this.linePrice = linePrice;
    }

    public OrderLine(int ordersId, int cupcakeId, int quantity, double linePrice) {
        this.ordersId = ordersId;
        this.cupcakeId = cupcakeId;
        this.quantity = quantity;
        this.linePrice = linePrice;
    }

    // Getters
    public int getOrderLineId() {
        return orderLineId;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public int getCupcakeId() {
        return cupcakeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLinePrice() {
        return linePrice;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLinePrice(double linePrice) {
        this.linePrice = linePrice;
    }
}


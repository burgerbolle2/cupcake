package app.entities;

public class OrderLineDTO {
    private int orderId;
    private int cupcakeId;
    private int quantity;
    private double linePrice;
    private String bottomName;
    private String topName;

    public OrderLineDTO(int orderId, int cupcakeId, int quantity, double linePrice, String bottomName, String topName) {
        this.orderId = orderId;
        this.cupcakeId = cupcakeId;
        this.quantity = quantity;
        this.linePrice = linePrice;
        this.bottomName = bottomName;
        this.topName = topName;
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCupcakeId() {
        return cupcakeId;
    }

    public void setCupcakeId(int cupcakeId) {
        this.cupcakeId = cupcakeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLinePrice() {
        return linePrice;
    }

    public void setLinePrice(double linePrice) {
        this.linePrice = linePrice;
    }

    public String getBottomName() {
        return bottomName;
    }

    public void setBottomName(String bottomName) {
        this.bottomName = bottomName;
    }

    public String getTopName() {
        return topName;
    }

    public void setTopName(String topName) {
        this.topName = topName;
    }
}


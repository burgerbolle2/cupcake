package app.entities;

import java.util.ArrayList;
import java.util.List;

public class OrdersDTO {
    private int orderId;
    private int userId;
    private String userEmail;
    private String orderDate;
    private double totalPrice;
    private boolean isCompleted;
    private List<OrderLineDTO> orderLines;

    public OrdersDTO(int orderId, int userId, String userEmail, String orderDate, double totalPrice, boolean isCompleted) {
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.isCompleted = isCompleted;
        this.orderLines = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<OrderLineDTO> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineDTO> orderLines) {
        this.orderLines = orderLines;
    }
}


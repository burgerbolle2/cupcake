package app.entities;

public class Orders {
        private int orderId;
        private int userId;
        private String orderDate;
        private double totalPrice;
        private boolean isCompleted;

        public Orders(int orderId, int userId, String orderDate, double totalPrice, boolean isCompleted) {
            this.orderId = orderId;
            this.userId = userId;
            this.orderDate = orderDate;
            this.totalPrice = totalPrice;
            this.isCompleted = isCompleted;
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

        @Override
        public String toString() {
            return "Order{" +
                    "orderId=" + orderId +
                    ", userId=" + userId +
                    ", orderDate='" + orderDate + '\'' +
                    ", totalPrice=" + totalPrice +
                    ", isCompleted=" + isCompleted +
                    '}';
        }
    }

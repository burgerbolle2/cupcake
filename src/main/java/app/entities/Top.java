package app.entities;

public class Top {
    private int topId;
    private String name;
    private double price;

    public Top(int topId, String name, double price) {
        this.topId = topId;
        this.name = name;
        this.price = price;
    }

    public Top(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public int getTopId() { return topId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}


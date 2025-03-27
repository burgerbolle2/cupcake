package app.entities;

public class cupcake {
    private String base;
    private String topping;
    private int basePrice;
    private int toppingPrice;

    public cupcake(String base, String topping, int basePrice, int toppingPrice) {
        this.base = base;
        this.topping = topping;
        this.basePrice = basePrice;
        this.toppingPrice = toppingPrice;
    }

    public String getBase() { return base; }
    public String getTopping() { return topping; }
    public int getBasePrice() { return basePrice; }
    public int getToppingPrice() { return toppingPrice; }

    public int getTotalPrice() {
        return basePrice + toppingPrice;
    }

    @Override
    public String toString() {
        return base + " med " + topping + " (" + getTotalPrice() + " kr)";
    }
}


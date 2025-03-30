package app.entities;

public class Cupcake {
    private int cupcakeId;
    private Bottom bottom;
    private Top top;
    private double price;

    public Cupcake(int cupcakeId, Bottom bottom, Top top) {
        this.cupcakeId = cupcakeId;
        this.bottom = bottom;
        this.top = top;
        this.price = bottom.getPrice()+top.getPrice();
    }

    public Cupcake(Bottom bottom, Top top) {
        this.bottom = bottom;
        this.top = top;
        this.price = bottom.getPrice()+top.getPrice();
    }

    public double getPrice() {
        return price;
    }

    public int getCupcakeId() {
        return cupcakeId;
    }

    public void setCupcakeId(int cupcakeId) {
        this.cupcakeId = cupcakeId;
    }

    public Bottom getBottom() {
        return bottom;
    }

    public Top getTop() {
        return top;
    }

    @Override
    public String toString() {
        return top.getName() + " & " + bottom.getName() + " Cupcake - Price: $" + getPrice();
    }


}



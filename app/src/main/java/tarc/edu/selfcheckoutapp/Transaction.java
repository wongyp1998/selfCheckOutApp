package tarc.edu.selfcheckoutapp;

public class Transaction {

    String tscID;
    String tscDate;
    String tscTime;
    Double total;
    String discountRate;
    Double subtotal;

    public Transaction() {
    }

    public String getTscID() {
        return tscID;
    }

    public void setTscID(String tscID) {
        this.tscID = tscID;
    }

    public String getTscDate() {
        return tscDate;
    }

    public void setTscDate(String tscDate) {
        this.tscDate = tscDate;
    }

    public String getTscTime() {
        return tscTime;
    }

    public void setTscTime(String tscTime) {
        this.tscTime = tscTime;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}

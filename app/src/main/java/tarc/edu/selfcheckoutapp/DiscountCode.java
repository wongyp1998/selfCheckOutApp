package tarc.edu.selfcheckoutapp;

public class DiscountCode {
    String description;
    String rate;

    public DiscountCode() {
    }

    public DiscountCode(String description, String rate) {
        this.description = description;
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}

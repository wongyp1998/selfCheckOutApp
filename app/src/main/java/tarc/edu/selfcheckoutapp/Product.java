package tarc.edu.selfcheckoutapp;

public class Product {
    String prodId;
    String prodName;
    String image;
    String weight;
    float price;
    float discount;

    public Product() {

    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public float getPrice() {
        return price;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}


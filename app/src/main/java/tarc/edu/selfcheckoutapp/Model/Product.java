package tarc.edu.selfcheckoutapp.Model;

public class Product {
    String Barcode;
    String Name;
    String Image;
    String Weight;
    Double Price;
    Double Discount;
    Integer CurrentStock;

    public Product() {

    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Double getDiscount() {
        return Discount;
    }

    public void setDiscount(Double discount) {
        Discount = discount;
    }

    public Integer getCurrentStock() {
        return CurrentStock;
    }

    public void setCurrentStock(Integer CurrentStock) {
        this.CurrentStock = CurrentStock;
    }
}


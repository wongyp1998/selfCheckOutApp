package tarc.edu.selfcheckoutapp;

public class Cart {

    private String pid;
    private String imageRef;
    private String pname;
    private String weight;
    private String price;
    private String quantity;

    public Cart() {
    }

    public Cart(String pid, String imageRef, String pname, String weight, String price, String quantity) {
        this.pid = pid;
        this.imageRef = imageRef;
        this.pname = pname;
        this.weight = weight;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImageRef() {
        return imageRef;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

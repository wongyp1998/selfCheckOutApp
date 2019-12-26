package tarc.edu.selfcheckoutapp.Model;

public class Transaction implements Comparable<Transaction> {

    String tscID;
    String tscDate;
    String tscTime;
    Double total;
    String discountRate;
    Double subtotal;
    Integer verifyStatus;
    String paymentMethod;
    String customerPhone;
    Long timeStamp;

    @Override
    public int compareTo(Transaction compareTsc) {
        return this.timeStamp.compareTo(compareTsc.timeStamp);
    }


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

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


}

package tarc.edu.selfcheckoutapp.Model;

public class Voucher {

    String Code;
    String header;
    String Desc;
    Long expiryDate;
    Integer LimitPerUser;
    Integer totalRedemption;
    Integer maxDiscount;
    Integer minPurchase;
    String rate;

    public Voucher() {
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Integer maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(Integer minPurchase) {
        this.minPurchase = minPurchase;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public Integer getLimitPerUser() {
        return LimitPerUser;
    }

    public void setLimitPerUser(Integer limitPerUser) {
        LimitPerUser = limitPerUser;
    }

    public Integer getTotalRedemption() {
        return totalRedemption;
    }

    public void setTotalRedemption(Integer totalRedemption) {
        this.totalRedemption = totalRedemption;
    }
}

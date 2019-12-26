package tarc.edu.selfcheckoutapp.Model;

public class DiscountCode {
    String Desc;
    String rate;
    Integer limit;
    String Code;

    public DiscountCode() {
    }

    public DiscountCode(String desc, String rate, Integer limit, String code) {
        Desc = desc;
        this.rate = rate;
        this.limit = limit;
        Code = code;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}

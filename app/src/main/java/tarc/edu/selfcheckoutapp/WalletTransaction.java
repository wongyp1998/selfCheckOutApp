package tarc.edu.selfcheckoutapp;

public class WalletTransaction {

    String wTscDesc,wTscID,wTscAmount,wTscStatus;
    Long wTscDateTime;

    public WalletTransaction() {
    }

    public String getwTscDesc() {
        return wTscDesc;
    }

    public void setwTscDesc(String wTscDesc) {
        this.wTscDesc = wTscDesc;
    }

    public String getwTscID() {
        return wTscID;
    }

    public void setwTscID(String wTscID) {
        this.wTscID = wTscID;
    }

    public Long getwTscDateTime() {
        return wTscDateTime;
    }

    public void setwTscDateTime(Long wTscDateTime) {
        this.wTscDateTime = wTscDateTime;
    }

    public String getwTscAmount() {
        return wTscAmount;
    }

    public void setwTscAmount(String wTscAmount) {
        this.wTscAmount = wTscAmount;
    }

    public String getwTscStatus() {
        return wTscStatus;
    }

    public void setwTscStatus(String wTscStatus) {
        this.wTscStatus = wTscStatus;
    }
}

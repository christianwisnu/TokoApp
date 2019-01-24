package model.master;

import java.io.Serializable;

public class MerchantDetailModel implements Serializable {

    private String id;
    private String day;
    private String open;
    private String close;
    private Integer active;
    private String cstat;
    private String cdstatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getCstat() {
        return cstat;
    }

    public void setCstat(String cstat) {
        this.cstat = cstat;
    }

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }
}

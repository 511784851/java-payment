package com.blemobi.payment.model;

public class Red {
    private String custorderno;

    private String senduuid;

    private String receiveuuid;

    private Integer amount;

    private String title;

    private Long sendtime;

    private Long receivetime;

    private Long invalidtime;

    private Integer status;

    public String getCustorderno() {
        return custorderno;
    }

    public void setCustorderno(String custorderno) {
        this.custorderno = custorderno == null ? null : custorderno.trim();
    }

    public String getSenduuid() {
        return senduuid;
    }

    public void setSenduuid(String senduuid) {
        this.senduuid = senduuid == null ? null : senduuid.trim();
    }

    public String getReceiveuuid() {
        return receiveuuid;
    }

    public void setReceiveuuid(String receiveuuid) {
        this.receiveuuid = receiveuuid == null ? null : receiveuuid.trim();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Long getSendtime() {
        return sendtime;
    }

    public void setSendtime(Long sendtime) {
        this.sendtime = sendtime;
    }

    public Long getReceivetime() {
        return receivetime;
    }

    public void setReceivetime(Long receivetime) {
        this.receivetime = receivetime;
    }

    public Long getInvalidtime() {
        return invalidtime;
    }

    public void setInvalidtime(Long invalidtime) {
        this.invalidtime = invalidtime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
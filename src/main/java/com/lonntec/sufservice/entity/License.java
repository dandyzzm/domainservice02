package com.lonntec.sufservice.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="t_domain_license")
public class License {
    @Id
    @Column(name="frowid", length = 36)
    String rowId;


    @Column(name = "fbillnumber")
    String billNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fdomainid")
    Domain domain;

    @Column(name = "fapplyusercount")
    Integer applyUserCount;

    @Column(name="fapplyexpireDate")
    Date applyExpireDate;

    @Column(name = "fmemo")
    String memo;

    @Column(name = "fcreatetime")
    Date createTime;

    @Column(name = "fbillstate")
    Integer billState;

    @Column(name = "faudittime")
    Date auditTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fauditerid")
    User auditer;

    @Column(name = "fauditmemo")
    String auditMemo;

    public License() {
        rowId= UUID.randomUUID().toString();
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Integer getApplyUserCount() {
        return applyUserCount;
    }

    public void setApplyUserCount(Integer applyUserCount) {
        this.applyUserCount = applyUserCount;
    }

    public Date getApplyExpireDate() {
        return applyExpireDate;
    }

    public void setApplyExpireDate(Date applyExpireDate) {
        this.applyExpireDate = applyExpireDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getBillState() {
        return billState;
    }

    public void setBillState(Integer billState) {
        this.billState = billState;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public User getAuditer() {
        return auditer;
    }

    public void setAuditer(User auditer) {
        this.auditer = auditer;
    }

    public String getAuditMemo() {
        return auditMemo;
    }

    public void setAuditMemo(String auditMemo) {
        this.auditMemo = auditMemo;
    }
}

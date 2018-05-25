package com.lonntec.sufservice.entity;

import javax.persistence.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name="t_domain_domainuser")
public class DomainUser {
    @Id
    @Column(name = "frowid")
    String rowId;

    @Column(name = "fusername")
    String userName;

    @Transient
    String domainUserPassword;

    @Column(name = "fpasswordhash")
    String passwordHash;

    @Column(name = "fmobile")
    String mobile;

    @Column(name = "femail")
    String email;

    public DomainUser() {
        rowId= UUID.randomUUID().toString();
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDomainUserPassword(String domainUserPassword) {
        this.domainUserPassword = domainUserPassword;
        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Data = digest.digest(this.domainUserPassword.getBytes("utf-8"));
            this.passwordHash =  Base64.getEncoder().encodeToString(md5Data);
        }catch (Exception ex){
            ex.printStackTrace();
            this.passwordHash = this.domainUserPassword;
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

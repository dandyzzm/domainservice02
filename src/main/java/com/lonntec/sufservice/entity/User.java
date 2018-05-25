package com.lonntec.sufservice.entity;

import javax.persistence.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name="t_sys_user")
public class User {
    @Id
    @Column(name="frowid", length = 36)
    String rowId;

    @Column(name="fusername")
    String username;

    @Column(name="fnickname")
    String nickname;

    @Transient
    String password;

    @Column(name="fpasswordhash")
    String passwordHash;

    @Column(name="fmobile")
    String mobile;

    @Column(name="femail")
    String email;

    @Column(name="fisadmin")
    Boolean isAdmin;

    @Column(name="fisenable")
    Boolean isEnable;

    public User() {
        rowId = UUID.randomUUID().toString();
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Data = digest.digest(this.password.getBytes("utf-8"));
            this.passwordHash =  Base64.getEncoder().encodeToString(md5Data);
        }catch (Exception ex){
            ex.printStackTrace();
            this.passwordHash = this.password;
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

}

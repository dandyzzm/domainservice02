package com.lonntec.sufservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.annotation.RequestSufToken;
import com.lonntec.sufservice.entity.License;
import com.lonntec.sufservice.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/license")
public class LicenseController {
    @Autowired
    LicenseService licenseService;

    /**
     *
     *获取授权申请列表
     */
    @RequestSufToken
    @RequestMapping("/getlist")
    public JSONArray getlist(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<License> applyFormList=licenseService.getlist(keyword,page,size);
        JSONArray revalue=new JSONArray();
        for (License item : applyFormList){
            revalue.add(item);
        }
        return revalue;
    }

    /**
     *
     * 获取授权申请数量
     */
    @RequestSufToken
    @RequestMapping("/getlistcount")
    public Integer getListCount(@PathParam("keyword") String keyword){
        return licenseService.getlistcount(keyword);
    }

    /**
     *
     * 递交授权申请
     */
    @RequestSufToken
    @RequestMapping("/apply")
    public JSONObject apply(@RequestBody JSONObject applyInfo) {
        String domainId=applyInfo.getString("domainId");
        String memo=applyInfo.getString("memo");
        Integer userCount=applyInfo.getInteger("userCount");
        Date expireDate=applyInfo.getDate("expireDate");
        License license=licenseService.apply(domainId,memo,userCount,expireDate);
        JSONObject json= JSON.parseObject(JSONObject.toJSONString(license));
        return json;
    }

    /**
     *
     * 审核授权申请
     */
    @RequestSufToken
    @RequestMapping("/auditapply")
    public License auditApply(@RequestBody JSONObject jsonObject){
        String applyId=jsonObject.getString("applyId");
        Boolean isPass=jsonObject.getBoolean("isPass");
        String auditMemo=jsonObject.getString("auditMemo");
        return licenseService.auditapply(applyId,isPass,auditMemo);
    }
}

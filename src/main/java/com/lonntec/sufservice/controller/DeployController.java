package com.lonntec.sufservice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.annotation.RequestSufToken;
import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.service.DeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/deploy")
public class DeployController {
    @Autowired
    DeployService deployService;

    /**
     * 获取开通申请列表
     */
    @RequestSufToken
    @RequestMapping("/getlist")
    public JSONArray getList(
            @PathParam("keyword") String keyword,
            @PathParam("page") Integer page,
            @PathParam("size") Integer size
    ){
        List<ApplyForm> applyFormList=deployService.getdeploylist(keyword,page,size);
        JSONArray revalue=new JSONArray();
        for (ApplyForm item : applyFormList){
            revalue.add(item);
        }
        return revalue;
    }

    /**
     * 获取开通申请数量
     */
    @RequestSufToken
    @RequestMapping("/getlistcount")
    public Integer getListCount(@PathParam("keyword") String keyword){
        return deployService.countdeploy(keyword);
    }

    /**
     * 递交开通申请
     */
    @RequestSufToken
    @RequestMapping("/apply")
    public JSONObject apply(@RequestBody JSONObject applyInfo) {
        String domainId=applyInfo.getString("domainId");
        String memo=applyInfo.getString("memo");
        String domainUserName=applyInfo.getString("domainUserName");
        String domainUserMobile=applyInfo.getString("domainUserMobile");
        String domainUserEmail=applyInfo.getString("domainUserEmail");
        ApplyForm applyForm=deployService.apply(domainId,memo,domainUserName,domainUserMobile,domainUserEmail);
        JSONObject json= JSON.parseObject(JSONObject.toJSONString(applyForm));
        return json;
    }
    /**
     *
     * 审核开通申请
     */
    @RequestSufToken
    @RequestMapping("/auditapply")
    public ApplyForm auditApply(@RequestBody JSONObject jsonObject){
            String applyId=jsonObject.getString("applyId");
            Boolean isPass=jsonObject.getBoolean("isPass");
            String auditMemo=jsonObject.getString("auditMemo");
        return deployService.auditapply(applyId,isPass,auditMemo);
    }

}
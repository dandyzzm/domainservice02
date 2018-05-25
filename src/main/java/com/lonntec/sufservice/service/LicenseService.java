package com.lonntec.sufservice.service;

import com.lonntec.sufservice.entity.License;

import java.util.Date;
import java.util.List;

public interface LicenseService {
    //获取授权申请列表
    List<License> getlist(String keyword, Integer page, Integer size);

    //获取授权申请数量
    Integer getlistcount(String keyword);

    //递交授权申请
    License apply(String domainId, String memo, Integer userCount, Date expireDate);

    //审核授权申请
    License auditapply(String applyId, Boolean isPass, String auditMemo);
}

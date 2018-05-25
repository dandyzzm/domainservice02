package com.lonntec.sufservice.service;

import com.lonntec.sufservice.entity.ApplyForm;

import java.util.List;

public interface DeployService {
    List<ApplyForm> getdeploylist(String keyword, Integer page, Integer size);

    Integer countdeploy(String keyword);

    ApplyForm apply(String domainId, String memo, String domainUserName, String domainUserMobile, String domainUserEmainl);

    ApplyForm auditapply(String applyId, Boolean isPass, String auditMemo);
}

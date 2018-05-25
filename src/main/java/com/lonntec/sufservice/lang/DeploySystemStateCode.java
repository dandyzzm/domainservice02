package com.lonntec.sufservice.lang;


import com.lonntec.framework.lang.StateCode;

public enum DeploySystemStateCode implements StateCode {
    Login_Overdue               (200061,"登录失效，请重新登录"),
    DomainId_IsEmpty            (200062,"企业域内码不能为空"),
    DomainUserName_IsEmpty      (200063,"请输入企业管理员姓名"),
    DomainUserMobile_IsEmpty    (200064,"请输入企业管理员手机号"),
    DomainUserEmail_IsEmpty     (200065,"请输入企业管理员邮箱"),
    Domain_IsNotExist           (200066,"企业域不存在"),
    ApplyForm_IsExist           (200067,"表单不存在"),
    UserCount_IsNotFigure       (200068,"请输入整数数"),
    ExpireDate_IsEmpty          (200069,"授权到期日不能为空"),
    User_IsNotAdmin             (200080,"用户没有权限"),
    License_IsNotExist          (200081,"表单不存在"),
    Suf_IsDeploy                (200082,"suf功能已开通"),
    Domain_IsNotEnable          (200083,"企业域已禁用")
    ;
    private Integer code;
    private String message;

    DeploySystemStateCode(Integer code, String message){

        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getCodeName() {
        return this.name();
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}

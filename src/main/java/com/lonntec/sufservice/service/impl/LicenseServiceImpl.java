package com.lonntec.sufservice.service.impl;

import com.lonntec.framework.lang.MicroServiceException;
import com.lonntec.framework.lang.UserContext;
import com.lonntec.framework.service.TokenService;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.License;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.proxy.CodeRuleService;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.LicenseRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class LicenseServiceImpl implements LicenseService{
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    CodeRuleService codeRuleService;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    /**
     *
     *获取授权申请列表
     */
    @Override
    public List<License> getlist(String keyword, Integer page, Integer size) {
        //用户是否存在 若果是admin可以查所有，如果是实施人员没有权限
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null|| size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可查所有，如果是实施人员只能查自己创建的
        if(!userOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_Overdue);
        }
            if(userOptional.get().getAdmin()==true){
                return  licenseRepository.findAllByMyQueryIsAdmin(queryKeywork,pageable);
            }else{
                return licenseRepository.findAllByMyQuery(queryKeywork,tokenuserId,pageable);
            }
    }
    /**
     *
     * 获取授权申请数量
     */
    @Override
    public Integer getlistcount(String keyword) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!userOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_Overdue);
        }
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可以查所有，如果是实施人员只能查自己创建的
        if(userOptional.get().getAdmin()==true){
            return  licenseRepository.countByMyQueryIsAdmin(queryKeywork);
        }else{
            return licenseRepository.countByMyQuery(queryKeywork,tokenuserId);
        }
    }
    /**
     *
     * 递交授权申请
     */
    @Override
    public License apply(String domainId, String memo, Integer userCount, Date expireDate) {
        //判断是否传入空值 domainId userCount(是否为数字) expireDate
        if(userCount==null){
            userCount=0;
        }
        String count=userCount.toString();
        if(domainId==null||domainId.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainId_IsEmpty);
        }else if (userCount==null||!Pattern.compile("[0-9]*").matcher(count).matches()){
            throw new DeploySystemException(DeploySystemStateCode.UserCount_IsNotFigure);
        }else if (expireDate==null){
            throw new DeploySystemException(DeploySystemStateCode.ExpireDate_IsEmpty);
        }
        //获取企业域信息
        Optional<Domain> domainOptional=domainRepository.findById(domainId);
        //判断企业域是否存在 是否禁用
        if(!domainOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotExist);
        }else if (domainOptional.get().getIsEnable()==false){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotEnable);
        }
        //获取owneruser
        Optional<User> userOptional=userRepository.findById(domainOptional.get().getUser().getRowId());
        License license=new License();
        Calendar calendar=Calendar.getInstance();
        license.setBillNumber(codeRuleService.generateCode("applySufLicenseRule"));
        license.setDomain(domainOptional.get());
        license.getDomain().setUser(userOptional.get());
        license.setApplyUserCount(userCount);
        license.setApplyExpireDate(expireDate);
        license.setMemo(memo);
        license.setCreateTime(calendar.getTime());
        license.setBillState(1);
        return licenseRepository.save(license);
    }
    /**
     *
     * 审核授权申请
     */
    @Override
    public License auditapply(String applyId, Boolean isPass, String auditMemo) {

        //判断用户是否存在 是否admin
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> userOptional=userRepository.findById(tokenuserId);
        if(!userOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_Overdue);
        }else if(userOptional.get().getAdmin()==false){
            throw new DeploySystemException(DeploySystemStateCode.User_IsNotAdmin);
        }
        //获取申请单
        Optional<License> licenseOptional= licenseRepository.findById(applyId);
        //判断表单是否存在，审核表单
        if(!licenseOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.License_IsNotExist);
        }

        License licenseForm=licenseOptional.get();
        if(isPass==true){
            licenseForm.setBillState(2);
            //修改企业域是否开通suf
            Domain domain=licenseForm.getDomain();
            domain.setExpireDate(licenseForm.getApplyExpireDate());
            domain.setUsercount(licenseForm.getApplyUserCount());
            domainRepository.save(domain);
        }else if (isPass==false||isPass==null){
            licenseForm.setBillState(3);
        }

        //审批备注
        licenseForm.setAuditMemo(auditMemo);
        //修改审核人信息
        licenseForm.setAuditer(userOptional.get());
        //修改审批时间
        Calendar calendar=Calendar.getInstance();
        licenseForm.setAuditTime(calendar.getTime());

        return licenseRepository.save(licenseForm);

    }
}

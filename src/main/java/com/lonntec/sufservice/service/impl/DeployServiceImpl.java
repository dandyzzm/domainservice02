package com.lonntec.sufservice.service.impl;

import com.lonntec.framework.lang.MicroServiceException;
import com.lonntec.framework.lang.UserContext;
import com.lonntec.framework.service.TokenService;
import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.DomainUser;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.proxy.CodeRuleService;
import com.lonntec.sufservice.repository.DeployRepository;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.DomainUserRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.DeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackOn = {MicroServiceException.class, RuntimeException.class})
@Service
public class DeployServiceImpl implements DeployService {
    @Autowired
    DeployRepository deployRepository;
    @Autowired
    CodeRuleService codeRuleService;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DomainUserRepository domainUserRepository;
    @Autowired
    TokenService tokenService;
    /**
     *
     *获取开通申请列表
     */
    @Override
    public List<ApplyForm> getdeploylist(String keyword, Integer page, Integer size) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> user=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!user.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_Overdue);
        }
        Integer queryPage = page==null || page <= 0 ? 1 : page;
        Integer querySize = size==null|| size <= 0 ? 25 : size;
        PageRequest pageable = new PageRequest(queryPage -1, querySize);
        String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
        if(!queryKeywork.contains("%")){
            queryKeywork = "%" + queryKeywork + "%";
        }
        //若果是admin可以查所有，如果是实施人员只能查自己创建的
            if(user.get().getAdmin()==true){
                return  deployRepository.findAllByMyQueryIsAdmin(queryKeywork,pageable);
            }else{
                return deployRepository.findAllByMyQuery(queryKeywork,tokenuserId,pageable);
            }
    }
    /**
     *
     *获取开通申请数量
     */
    @Override
    public Integer countdeploy(String keyword) {
        UserContext currCtx=UserContext.getCurrentUserContext();
        String tokenuserId=currCtx.properties.getString("rowid");
        Optional<User> user=userRepository.findById(tokenuserId);
        //判断用户是否存在
        if(!user.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Login_Overdue);
        }
            String queryKeywork = keyword== null || keyword.replaceAll("\\s*","").equals("") ? "%" : keyword;
            if(!queryKeywork.contains("%")){
                queryKeywork = "%" + queryKeywork + "%";
            }
            //若果是admin可以查所有，如果是实施人员只能查自己创建的
            if(user.get().getAdmin()==true){
                return  deployRepository.countByMyQueryIsAdmin(queryKeywork);
            }else{
                return deployRepository.countByMyQuery(queryKeywork,tokenuserId);
            }
    }
    /**
     *
     *递交开通申请
     */
    @Override
    public ApplyForm apply(String domainId, String memo, String domainUserName, String domainUserMobile, String domainUserEmainl) {
        //判断是否传入空值 domainId domianUserName domainUserMobile domainUserEmail
        if(domainId==null||domainId.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainId_IsEmpty);
        }else if (domainUserName==null||domainUserName.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserName_IsEmpty);
        }else if (domainUserMobile==null||domainUserMobile.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserMobile_IsEmpty);
        }else if (domainUserEmainl==null||domainUserEmainl.replaceAll("\\s*","").equals("")){
            throw new DeploySystemException(DeploySystemStateCode.DomainUserEmail_IsEmpty);
        }
        //获取企业域信息
        Optional<Domain> domain= domainRepository.findById(domainId);
        //判断企业域是否存在 是否已开通suf 是否禁用
        if(!domain.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotExist);
        }else if(domain.get().getIsActiveSuf()==true){
            throw new DeploySystemException(DeploySystemStateCode.Suf_IsDeploy);
        }else if (domain.get().getIsEnable()==false){
            throw new DeploySystemException(DeploySystemStateCode.Domain_IsNotEnable);
        }
        ApplyForm applyForm=new ApplyForm();
        applyForm.setBillNumber(codeRuleService.generateCode("activeSufFormRule"));
        applyForm.setDomain(domain.get());
        applyForm.setDomainUserName(domainUserName);
        applyForm.setDomainUserMobile(domainUserMobile);
        applyForm.setDomainUserEmain(domainUserEmainl);
        applyForm.setMemo(memo);
        applyForm.setBillState(1);
        deployRepository.save(applyForm);
        return applyForm;
    }
    /**
     *
     *审核开通申请
     */
    @Override
    public ApplyForm auditapply(String applyid, Boolean isPass, String auditMemo) {
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
        Optional<ApplyForm> applyFormOptional= deployRepository.findById(applyid);
        if(!applyFormOptional.isPresent()){
            throw new DeploySystemException(DeploySystemStateCode.ApplyForm_IsExist);
        }else{
            ApplyForm applyForm=applyFormOptional.get();
            if(isPass==true){
                applyForm.setBillState(2);
                //验证domainUser表数据，若数据为空则添加数据
                DomainUser domainUser= domainUserRepository.findByMobile(applyForm.getDomainUserMobile());
                if(domainUser==null){
                    domainUser = new DomainUser();
                    domainUser.setRowId(UUID.randomUUID().toString());
                    domainUser.setEmail(applyForm.getDomainUserEmain());
                    domainUser.setMobile(applyForm.getDomainUserMobile());
                    domainUser.setUserName(applyForm.getDomainUserName());
                    domainUserRepository.save(domainUser);
                }
                //修改企业域是否开通suf
                Domain domain=applyFormOptional.get().getDomain();
                domain.setIsActiveSuf(true);
                Calendar calendar=Calendar.getInstance();
                calendar.add(Calendar.MONTH,2);
                domain.setExpireDate(calendar.getTime());
                domain.setUsercount(10);
                domain.getDomainUsers().add(domainUser);
                domainRepository.save(domain);
            }else if (isPass==false||isPass==null){
                applyForm.setBillState(3);
            }
            applyForm.setAuditMemo(auditMemo);
            //修改审批时间
            Calendar calendar=Calendar.getInstance();
            applyForm.setAuditTime(calendar.getTime());
            //修改审核人信息
            applyForm.setAuditer(userOptional.get());
            return deployRepository.save(applyForm);
        }
    }
}

package com.lonntec.sufservice;

import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.lang.UserContext;
import com.lonntec.framework.service.TokenService;
import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.DomainUser;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.repository.DeployRepository;
import com.lonntec.sufservice.repository.DomainRepository;
import com.lonntec.sufservice.repository.DomainUserRepository;
import com.lonntec.sufservice.repository.UserRepository;
import com.lonntec.sufservice.service.DeployService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeployServiceUnitTest {

    @Autowired
    DeployService deployService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeployRepository deployRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    DomainUserRepository domainUserRepository;

    @Autowired
    TokenService tokenService;
    @Before
    public void beforeTest(){
        //添加用户 管理员
        User userisadmin=new User();
        userisadmin.setRowId("201801");
        userisadmin.setUsername("userfordeploy01");
        userisadmin.setNickname("fdeploy01");
        userisadmin.setEnable(true);
        userisadmin.setAdmin(true);
        Optional<User> userOptional1=userRepository.findById("201801");
        if(!userOptional1.isPresent()){
            userRepository.save(userisadmin);
        }
        //添加用户 实施
        User usernotadmin=new User();
        usernotadmin.setRowId("201802");
        usernotadmin.setUsername("userfordeploy02");
        usernotadmin.setNickname("fdeploy02");
        usernotadmin.setEnable(true);
        usernotadmin.setAdmin(false);
        Optional<User> userOptional2=userRepository.findById("201802");
        if(!userOptional2.isPresent()){
            userRepository.save(usernotadmin);
        }
        //添加企业域 管理员
        Domain domain=new Domain();
        domain.setRowId("20180101");
        domain.setDomainName("苹果公司");
        domain.setDomainShortName("Apple");
        domain.setDomainnumber("661265456");
        domain.setAddress("美国");
        domain.setIsEnable(true);
        domain.setLinkMan("乔布斯");
        domain.setLinkManMobile("9110101");
        domain.setMemo("Welcome !");
        domain.setUser(userisadmin);
        domainRepository.save(domain);
        //添加企业域 实施
        Domain domain1=new Domain();
        domain1.setRowId("20180202");
        domain1.setDomainName("微软公司");
        domain1.setDomainShortName("微软");
        domain1.setDomainnumber("6612654456");
        domain1.setAddress("美国");
        domain1.setIsEnable(true);
        domain1.setLinkMan("比尔盖茨");
        domain1.setLinkManMobile("9110202");
        domain1.setMemo("Welcome !");
        domain1.setUser(usernotadmin);
        domainRepository.save(domain1);
        //添加企业管理员
        DomainUser domainUser=new DomainUser();
        domainUser.setRowId("20180518003");
        domainUser.setUserName("奥巴马");
        domainUser.setMobile("9110321201");
        domainUser.setEmail("aobama@us.com");
        domainUserRepository.save(domainUser);
        //添加申请表单 管理员
        for(int i=0;i<99;i++){
            ApplyForm applyForm=new ApplyForm();
            applyForm.setRowId("iphone"+i);
            applyForm.setBillNumber("apple000"+i);
            applyForm.setDomain(domain);
            applyForm.getDomain().setUser(userisadmin);
            applyForm.setDomainUserName(domainUser.getUserName());
            applyForm.setDomainUserMobile(domainUser.getMobile());
            applyForm.setDomainUserEmain(domainUser.getEmail());
            applyForm.setMemo("ipad");
            Calendar calendar=Calendar.getInstance();
            applyForm.setCreateTime(calendar.getTime());
            applyForm.setBillState(1);
            deployRepository.save(applyForm);
        }
        //添加申请表单 实施
        for(int i=0;i<99;i++){
            ApplyForm applyForm=new ApplyForm();
            applyForm.setRowId("soft"+i);
            applyForm.setBillNumber("soft000"+i);
            applyForm.setDomain(domain1);
            applyForm.getDomain().setUser(usernotadmin);
            applyForm.setDomainUserName(domainUser.getUserName());
            applyForm.setDomainUserMobile(domainUser.getMobile());
            applyForm.setDomainUserEmain(domainUser.getEmail());
            applyForm.setMemo("soft");
            Calendar calendar=Calendar.getInstance();
            applyForm.setCreateTime(calendar.getTime());
            applyForm.setBillState(1);
            deployRepository.save(applyForm);
        }
    }
    @After
    public void testAfter(){
        deployRepository.deleteAll();
        domainUserRepository.deleteAll();
        domainRepository.deleteAll();
        userRepository.deleteAll();
    }
    /**
     *
     *获取开通申请列表
     * 管理员版
     */
    @Test          //输入为空 页数为1
    public void test_getdeploylist_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("", 1, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test         //输入为空 页数为2
    public void test_getdeploylist_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("", 2, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test         //输入为空 页数为0
    public void test_getdeploylist_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("", 0, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test        //输入为 页数为8
    public void test_getdeploylist_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);;
       List<ApplyForm> applyForm=deployService.getdeploylist("苹果",4, 25);
        Assert.assertEquals(applyForm.size(), 24);
    }

    @Test        //输入为%苹果% 页数为4
    public void test_getdeploylist_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("%苹果%", 4, 25);
        Assert.assertEquals(applyForm.size(), 24);
    }

    @Test        //关键字不存在
    public void test_getdeploylist_case6(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("l  o", 1, 25);
        Assert.assertEquals(applyForm.size(), 0);
    }

    @Test        //输入空格 页数为1 显示条数为-1
    public void test_getdeploylist_case7(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
       List<ApplyForm> applyForm=deployService.getdeploylist("", 1, -1);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test          //用户不存在
    public void test_getdeploylist_case8(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201803");
        context.put("username", "userfordeploy03");
        UserContext.createUserContext(context);
        try {
            List<ApplyForm> applyForm=deployService.getdeploylist("", 1, 25);
            Assert.assertEquals(true, false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Login_Overdue.getCode());
        }
    }
    /**
     *
     *获取开通申请列表
     * 实施版
     */
    @Test          //输入为空 页数为1
    public void test_getdeploylist_case11(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        List<ApplyForm> applyForm=deployService.getdeploylist("", 1, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test         //输入为空 页数为2
    public void test_getdeploylist_case12(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        List<ApplyForm> applyForm=deployService.getdeploylist("", 2, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test         //输入为空 页数为0
    public void test_getdeploylist_case13(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        List<ApplyForm> applyForm=deployService.getdeploylist("", 0, 25);
        Assert.assertEquals(applyForm.size(), 25);
    }

    @Test        //关键字不存在 页数为1
    public void test_getdeploylist_case14(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        List<ApplyForm> applyForm=deployService.getdeploylist("苹果", 1, 25);
        Assert.assertEquals(applyForm.size(), 0);
    }

    @Test        //输入为%
    public void test_getdeploylist_case15(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        long count1=deployService.countdeploy("%微软%").longValue();
        List<ApplyForm> applyForm=deployService.getdeploylist("%", 1, 200);
        Assert.assertEquals(applyForm.size(), count1);
    }

    @Test        //输入空格 页数为1 显示条数为-1
    public void test_getdeploylist_case16(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        List<ApplyForm> applyForm=deployService.getdeploylist("", 1, -1);
        Assert.assertEquals(applyForm.size(), 25);
    }

    /**
     *
     *获取开通申请数量
     * 管理员版
     */
    @Test       //总数
    public void test_countdeploy_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
        List<ApplyForm> list=deployService.getdeploylist("",1,2000);
        long count1=deployService.countdeploy("").longValue();
        Assert.assertEquals(count1,list.size());
    }
    @Test       //用户不存在
    public void test_countdeploy_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201803");
        context.put("username", "userfordeploy03");
        UserContext.createUserContext(context);
        try {
            deployService.countdeploy("");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Login_Overdue.getCode());
        }
    }
    /**
     *
     *获取开通申请数量
     * 实施版
     */
    @Test       //总数
    public void test_countdeploy_case11(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        long count1=deployService.countdeploy("").longValue();
        List<ApplyForm> list=deployService.getdeploylist("",1,2000);
        Assert.assertEquals(count1,list.size());
    }
    /**
     *
     * 递交开通申请
     */
    @Test       //判断domainId是否为空
    public void test_apply_case1(){
        try {
            deployService.apply("","nihao","domainUser001","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainId_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserName是否为空
    public void test_apply_case2(){
        try {
            deployService.apply("20180202","nihao","","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserName_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserMobile是否为空
    public void test_apply_case3(){
        try {
            deployService.apply("20180202","nihao","domainUser001","","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserMobile_IsEmpty.getCode());
        }
    }
    @Test       //判断domainUserEmail是否为空
    public void test_apply_case4(){
        try {
            deployService.apply("20180202","nihao","domainUser120","domainUser120","");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainUserEmail_IsEmpty.getCode());
        }
    }
    @Test       //判断企业域是否存在
    public void test_apply_case5(){
        try {
            deployService.apply("20180203","nihao","domainUser120","domainUser120","domain@123");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Domain_IsNotExist.getCode());
        }
    }
    @Test       //已开通suf
    public void  test_apply_case6(){
        Optional<Domain> domainOptional= domainRepository.findById("20180202");
        if(domainOptional.isPresent()){
            Domain domain=domainOptional.get();
            domain.setIsActiveSuf(true);
            domainRepository.save(domain);
        }
        try {
            deployService.apply("20180202","hello","aaa","13955000002","5414");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Suf_IsDeploy.getCode());
        }
    }
    @Test       //企业域已禁用
    public void test_apply_case7(){
        Optional<Domain> domainOptional= domainRepository.findById("20180202");
        if(domainOptional.isPresent()){
            Domain domain=domainOptional.get();
            domain.setIsEnable(false);
            domainRepository.save(domain);
        }
        try {
            deployService.apply("20180202","hello","aaa","13955000002","5414");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Domain_IsNotEnable.getCode());
        }
    }
    @Test
    public void test_apply_case8(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        ApplyForm applyForm=deployService.apply("20180202","nihao","奥巴马","9110321201","aobama@us.com");
        Assert.assertEquals(applyForm.getBillState().longValue(),1);
    }
    /**
     *
     * 审核开通申请
     */
    @Test       //表单不存在
    public void test_auditApply_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
        try {
            deployService.auditapply("1234",true,"nice");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.ApplyForm_IsExist.getCode());
        }
    }
    @Test       //审核开通申请
    public void test_auditApply_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
        deployService.auditapply("iphone1",true,"nice");
        Optional<ApplyForm> applyFormOptional=deployRepository.findById("iphone1");
        if(applyFormOptional.isPresent()){
            Assert.assertEquals(applyFormOptional.get().getBillState().longValue(),2);
        }
    }
    @Test       //拒绝开通
    public void test_auditApply_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201801");
        context.put("username", "userfordeploy01");
        UserContext.createUserContext(context);
        deployService.auditapply("iphone1",false,"nice");
        Optional<ApplyForm> applyFormOptional=deployRepository.findById("iphone1");
        if(applyFormOptional.isPresent()){
            Assert.assertEquals(applyFormOptional.get().getBillState().longValue(),3);
        }
    }
    @Test       //用户不存在
    public void test_auditApply_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201803");
        context.put("username", "userfordeploy03");
        UserContext.createUserContext(context);
        try {
            deployService.auditapply("iphone1",true,"nice");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Login_Overdue.getCode());
        }
    }
    @Test       //用户不是管理员
    public void test_auditApply_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "201802");
        context.put("username", "userfordeploy02");
        UserContext.createUserContext(context);
        try {
            deployService.auditapply("iphone1",true,"nice");
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.User_IsNotAdmin.getCode());
        }

    }
}

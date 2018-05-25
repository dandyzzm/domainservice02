package com.lonntec.sufservice;

import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.lang.UserContext;
import com.lonntec.framework.service.TokenService;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.License;
import com.lonntec.sufservice.entity.User;
import com.lonntec.sufservice.lang.DeploySystemException;
import com.lonntec.sufservice.lang.DeploySystemStateCode;
import com.lonntec.sufservice.repository.*;
import com.lonntec.sufservice.service.DeployService;
import com.lonntec.sufservice.service.LicenseService;
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
public class LicenseServiceUnitTest {

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
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    LicenseService licenseService;
    @Before
    public void testBefore(){
        //添加用户
        User user=new User();
        user.setRowId("abc");
        user.setUsername("hello");
        user.setNickname("hi");
        user.setEnable(true);
        user.setAdmin(true);
        Optional<User> userOptional=userRepository.findById("abc");
        if(!userOptional.isPresent()){
            userRepository.save(user);
        }
        //添加企业域
        Domain domain=new Domain();
        domain.setRowId("123");
        domain.setDomainName("lonntec");
        domain.setDomainnumber("456");
        domain.setDomainShortName("lt");
        domain.setIsEnable(true);
        domain.setIsActiveSuf(true);
        domain.setUsercount(10);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,2);
        domain.setExpireDate(calendar.getTime());
        domain.setUser(user);
        domainRepository.save(domain);
        //添加申请表
        License license=new License();
        license.setRowId("111");
        license.setBillNumber("101");
        license.setDomain(domain);
        license.getDomain().setUser(user);
        license.setApplyUserCount(100);
        Calendar calendar1=Calendar.getInstance();
        calendar1.add(Calendar.YEAR,2);
        license.setApplyExpireDate(calendar1.getTime());
        Calendar calendar2=Calendar.getInstance();
        license.setCreateTime(calendar2.getTime());
        license.setBillState(1);
        licenseRepository.save(license);
    }
    @After
    public void testAfter(){
        licenseRepository.deleteAll();
        deployRepository.deleteAll();
        domainUserRepository.deleteAll();
        domainRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test   //添加申请表
    public void test_licenseApply_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        Calendar calendar3=Calendar.getInstance();
        calendar3.add(Calendar.YEAR,2);
        licenseService.apply("123","aa",100,calendar3.getTime());
    }
    @Test       //企业域是否禁用
    public void test_licenseApply_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        Optional<Domain> domainOptional=domainRepository.findById("123");
        if(domainOptional.isPresent()){
            Domain domain=domainOptional.get();
            domain.setIsEnable(false);
            domainRepository.save(domain);
        }
        Calendar calendar3=Calendar.getInstance();
        calendar3.add(Calendar.YEAR,2);
        try {
            licenseService.apply("123","aa",100,calendar3.getTime());
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Domain_IsNotEnable.getCode());
        }
    }
    @Test       //参数有空值
    public void test_licenseApply_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        Calendar calendar3=Calendar.getInstance();
        calendar3.add(Calendar.YEAR,2);
        try {
            licenseService.apply("","aa",100,calendar3.getTime());
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.DomainId_IsEmpty.getCode());
        }
    }
    @Test       //参数有空值
    public void test_licenseApply_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        try {
            licenseService.apply("123","aa",null,null);
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.ExpireDate_IsEmpty.getCode());
        }
    }
    @Test       //企业域不存在
    public void test_licenseApply_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        Calendar calendar3=Calendar.getInstance();
        calendar3.add(Calendar.YEAR,2);
        try {
            licenseService.apply("1234","aa",null,calendar3.getTime());
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Domain_IsNotExist.getCode());
        }
    }
    @Test       //正向审核
    public void test_auditapply_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        licenseService.auditapply("111",true,"000");
        Optional<License> licenseOptional=licenseRepository.findById("111");
        if(licenseOptional.isPresent()){
            Assert.assertEquals(licenseOptional.get().getBillState().longValue(),2);
        }
    }

    @Test       //反向审核
    public void test_auditapply_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        licenseService.auditapply("111",false,"000");
        Optional<License> licenseOptional=licenseRepository.findById("111");
        if(licenseOptional.isPresent()){
            Assert.assertEquals(licenseOptional.get().getBillState().longValue(),3);
        }
    }
    @Test   //管理员查询列表
    public void test_getlist_case1(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        List<License> list= licenseService.getlist("",1,25);
        long count=licenseService.getlistcount("").longValue();
        Assert.assertEquals(list.size(),count);
    }
    @Test   //关键字不存在
    public void test_getlist_case2(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        List<License> list= licenseService.getlist("ss",1,25);
        long count=licenseService.getlistcount("ss").longValue();
        Assert.assertEquals(list.size(),count);
    }
    @Test   //实施用户查询列表
    public void test_getlist_case3(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abc");
        context.put("username", "hello");
        UserContext.createUserContext(context);
        Optional<User> userOptional=userRepository.findById("abc");
        if(userOptional.isPresent()){
            User user=userOptional.get();
            user.setAdmin(false);
            userRepository.save(user);
        }
        List<License> list= licenseService.getlist("",1,25);
        long count=licenseService.getlistcount("").longValue();
        Assert.assertEquals(list.size(),count);
    }
    @Test   //用户没登录 获取列表
    public void test_getlist_case4(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abcd");
        context.put("username", "hellod");
        UserContext.createUserContext(context);
        try {
            List<License> list= licenseService.getlist("",1,25);
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Login_Overdue.getCode());
        }
    }
    @Test   //用户没登录 获取数量
    public void test_getlist_case5(){
        JSONObject context = new JSONObject();
        context.put("rowid", "abcd");
        context.put("username", "hellod");
        UserContext.createUserContext(context);
        try {
            long count=licenseService.getlistcount("").longValue();
            Assert.assertEquals(true,false);
        }catch (DeploySystemException ex){
            Assert.assertEquals(ex.getStateCode().getCode(), DeploySystemStateCode.Login_Overdue.getCode());
        }
    }
}

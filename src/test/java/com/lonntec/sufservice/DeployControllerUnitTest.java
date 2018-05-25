package com.lonntec.sufservice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.lang.Result;
import com.lonntec.framework.lang.SystemStateCode;
import com.lonntec.framework.service.TokenService;
import com.lonntec.sufservice.entity.ApplyForm;
import com.lonntec.sufservice.entity.Domain;
import com.lonntec.sufservice.entity.DomainUser;
import com.lonntec.sufservice.entity.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeployControllerUnitTest {

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

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

        Optional<User> init = userRepository.findById("admin001");
        if(init.isPresent()){
            return;
        }

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        //添加用户 管理员
        User admin=new User();
        admin.setRowId("admin001");
        admin.setUsername("admin001");
        admin.setNickname("admin001");
        admin.setEnable(true);
        admin.setAdmin(true);
        Optional<User> userOptional1=userRepository.findById("admin001");
        if(!userOptional1.isPresent()){
            userRepository.save(admin);
        }
        //添加用户 实施
        User user=new User();
        user.setRowId("user001");
        user.setUsername("user001");
        user.setNickname("user001");
        user.setEnable(true);
        user.setAdmin(false);
        Optional<User> userOptional2=userRepository.findById("user001");
        if(!userOptional2.isPresent()){
            userRepository.save(user);
        }
        //添加企业域 管理员
        Domain domain=new Domain();
        domain.setRowId("dom001");
        domain.setDomainName("dom001");
        domain.setDomainShortName("dom001");
        domain.setDomainnumber("dom001");
        domain.setAddress("dom001");
        domain.setIsEnable(true);
        domain.setLinkMan("131001");
        domain.setLinkManMobile("131001");
        domain.setUser(admin);
        domainRepository.save(domain);
        //添加企业域 实施
        Domain domain1=new Domain();
        domain1.setRowId("dom002");
        domain1.setDomainName("dom002");
        domain1.setDomainShortName("dom002");
        domain1.setDomainnumber("dom002");
        domain1.setAddress("dom002");
        domain1.setIsEnable(true);
        domain1.setLinkMan("131002");
        domain1.setLinkManMobile("131002");
        domain1.setUser(user);
        domainRepository.save(domain1);
        //添加企业管理员
        DomainUser domainUser=new DomainUser();
        domainUser.setRowId("domainuser001");
        domainUser.setUserName("domainuser001");
        domainUser.setMobile("domainuser001");
        domainUser.setEmail("domainuser@001.com");
        domainUserRepository.save(domainUser);
        //添加申请表单 管理员
        for(int i=0;i<50;i++){
            ApplyForm applyForm=new ApplyForm();
            applyForm.setRowId("vinda"+i);
            applyForm.setBillNumber("vinda"+i);
            applyForm.setDomain(domain);
            applyForm.getDomain().setUser(admin);
            applyForm.setDomainUserName(domainUser.getUserName());
            applyForm.setDomainUserMobile(domainUser.getMobile());
            applyForm.setDomainUserEmain(domainUser.getEmail());
            Calendar calendar=Calendar.getInstance();
            applyForm.setCreateTime(calendar.getTime());
            applyForm.setBillState(1);
            deployRepository.save(applyForm);
        }
        //添加申请表单 实施
        for(int i=0;i<50;i++){
            ApplyForm applyForm=new ApplyForm();
            applyForm.setRowId("coffee"+i);
            applyForm.setBillNumber("coffee"+i);
            applyForm.setDomain(domain1);
            applyForm.getDomain().setUser(user);
            applyForm.setDomainUserName(domainUser.getUserName());
            applyForm.setDomainUserMobile(domainUser.getMobile());
            applyForm.setDomainUserEmain(domainUser.getEmail());
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
     * 获取开通申请列表
     * 和开通申请数量
     */
    @Test  //管理员获取总数量
    public void test_getdeploylist_case1() throws Exception{
        String token = adminlogin();

        String resultCount = mockMvc.perform(get("/deploy/getlistcount?keyword=")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(),SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/deploy/getlist?keyword=&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    @Test  //实施人员获取总数量
    public void test_getdeploylist_case2() throws Exception{
        String token = userlogin();

        String resultCount = mockMvc.perform(get("/deploy/getlistcount?keyword=")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(),SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/deploy/getlist?keyword=&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    @Test  //关键字为 "001"
    public void test_getdeploylist_case3() throws Exception{
        String token = adminlogin();

        String resultCount = mockMvc.perform(get("/deploy/getlistcount?keyword=001")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(),SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/deploy/getlist?keyword=001&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    /**
     * 递交开通申请
     *
     */
    @Test //递交申请成功
    public void test_apply_case1() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),SystemStateCode.OK.getCode());
        Assert.assertNotNull(result.getResult());
    }

    @Test //domainId为空
    public void test_apply_case2() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.DomainId_IsEmpty.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //domainUserName为空
    public void test_apply_case3() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.DomainUserName_IsEmpty.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //domainUserMobile为空
    public void test_apply_case4() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.DomainUserMobile_IsEmpty.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //domainUserEmail为空
    public void test_apply_case5() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.DomainUserEmail_IsEmpty.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //企业域不存在
    public void test_apply_case6() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("domainId","22222");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.Domain_IsNotExist.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //已开通Suf
    public void test_apply_case7() throws  Exception{
        String token = adminlogin();
        Optional<Domain> domainOptional= domainRepository.findById("dom001");
        if(domainOptional.isPresent()){
            Domain domain=domainOptional.get();
            domain.setIsActiveSuf(true);
            domainRepository.save(domain);
        }
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.Suf_IsDeploy.getCode());
        Assert.assertNull(result.getResult());
    }

    @Test //企业域已禁用
    public void test_apply_case8() throws  Exception{
        String token = adminlogin();
        Optional<Domain> domainOptional= domainRepository.findById("dom001");
        if(domainOptional.isPresent()){
            Domain domain=domainOptional.get();
            domain.setIsEnable(false);
            domainRepository.save(domain);
        }
        JSONObject apply = new JSONObject();
        apply.put("domainId","dom001");
        apply.put("memo","hello");
        apply.put("domainUserName","domainuser001");
        apply.put("domainUserMobile","domainuser001");
        apply.put("domainUserEmail","domainuser@001.com");
        String responseBody = mockMvc.perform(
                post("/deploy/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.Domain_IsNotEnable.getCode());
        Assert.assertNull(result.getResult());
    }

    /**
     * 审核开通申请
     *
     */
    @Test //开通成功
    public void test_auditApply_case1() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("applyId","vinda1");
        apply.put("isPass", true);
        apply.put("auditMemo","OK");
        String responseBody = mockMvc.perform(
                post("/deploy/auditapply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),SystemStateCode.OK.getCode());
        Assert.assertNotNull(result.getResult());
        Optional<ApplyForm> applyFormOptional=deployRepository.findById("vinda1");
        if(applyFormOptional.isPresent()){
            Assert.assertEquals(applyFormOptional.get().getBillState().longValue(),2);
        }
    }

    @Test //拒绝开通
    public void test_auditApply_case2() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("applyId","vinda1");
        apply.put("isPass", false);
        apply.put("auditMemo","NO");
        String responseBody = mockMvc.perform(
                post("/deploy/auditapply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),SystemStateCode.OK.getCode());
        Assert.assertNotNull(result.getResult());
        Optional<ApplyForm> applyFormOptional=deployRepository.findById("vinda1");
        if(applyFormOptional.isPresent()){
            Assert.assertEquals(applyFormOptional.get().getBillState().longValue(),3);
        }
    }

    @Test //用户不是管理员
    public void test_auditApply_case3() throws  Exception{
        String token = userlogin();
        JSONObject apply = new JSONObject();
        apply.put("applyId","vinda1");
        apply.put("isPass", true);
        apply.put("auditMemo","OK");
        String responseBody = mockMvc.perform(
                post("/deploy/auditapply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.User_IsNotAdmin.getCode());
        Assert.assertNull(result.getResult());

    }

    @Test //表单不存在
    public void test_auditApply_case4() throws  Exception{
        String token = adminlogin();
        JSONObject apply = new JSONObject();
        apply.put("applyId","123456");
        apply.put("isPass", true);
        apply.put("auditMemo","OK");
        String responseBody = mockMvc.perform(
                post("/deploy/auditapply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),DeploySystemStateCode.ApplyForm_IsExist.getCode());
        Assert.assertNull(result.getResult());

    }

    //管理员登录
    private  String adminlogin() throws  Exception {
        JSONObject contextBody = new JSONObject();
        contextBody.put("rowid","admin001");
        contextBody.put("username","admin001");
        String token = tokenService.grantToken(contextBody);
        return token;
    }

    //非管理员登录
    private  String userlogin() throws  Exception {
        JSONObject contextBody = new JSONObject();
        contextBody.put("rowid","user001");
        contextBody.put("username","user001");
        String token = tokenService.grantToken(contextBody);
        return token;
    }
}

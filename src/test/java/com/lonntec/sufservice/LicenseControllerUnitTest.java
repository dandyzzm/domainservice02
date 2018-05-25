package com.lonntec.sufservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lonntec.framework.lang.Result;
import com.lonntec.framework.lang.SystemStateCode;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LicenseControllerUnitTest {

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
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    LicenseService licenseService;
    @Before
    public void testBefore(){

        Optional<User> init = userRepository.findById("admin000");
        if(init.isPresent()){
            return;
        }

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        //添加用户
        User user=new User();
        user.setRowId("admin000");
        user.setUsername("admin000");
        user.setEnable(true);
        user.setAdmin(true);
        Optional<User> userOptional=userRepository.findById("admin000");
        if(!userOptional.isPresent()){
            userRepository.save(user);
        }
        //添加企业域
        Domain domain=new Domain();
        domain.setRowId("domain000");
        domain.setDomainName("domain000");
        domain.setDomainnumber("domain000");
        domain.setDomainShortName("domain000");
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
        license.setRowId("license000");
        license.setBillNumber("license000");
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

    /**
     * 获取开通申请列表
     * 和开通申请数量
     */
    @Test  //管理员获取总数量
    public void test_getdeploylist_case1() throws Exception{
        String token = adminlogin();

        String resultCount = mockMvc.perform(get("/license/getlistcount?keyword=")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(), SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/license/getlist?keyword=&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    @Test  //实施人员获取总数量
    public void test_getdeploylist_case2() throws Exception{
        String token = adminlogin();
        Optional<User> userOptional=userRepository.findById("admin000");
        if(userOptional.isPresent()){
            User user=userOptional.get();
            user.setAdmin(false);
            userRepository.save(user);
        }

        String resultCount = mockMvc.perform(get("/license/getlistcount?keyword=")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(), SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/license/getlist?keyword=&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    @Test  //关键字为000
    public void test_getdeploylist_case3() throws Exception{
        String token = adminlogin();

        String resultCount = mockMvc.perform(get("/license/getlistcount?keyword=000")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result1 = JSON.parseObject(resultCount,Result.class);
        Assert.assertEquals(result1.getStateCode(), SystemStateCode.OK.getCode());
        int count = (int)result1.getResult();

        String resultList = mockMvc.perform(get("/license/getlist?keyword=000&page=1&size=200")
                .header("Suf-Token",token))
                .andReturn().getResponse().getContentAsString();
        Result result2 = JSON.parseObject(resultList,Result.class);
        Assert.assertEquals(result2.getStateCode(),SystemStateCode.OK.getCode());
        JSONArray list = (JSONArray) result2.getResult();

        Assert.assertEquals(list.size(), count);

    }

    /**
     *添加申请表
     */
    @Test
    public void test_licenseApply_case1() throws Exception{
        String token = adminlogin();
        Calendar calendar3=Calendar.getInstance();
        calendar3.add(Calendar.YEAR,2);
        JSONObject apply = new JSONObject();
        apply.put("domainId","domain000");
        apply.put("memo","hello");
        apply.put("userCount",100);
        apply.put("expireDate",calendar3.getTime());
        String responseBody = mockMvc.perform(
                post("/license/apply")
                        .header("Suf-Token",token)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(apply.toJSONString()))
                .andReturn().getResponse().getContentAsString();
        Result result = JSON.parseObject(responseBody, Result.class);
        Assert.assertEquals(result.getStateCode(),SystemStateCode.OK.getCode());
        Assert.assertNotNull(result.getResult());
    }


    //管理员登录
    private  String adminlogin() throws  Exception {
        JSONObject contextBody = new JSONObject();
        contextBody.put("rowid","admin000");
        contextBody.put("username","admin000");
        String token = tokenService.grantToken(contextBody);
        return token;
    }

}

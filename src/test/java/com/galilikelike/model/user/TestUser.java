package com.galilikelike.model.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galilikelike.Utils.GeneratePresignedUrlDemo;
import com.galilikelike.Utils.ManageCosUtils;
import com.galilikelike.model.dto.PageDto;
import com.galilikelike.model.dto.UserDto;
import com.galilikelike.model.dto.UserLoginDto;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;
import com.galilikelike.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
public class TestUser {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private static Logger LOGGER = LoggerFactory.getLogger(TestUser.class);

    @Test
    public void testSave() {
        User user = new User();
        user.setUserAccount("acount");
        user.setUserPassword("a");
        user.setUserName("帮你");
        user.setEmail("11111@qq.com");
        user.setPhone("456");
        user.setUserRole(Short.valueOf("0"));
        user.setUserStatus(Short.valueOf("0"));
        user.setIsDelete(Short.valueOf("0"));
        user.setAvatarUrl("/a.png");
        userService.save(user);
        System.out.println(user.getId());
        assertNotEquals(1, user.getId());
    }

    @Test
    @DisplayName("测试查询全部用户")
    public void testGetAll() {
        List<User> users = userService.list();
        assertNotNull(users,"用户为空");
    }

    @Test
    @DisplayName("测试账号")
    public void testUsername() {
        boolean rs = Pattern.matches("\\w{6,}", "al;bce4F");
        assertEquals(false, rs,"匹配成功");
    }

    @Test
    @DisplayName("测试分页")
    public void testPage() {
        Page<UserVo> userVos = userService.pageUsers(new PageDto(1, 10));
        assertEquals(10, userVos.getSize(),"数据数量不够");
    }


    @Test
    @DisplayName("测试登录成功")
    public void testLoginSuccess() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserAccount("tony12346");
        userLoginDto.setUserPassword("123456");
        String jsonData = objectMapper.writeValueAsString(userLoginDto);
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonData, headers);
        UserVo userVo = restTemplate.postForObject("http://localhost:8080/user/login", httpEntity, UserVo.class);
        LOGGER.info("用户信息:{}",userVo);
        assertNotNull(userVo,"登录失败");
    }

    @Test
    @DisplayName("测试注册成功")
    public void testregeditSuccess() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = new UserDto();
        userDto.setUserAccount("tom");
        userDto.setUserPassword("123456;");
        userDto.setCode("123456");
        String jsonData = objectMapper.writeValueAsString(userDto);
        HttpEntity httpEntity = new HttpEntity(jsonData);
        Integer result = restTemplate.postForObject("http://localhost:8080/user/regedit", httpEntity, Integer.class);
        assertNotNull(result,"登录失败");
    }

    @Test
    @DisplayName("测试验证码结果")
    public void testVerCode() {

    }

    @Test
    @DisplayName("生成url")
    public void createUrl() {
        GeneratePresignedUrlDemo.generatePresignedDownloadUrlWithOverrideResponseHeader("headers/01694541-7533-4410-b16e-a494eeb1aa4bui_5c6be7ac8ecf9.jpg");
    }

    @Test
    @DisplayName("测试文件hash值")
    public void testHashCode() throws IOException {
        File file = new File("src/main/resources/1739269271000.jpg");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024*1024];
            int len = 0;
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            while((len = fis.read(buffer)) != -1) {
                instance.update(buffer,0,len);
            }
            byte[] digestBytes = instance.digest();
            StringBuffer hashValue = new StringBuffer();
            for (int i = 0;i<digestBytes.length;i++) {
                byte cur = digestBytes[i];
                hashValue.append(String.format("%02x",cur));
            }
            log.info("file:{},hash:{}",file.getName(),hashValue.toString());
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            fis.close();
        }


    }

}

package com.bochkov.jpa.test;

import com.bochkov.jpa.entity.User;
import com.bochkov.jpa.repository.UserFilter;
import com.bochkov.jpa.repository.UserRepository;
import com.bochkov.security.jwt.JwtRequest;
import com.bochkov.security.jwt.JwtResponse;
import com.bochkov.security.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final String password = "1234";
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(ApiTest.class);


    @Autowired
    public ApiTest(MockMvc mockMvc, ObjectMapper objectMapper, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Test
    void testValidateUniqueEmail() {
        Exception exception = null;
        try {
            User user = userRepository.findById(1l).orElse(null);
            user.setEmail("user-2@yandex.ru");
            userRepository.save(user);
        } catch (Exception e) {
            exception = e;
            logger.debug("error when save user", e);
        }
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateWrongEmail() {
        Exception exception = null;
        try {
            userRepository.save(User.create(26).setEmail("---"));
        } catch (Exception e) {
            exception = e;
            logger.debug("error when save user", e);
        }
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateWrongPhone() {
        Exception exception = null;
        try {
            userRepository.save(User.create().setPhone("0000000"));
        } catch (Exception e) {
            exception = e;
            logger.debug(e.getMessage());
        }
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateUniquePhoneFail() {
        Exception exception = null;
        try {
            User user = userRepository.findById(1l).orElse(null);
            userRepository.save(user.setPhone("0000000002"));
        } catch (Exception e) {
            exception = e;
            logger.debug(e.getMessage());
        }
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateUniquePhoneSuccess() {
        Exception exception = null;
        User user = userRepository.findByName(User.create().getName()).orElse(null);
        user.setPhone("0000000111");
        try {
            userRepository.save(user);
        } catch (Exception e) {
            exception = e;
            logger.debug(e.getMessage());
        }
        Assertions.assertNull(exception);
    }

    @Test
    void testFilterPage() {
        Page<User> page = userRepository.findAll(PageRequest.of(0, 15), new UserFilter().setName("user-1"));
        Assertions.assertTrue(page.getTotalElements() == 11);
        page = userRepository.findAll(PageRequest.of(0, 15), new UserFilter().setPhone("0000000005"));
        Assertions.assertTrue(page.getTotalElements() == 1);
    }

    @Test
    public void testAuthenticate() throws Exception {
        User userView = User.create();
        JwtRequest request = new JwtRequest(userView.getName(), password);
        MvcResult result = this.mockMvc
                .perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
//                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(), JwtResponse.class);
        String jwtToken = jwtResponse.getJwttoken();
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    public void testGetUsers() throws Exception {
        User user = User.create();
        MvcResult result = this.mockMvc.perform(get("/users?page=0&size-5")
                        .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAuthorizationHeader(user.getName())))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertNotNull(result);
    }

    @Test
    public void testChangeEmailSuccess() throws Exception {
        User user = userRepository.findById(1l).orElse(null);
        user.setEmail("me@yandex.ru");
        MvcResult result = this.mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAuthorizationHeader(user.getName())))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertNotNull(result);
    }

    @Test
    public void testChangePhoneSuccess() throws Exception {
        User user = userRepository.findById(1l).orElse(null);
        user.setPhone("9098301186", "0000000000");
        MvcResult result = this.mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAuthorizationHeader(user.getName())))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertNotNull(result);
    }

}

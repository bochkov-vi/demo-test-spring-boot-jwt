package com.bochkov.jpa.test;

import com.bochkov.jpa.entity.User;
import com.bochkov.jpa.repository.UserRepository;
import com.bochkov.security.jwt.JwtRequest;
import com.bochkov.security.jwt.JwtResponse;
import com.bochkov.security.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired
    public ApiTest(MockMvc mockMvc, ObjectMapper objectMapper, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
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

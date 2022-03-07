package com.bochkov.jpa.test;

import com.bochkov.jpa.entity.User;
import com.bochkov.jpa.repository.UserFilter;
import com.bochkov.jpa.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@SpringBootTest
class UserRepositoryTest {

    Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);
    @Autowired
    UserRepository userRepository;


    @Test
    void testValidateUniqueEmail() {
        Exception exception = null;
        try {
            User user = User.create(25);
            user.setEmail("user-1@yandex.ru");
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
            userRepository.save(User.create().setEmail("a@a.a"));
        } catch (Exception e) {
            exception = e;
            logger.debug(e.getMessage());
        }
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateUniquePhoneSuccess() {
        Exception exception = null;
        User user = userRepository.findById(1l).orElse(null);
        user.setPhone("9098301186");
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
        page = userRepository.findAll(PageRequest.of(0, 15), new UserFilter().setPhone("0000000001"));
        Assertions.assertTrue(page.getTotalElements() == 1);
    }

    @Test
    void testUseCache() {
        userRepository.findById(1l);
        userRepository.findById(1l);
        userRepository.findById(1l);
        userRepository.findById(1l);
        userRepository.findById(1l);
    }

}

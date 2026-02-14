package com.sky;

import com.sky.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SkyApplication.class)
class UserDTOTest {

    @Autowired
    private Validator validator;

    @Test
    void should_fail_when_username_blank() {
        UserDTO dto = new UserDTO();
        dto.setUsername("");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        for (ConstraintViolation<UserDTO> violation : violations) {
            System.out.println(violation.getMessage());
        }

        assertEquals(2, violations.size());
    }

    @Test
    void should_fail_when_age_invalid() {
        UserDTO dto = new UserDTO();
        dto.setUsername("Tom");
        dto.setAge(17);

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        for (ConstraintViolation<UserDTO> violation : violations) {
            System.out.println(violation.getMessage());
        }
        assertEquals(1, violations.size());
    }

    @Test
    void should_pass_when_valid() {
        UserDTO dto = new UserDTO();
        dto.setUsername("Tom");
        dto.setAge(30);

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        for (ConstraintViolation<UserDTO> violation : violations) {
            System.out.println(violation.getMessage());
        }
        assertEquals(0, violations.size());
    }
}

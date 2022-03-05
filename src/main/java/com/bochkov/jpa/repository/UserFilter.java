package com.bochkov.jpa.repository;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserFilter implements Serializable {
    private Integer age;
    private String phone;
    private  String name;
    private  String email;
}

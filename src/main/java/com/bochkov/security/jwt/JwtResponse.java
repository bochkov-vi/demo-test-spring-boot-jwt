package com.bochkov.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class JwtResponse implements Serializable {
    private final String jwttoken;
}

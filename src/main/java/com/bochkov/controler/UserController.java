package com.bochkov.controler;

import com.bochkov.jpa.entity.User;
import com.bochkov.jpa.repository.UserFilter;
import com.bochkov.jpa.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class UserController {
    UserRepository repository;

    @GetMapping("/users")
    public Page<User> findAllUsers(Pageable pageable, UserFilter filter) {
        return repository.findAll(pageable, filter);
    }

    @GetMapping("/users/{id}")
    public User findAllUsers(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        ));
    }

    @PostMapping("/users")
    User createUser(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/users/{id}")
    @PatchMapping("/users/{id}")
    @PreAuthorize("hasAnyRole(#id,'ADMIN')")
    @Transactional
    public User replaceUser(@RequestBody User _entity, @P("id") @PathVariable Long id) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setEmail(_entity.getEmail()).setName(_entity.getName()).setAge(_entity.getAge());
                    entity.setProfile(_entity.getProfile());
                    entity.setPhones(_entity.getPhones());
                    return entity;
                })
                .map(entity -> repository.save(entity))
                .orElseGet(() -> repository.save(_entity));
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }


}

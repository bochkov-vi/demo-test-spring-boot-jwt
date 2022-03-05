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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public User replaceUser(@RequestBody User _entity, @P("id") @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repository.findById(id)
                .map(entity -> repository.save(copyProperties(_entity, entity)))
                .orElseGet(() -> repository.save(_entity));
    }


    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }


    protected User copyProperties(User source, User target) {
        target.setAge(source.getAge()).setEmail(source.getEmail());
        target.getPhones().clear();
        target.setProfile(Optional.ofNullable(source.getProfile()).map(p -> p.setUser(target)).orElse(null));
        target.setPhones(Optional.ofNullable(source.getPhones()).map(phones -> phones.stream().map(p -> p.setUser(target)).collect(Collectors.toSet())).orElse(Collections.emptySet()));
        return target;
    }


}

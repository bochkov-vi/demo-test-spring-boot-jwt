package com.bochkov.controler;

import com.bochkov.jpa.entity.Phone;
import com.bochkov.jpa.entity.Profile;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
    @Transactional
    public User replaceUser(@RequestBody User _entity, @P("id") @PathVariable Long id) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setEmail(_entity.getEmail()).setName(_entity.getName()).setAge(_entity.getAge());
                    entity.setProfile(Optional.ofNullable(entity.getProfile())
                            .map(existingProfile -> {
                                return existingProfile.setCash(
                                        Optional.ofNullable(_entity.getProfile()).map(Profile::getCash).orElse(BigDecimal.ZERO));
                            })
                            .orElseGet(() -> {
                                return Optional.ofNullable(_entity.getProfile()).map(putProfile -> putProfile.setUser(entity)).orElse(null);
                            }));

                    List<Phone> deletedPhones = entity.getPhones().stream().filter(phoneEntity -> {
                        boolean exist = Optional.ofNullable(_entity.getPhones()).map(putPhones -> putPhones.stream().anyMatch(putPhone -> Objects.equals(putPhone.getPhone(), phoneEntity.getPhone()))).orElse(false);
                        return !exist;
                    }).collect(Collectors.toList());

                    entity.getPhones().removeAll(deletedPhones);

                    if (_entity.getPhones() != null) {
                        _entity.getPhones().stream().forEach(putPhone -> {
                            boolean exist = entity.getPhones().stream().anyMatch(phone -> Objects.equals(putPhone.getPhone(), phone.getPhone()));
                            if (!exist) {
                                entity.getPhones().add(putPhone.setUser(entity));
                            }
                        });
                    }
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

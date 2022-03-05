package com.bochkov.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "USERS")
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends AbstractEntity {

    @Column(nullable = false)
    String name;

    @Column(length = 3)
    Integer age;

    @NotNull(message = "email can't be null")
    @Email(message = "wrong email format")
    @Column(unique = true, nullable = false)
    String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Phone> phones;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Profile profile;

    public User(String name, Integer age, String email, Set<Phone> phones, Profile profile) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phones = phones;
        this.profile = profile;
        if (this.phones != null) {
            this.phones.stream().forEach(phone -> phone.setUser(this));
        }
        if (this.profile != null) {
            this.profile.setUser(this);
        }
    }

    public User(String name, Integer age, String email, BigDecimal cash, String... phones) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.profile = new Profile(cash, this);
        if (phones != null && phones.length > 0) {
            this.phones = Arrays.stream(phones).map(Phone::new).map(phone -> phone.setUser(this)).collect(Collectors.toSet());
        }
    }

    static public User create() {
        return create(0);
    }

    static public User create(Integer num) {
        num = num != null ? num : 0;
        String name = num != null ? String.format("user-%s", num) : "user-0";
        BigDecimal cash = BigDecimal.valueOf(new Random().nextInt(10000), 2);
        return new User(name, 18, name + "@yandex.ru", cash, String.format("%010d", num));
    }

    static public List<User> createUsers(Integer limit) {
        return IntStream.range(0, limit).mapToObj(num -> create(num)).collect(Collectors.toList());
    }

    @JsonIgnore
    public User setPhone(String... phones) {
        this.setPhones(Arrays.stream(phones).map(phone -> new Phone(phone, this)).collect(Collectors.toSet()));
        return this;
    }

    public User setPhones(Set<Phone> phones) {
        if (phones != null) {
            if (this.phones == null) {
                this.phones = new HashSet<>();
            } else {
                this.phones.clear();
            }
            this.phones.addAll(phones.stream().map(phone -> phone.setUser(User.this)).collect(Collectors.toSet()));

        }
        return this;
    }

    public User setProfile(Profile profile) {
        this.profile = profile;
        profile.setUser(this);
        return this;

    }
}

package com.bochkov.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "PHONES")
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Phone extends AbstractEntity {

    @Column(unique = true, name = "PHONE")
    @NotNull
    @Pattern(regexp = "(^[0-9]{10}$)", message = "phone format not valid")
    String phone;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    User user;

    public Phone(String phone) {
        this.phone = phone;
    }
}

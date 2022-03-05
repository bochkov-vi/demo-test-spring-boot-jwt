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
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "PROFILES")
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Profile extends AbstractEntity {
    @Column(name = "CASH", precision = 19, scale = 2)
    BigDecimal cash;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    User user;

    public Profile(BigDecimal cash) {
        this.cash = cash;
    }
}

package com.bochkov.jpa.repository;

import com.bochkov.jpa.entity.Phone;
import com.bochkov.jpa.entity.User;
import com.google.common.base.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    default Page<User> findAll(Pageable pageRequest, UserFilter filter) {
        if (filter != null) {
            Specification<User> specification = (r, q, b) -> {
                if (filter.getAge() != null) {
                    return b.equal(r.get("age"), filter.getAge());
                }
                return null;
            };
            if (!Strings.isNullOrEmpty(filter.getEmail())) {
                specification = specification.and((r, q, b) -> b.equal(r.get("email"), filter.getEmail()));
            }
            if (!Strings.isNullOrEmpty(filter.getPhone())) {
                specification = specification.and((r, q, b) -> {
                    Subquery subquery = q.subquery(Phone.class);
                    Root<User> sr = subquery.correlate(r);
                    subquery.select(sr);
                    subquery.where(b.equal(sr.join("phones").get("phone"), filter.getPhone()));
                    return b.exists(subquery);
                });
            }
            if (!Strings.isNullOrEmpty(filter.getName())) {
                specification = specification.and((r, q, b) -> {
                    Path<String> name = r.get("name");
                    q.orderBy(b.asc(name), b.asc(b.locate(name, filter.getName())));
                    return b.like(b.lower(name), String.format("%%%s%%", filter.getName().toLowerCase()));
                });
            }
            return findAll(specification, pageRequest);
        }
        return findAll(pageRequest);
    }

    Optional<User> findByName(String username);
}

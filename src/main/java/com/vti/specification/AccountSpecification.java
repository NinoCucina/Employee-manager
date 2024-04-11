package com.vti.specification;

import com.vti.entity.Account;
import com.vti.form.AccountFilterForm;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AccountSpecification {
    public static Specification<Account> buildSpec(AccountFilterForm form) {
        if (form == null) {
            return null;
        }
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(form.getSearch())) {
                String pattern = "%" + form.getSearch() + "%";
                Predicate hasUsernameLike = builder.like(root.get("username"), pattern);
                // Predicate hasDepartmentNameLike = builder.like(root.get("department").get("name"), pattern);
                Predicate hasFirstNameLike = builder.like(root.get("firstName"), pattern);;
                Predicate hasLastNameLike = builder.like(root.get("lastName"), pattern);;
                predicates.add(builder.or(
                        hasUsernameLike,
                        // hasDepartmentNameLike,
                        hasFirstNameLike,
                        hasLastNameLike
                ));
            }
            if (form.getMinId() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("id"),
                        form.getMinId()
                ));
            }
            if (form.getMaxId() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("id"),
                        form.getMaxId()
                ));
            }
            if (form.getRole() != null) {
                predicates.add(builder.equal(
                        root.get("role"),
                        form.getRole()
                ));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

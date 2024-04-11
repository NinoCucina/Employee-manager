package com.vti.specification;

import com.vti.entity.Department;
import com.vti.form.DepartmentFilterForm;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DepartmentSpecification {
    public static Specification<Department> buildSpec(DepartmentFilterForm form) {
        if (form == null) {
            return null;
        }
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(form.getSearch())) {
                predicates.add(builder.or(
                                builder.like(
                                        root.get("name"),
                                        "%" + form.getSearch() + "%"
                                )
                        )
                );
            }
            if (form.getType() != null) {
                predicates.add(builder.equal(
                        root.get("type"),
                        form.getType()
                ));
            }
            if (form.getCreatedAt() != null) {
                predicates.add(builder.equal(
                        root.get("createdAt").as(LocalDateTime.class),
                        form.getCreatedAt()
                ));
            }
            if (form.getMinCreatedAt() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("createdAt").as(LocalDateTime.class),
                        form.getMinCreatedAt()
                ));
            }
            if (form.getMaxCreatedAt() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("createdAt").as(LocalDateTime.class),
                        form.getMaxCreatedAt()
                ));
            }
            if (form.getMinCreatedYear() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        builder.function("YEAR", Integer.class, root.get("createdAt")),
                        form.getMinCreatedYear()
                ));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

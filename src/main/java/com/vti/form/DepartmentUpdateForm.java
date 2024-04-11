package com.vti.form;

import com.vti.validation.DepartmentIdExists;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateForm {
    @DepartmentIdExists
    private int id;
    private String name;
    private int totalMembers;
    private String type;
}

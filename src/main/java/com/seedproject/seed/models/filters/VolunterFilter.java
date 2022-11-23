package com.seedproject.seed.models.filters;

import com.seedproject.seed.models.enums.RoleName;
import com.seedproject.seed.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolunterFilter {
    public Status status;
    public RoleName roleId;
}

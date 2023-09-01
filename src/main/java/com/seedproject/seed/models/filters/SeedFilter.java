package com.seedproject.seed.models.filters;

import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.models.enums.ContributorState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeedFilter {
    public ContributorState status;
    public ContributionType contributionType;
    public String seedName;
    public String applicantName;
    public String viewPage;
}

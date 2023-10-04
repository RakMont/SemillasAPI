package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.enums.ContributionType;
import lombok.Value;

@Value
public class ComboSeed {
    String contributor_id;
    String country;
    String city;
    String largename;
    String email;
    String phone;
    String dni;
    String contributionType;

    public ComboSeed(String contributor_id, Contributor contributor) {
        this.contributor_id = contributor_id;
        this.country = contributor.getCountry();
        this.city = contributor.getCity();
        this.largename = contributor.getSeedFullName();
        this.email = contributor.getUser().getEmail();
        this.phone = contributor.getUser().getPhone();
        this.dni = contributor.getUser().getDni();
        this.contributionType = contributor.getActiveContribution().getContribution_key()
                .equals(ContributionType.APORTE_CONSTANTE) ?  "Aporte Constante" :
                contributor.getActiveContribution().getContribution_key()
                        .equals(ContributionType.APORTE_UNICO) ? "Aporte Único" : " Aporte Empresas"
        ;
    }
}

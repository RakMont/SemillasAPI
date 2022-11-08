package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.TrackingAssignment;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.repositories.ContributorRepository;
import com.seedproject.seed.repositories.TrackingAssignmentRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TrackingAssignmentService {
    @Inject
    TrackingAssignmentRepository trackingAssignmentRepository;
    @Inject
    ContributorRepository contributorRepository;
    public Table findtrackingSeeds(Long id){
        List<TrackingAssignment> contributors = trackingAssignmentRepository.findByVolunterId(id);
        Table resultTable = this.getContributtorsInFormat(contributors);
        return resultTable;
    }

    private Table getContributtorsInFormat(List<TrackingAssignment> trackingAssignments){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        Contributor contributor;
        for (TrackingAssignment trackingAssignment: trackingAssignments){
            List<Cell> cells = new ArrayList<Cell>();
            contributor = this.contributorRepository.getById(trackingAssignment.getContributor_id());
            cells.add(new Cell(
                    new CellHeader("#",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,String.valueOf(index),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Semilla",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributor.getUser().getName() + ' ' + contributor.getUser().getLastname()  ,
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Celular",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, contributor.getUser().getPhone(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Correo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, contributor.getUser().getEmail(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable Registro",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, "SISTEMA",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tipo aporte",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null,
                                            contributor.getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value : ColorCode.UNIQUE_CONTRIBUTION.value, false,
                                            null,null,
                                            contributor.getContributionConfig().getContribution_key().toString(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                            new CellHeader("Opciones",0,"String",false,null),
                            new CellProperty(null,false,null,null),
                            new ArrayList<CellContent>(
                                    Arrays.asList(
                                            new CellContent("iconAccion",
                                                    "library_books", ColorCode.VIEW_TRACKING_SEEDS.value, true,
                                                    "Donations","Seguimiento de aportes", null,
                                                    new ArrayList<CellParam>(Arrays.asList(
                                                            new CellParam("contributorId",contributor.getContributor_id().toString()),
                                                            new CellParam("trackingassignmentId",trackingAssignment.getTracking_assignment_id().toString()),
                                                            new CellParam("contributionConfigId",contributor.getContributionConfig().getContribution_config_id().toString())
                                                    ))
                                            ))
                            )
                    )
            );
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    public void saveTrackingAssignment(TrackingAssignment trackingAssignment){
        TrackingAssignment result = this.trackingAssignmentRepository.save(trackingAssignment);
        Long res = result.getTracking_assignment_id();
    }
}

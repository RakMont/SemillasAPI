package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.TrackingAssignment;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.models.filters.ContributorFilter;
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
    EncripttionService encripttionService;
    @Inject
    ContributorRepository contributorRepository;
    public Table getVolunterTrackingSeeds(String id){
        id = encripttionService.decrypt(id);
        List<TrackingAssignment> contributors = trackingAssignmentRepository.findByVolunterId(Long.parseLong(id));
        Table resultTable = this.getContributtorsInFormat(contributors);
        return resultTable;
    }

    public Table getAllTrackingSeeds(ContributorFilter contributorFilter){
        List<Contributor> contributors = contributorRepository.findAll();
        if (contributorFilter.getState() != null){
            contributors.removeIf(contributor -> contributor.getContributorState()!= contributorFilter.getState());
        }
        if (contributorFilter.getName() != null){

        }
        //Table resultTable = new Table(new TableRow());
        return null;
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

    public void saveTrackingAssignment(TrackingAssignmentDao trackingAssignment){
        trackingAssignment.setTracking_assignment_id(encripttionService.decrypt(trackingAssignment.getTracking_assignment_id()));
        trackingAssignment.setVolunter_id(encripttionService.decrypt(trackingAssignment.getVolunter_id()));
        trackingAssignment.setContributor_id(encripttionService.decrypt(trackingAssignment.getContributor_id()));
        TrackingAssignment payload = new TrackingAssignment();

        payload.setTracking_assignment_id(trackingAssignment.getTracking_assignment_id() != null ?
                Long.parseLong(trackingAssignment.getTracking_assignment_id()): null);
        payload.setVolunter_id(Long.parseLong(trackingAssignment.getVolunter_id()));
        payload.setContributor_id(Long.parseLong(trackingAssignment.getContributor_id()));
        payload.setStatus(trackingAssignment.getStatus());
        payload.setStart_date(trackingAssignment.getStart_date());
        payload.setEnd_date(trackingAssignment.getEnd_date());

        try {
            TrackingAssignment result = this.trackingAssignmentRepository.save(payload);
        }catch (Exception exception){
            throw exception;
        }
    }
}

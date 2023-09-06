package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.TrackingAssignmentDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.TrackingAssignment;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.*;
import com.seedproject.seed.models.filters.ContributorFilter;
import com.seedproject.seed.models.filters.SeedFilter;
import com.seedproject.seed.repositories.ContributorRepository;
import com.seedproject.seed.repositories.TrackingAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
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
    ContributionRecordService contributionRecordService;
    @Inject
    ContributorRepository contributorRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;


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

    private Table getTrackingContributorsInFormat(List<TrackingSeedDTO> trackingAssignments){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        for (TrackingSeedDTO trackingSeed: trackingAssignments){
            List<Cell> cells = new ArrayList<Cell>();
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
                                            trackingSeed.getName() + ' ' + trackingSeed.getLastname()  ,
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado de semilla",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(this.getContributorState(trackingSeed)))
                    )
            );
            cells.add(new Cell(
                    new CellHeader("Celular",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, trackingSeed.getPhone(),
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
                                            null,null, trackingSeed.getEmail(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Ultimo aporte",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, "fecha",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Total aporte",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, "20BS",
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
                                           trackingSeed.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value : ColorCode.UNIQUE_CONTRIBUTION.value, false,
                                            null,null,
                                            trackingSeed.getContribution_key().toString(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                            new CellHeader("Opciones",0,"String",false,null),
                            new CellProperty(null,false,null,null),
                            this.getOptionsTracking(trackingSeed))

            );
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private CellContent getContributorState(TrackingSeedDTO trackingSeedDTO){
        CellContent cellContent = null;
        if (trackingSeedDTO.getContributor_state() == ContributorState.ACCEPTED.value){
            cellContent = new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_ACEPTED.value, false,
                    null,null, "Activo",
                    null
            );
        } else if (trackingSeedDTO.getContributor_state() == ContributorState.PENDING.value){
            cellContent = new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PENDING.value, false,
                    null,null, "Pendiente",
                    null
            );
        } else if (trackingSeedDTO.getContributor_state() == ContributorState.REJECTED.value){
            cellContent = new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Rechazado",
                    null
            );
        }else if (trackingSeedDTO.getContributor_state() == ContributorState.PAUSED.value){
            cellContent = new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PAUSED.value, false,
                    null,null, "En pausa",
                    null
            );
        }else if (trackingSeedDTO.getContributor_state() == ContributorState.DESERTER.value){
            cellContent = new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Desertante",
                    null
            );
        }
        return cellContent;
    }
    private List<CellContent>  getOptionsTracking(TrackingSeedDTO trackingSeedDTO){
        List<CellContent> contents = new ArrayList<>();
        if (trackingSeedDTO.getContribution_key().equals(ContributionType.APORTE_CONSTANTE) ){
            contents.add( new CellContent("iconAccion",
                    "library_books", ColorCode.VIEW_TRACKING_SEEDS.value, true,
                    "Donations","Seguimiento de aportes", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(trackingSeedDTO.getContributor_id().toString())),
                            new CellParam("trackingassignmentId",
                                    encripttionService.encrypt(trackingSeedDTO.getTracking_assignment_id().toString())),
                            new CellParam("contributionConfigId",
                                    encripttionService.encrypt(trackingSeedDTO.getContribution_config_id().toString()))
                    ))
            ) );
        }
        else {
            TrackingAssignment trackingAssignment = trackingAssignmentRepository.getById(trackingSeedDTO.getTracking_assignment_id());
            List<ContributionRecord> uniqueRecord = contributionRecordService.uniqueContributorHasRegisterContribution(trackingAssignment);
            if (!uniqueRecord.isEmpty()){
                contents.add( new CellContent("iconAccion",
                        "edit", ColorCode.EDIT.value, true,
                        "UpdateRecord", "Actualizar aporte", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributionRecordId",
                                        encripttionService.encrypt(uniqueRecord.get(0).getContribution_record_id().toString()))
                        ))
                ));
                contents.add( new CellContent("iconAccion",
                        "description",ColorCode.VIEW_CONTR.value, true,
                        "SeeRecord","Ver Detalle", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributionRecordId",
                                        encripttionService.encrypt(uniqueRecord.get(0).getContribution_record_id().toString()))
                        ))));
            }
            else{
                contents.add(  new CellContent("iconAccion",
                        "add_comment", ColorCode.VIEW_TRACKING_SEEDS.value, true,
                        "ViewUniqueDonation", "Registrar aporte", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributorId",
                                        encripttionService.encrypt(trackingSeedDTO.getContributor_id().toString())),
                                new CellParam("trackingassignmentId",
                                        encripttionService.encrypt(trackingSeedDTO.getTracking_assignment_id().toString())),
                                new CellParam("contributionConfigId",
                                        encripttionService.encrypt(trackingSeedDTO.getContribution_config_id().toString()))
                        ))
                ));
            }
        }

        return contents;
    }


    public ResponseEntity<RequestResponseMessage> saveTrackingAssignment(TrackingAssignmentDao trackingAssignment){
        trackingAssignment.setTracking_assignment_id(encripttionService.decrypt(trackingAssignment.getTracking_assignment_id()));
        trackingAssignment.setVolunter_id(encripttionService.decrypt(trackingAssignment.getVolunter_id()));
        trackingAssignment.setContributor_id(encripttionService.decrypt(trackingAssignment.getContributor_id()));

        if (this.noActiveAssignment(Long.parseLong(trackingAssignment.getContributor_id()))){
            TrackingAssignment payload = new TrackingAssignment();
            payload.setTracking_assignment_id(trackingAssignment.getTracking_assignment_id() != null ?
                    Long.parseLong(trackingAssignment.getTracking_assignment_id()): null);
            payload.setVolunter_id(Long.parseLong(trackingAssignment.getVolunter_id()));
            payload.setContributor_id(Long.parseLong(trackingAssignment.getContributor_id()));
            payload.setStatus(trackingAssignment.getStatus());
            payload.setStart_date(trackingAssignment.getStart_date());
            payload.setEnd_date(trackingAssignment.getEnd_date());
            payload.setStatus(Status.ACTIVE);
            try {
                TrackingAssignment result = this.trackingAssignmentRepository.save(payload);
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Se asignó correctamente", ResponseStatus.SUCCESS), HttpStatus.CREATED);
            }catch (Exception exception){
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Error asignando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
                //throw exception;
            }
        }else {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La semilla ya tiene un responsable de seguimiento", ResponseStatus.WARNING),HttpStatus.BAD_REQUEST);
        }
    }

    public boolean noActiveAssignment(Long contributorId){
       try {
           List<TrackingAssignment> assignments = this.trackingAssignmentRepository.findByContributorId(contributorId);
           return assignments.isEmpty();
       } catch (Exception e){
           throw e;
       }
    }

    ////////////////////////////////////////////////////////////////
    /*VOLUNTEER TRACKING SEEDS*/

    public Table getVolunteerTrackingSeeds(String id){
        id = encripttionService.decrypt(id);

        try {
            List<TrackingSeedDTO> trackingAssignments = this.trackingAssignmentRepository.findByTrackingContributors(Long.parseLong(id));
            trackingAssignments.removeIf(ta -> ta.getStatus() == null || !ta.getStatus().equals(Status.ACTIVE));
            return this.getTrackingContributorsInFormat(trackingAssignments);
            //List<TrackingAssignment> trackingAssignments = trackingAssignmentRepository.findByVolunterId(Long.parseLong(id));
            //trackingAssignments.removeIf(ta -> ta.getStatus() == null || !ta.getStatus().equals(Status.ACTIVE));
            //return this.getContributorsInFormat(trackingAssignments);
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }

    }

    public Table findVolunteerTrackingSeeds(Principal principal, SeedFilter seedFilter){

        try {
            Volunter volunteerDTO =(Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
            List<TrackingSeedDTO> trackingAssignments = trackingAssignmentRepository.findByTrackingContributors(volunteerDTO.getVolunterId());
            trackingAssignments.removeIf(ta -> ta.getStatus() == null || !ta.getStatus().equals(Status.ACTIVE));
            return this.getTrackingContributorsInFormat(trackingAssignments);

        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }
}

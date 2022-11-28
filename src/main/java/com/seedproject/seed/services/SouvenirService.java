package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.SouvenirTrackingDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.models.filters.SouvenirTrackingFilter;
import com.seedproject.seed.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SouvenirService {

    @Inject
    BenefitedCollaboratorRepository benefitedCollaboratorRepository;
    @Inject
    SouvenirTrackingRepository souvenirTrackingRepository;
    @Inject
    EncripttionService encripttionService;
    @Inject
    ContributorRepository contributorRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    CommentRecordRepository commentRecordRepository;

    public ResponseEntity<RequestResponseMessage> createBenefitedCollaborator(SouvenirTrackingDao souvenirTrackingDao){
        souvenirTrackingDao.setContributorId(encripttionService.decrypt(souvenirTrackingDao.getContributorId()));
        souvenirTrackingDao.setVolunteerInChargeId(encripttionService.decrypt(souvenirTrackingDao.getVolunteerInChargeId()));

        Contributor contributor = contributorRepository.getById(Long.parseLong(souvenirTrackingDao.getContributorId()));
        Volunter volunter = volunterRepository.getById(Long.parseLong(souvenirTrackingDao.getVolunteerInChargeId()));
        BenefitedCollaborator benefitedCollaborator = new BenefitedCollaborator();
        benefitedCollaborator.setSelected_date(souvenirTrackingDao.getSelected_date());
        benefitedCollaborator.setObservation(souvenirTrackingDao.getObservation());
        benefitedCollaborator.setCity(souvenirTrackingDao.getCity());
        benefitedCollaborator.setContributor(contributor);
        benefitedCollaborator.setRegisterVolunteer(volunter);
        try {
            benefitedCollaboratorRepository.save(benefitedCollaborator);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La semilla fue seleccionada como beneficiada", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Ocurrio un error intentelo mas tarde", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> createSouvenirTracking(SouvenirTrackingDao souvenirTrackingDao){
        souvenirTrackingDao.setBenefitedCollaboratorId(encripttionService.decrypt(souvenirTrackingDao.getBenefitedCollaboratorId()));
        souvenirTrackingDao.setVolunteerInChargeId(encripttionService.decrypt(souvenirTrackingDao.getVolunteerInChargeId()));
        SouvenirTracking souvenirTracking = new SouvenirTracking();
        Volunter volunter = volunterRepository.getById(Long.parseLong(souvenirTrackingDao.getVolunteerInChargeId()));
        BenefitedCollaborator benefitedCollaborator = benefitedCollaboratorRepository.getById(Long.parseLong(souvenirTrackingDao.getBenefitedCollaboratorId()));
        if (!souvenirTrackingDao.getSouvenirTrackingComments().isEmpty()){
            List<CommentRecord> souvenirTrackingComments = new ArrayList<>();
            souvenirTrackingDao.getSouvenirTrackingComments().forEach(commentRecordDao -> {
                CommentRecord commentRecord = new CommentRecord();
                if (commentRecordDao.getCommentRecordId() != null){
                    commentRecordDao.setCommentRecordId(encripttionService.decrypt(commentRecordDao.getCommentRecordId()));
                    commentRecord = commentRecordRepository.getById(Long.parseLong(commentRecordDao.getCommentRecordId()));
                    commentRecord.setComment(commentRecordDao.getComment());
                    souvenirTrackingComments.add(commentRecord);
                }else {
                    commentRecord.setComment(commentRecordDao.getComment());
                    commentRecord.setComment_date(new Date());
                    commentRecord.setRegisterVolunteer(volunter);
                    souvenirTrackingComments.add(commentRecord);
                }
            });
            souvenirTracking.setSouvenirTrackingComments(souvenirTrackingComments);
        }
        souvenirTracking.setSpentAmount(souvenirTrackingDao.getSpentAmount());
        souvenirTracking.setSouvenir_send_date(souvenirTrackingDao.getSouvenir_send_date());
        souvenirTracking.setTrackingStatus(souvenirTrackingDao.getTrackingStatus());
        souvenirTracking.setBenefitedCollaborator(benefitedCollaborator);
        try {
            souvenirTrackingRepository.save(souvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La creado registro", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Ocurrio un error intentelo mas tarde", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> deleteBenefitedCollaboratorById(String id){
        id = encripttionService.decrypt(id);
        try {
            benefitedCollaboratorRepository.deleteById(Long.parseLong(id));
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La semilla fue eliminada de la lista de beneficiados", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Ocurrio un error intentelo mas tarde", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> deleteSouvenirTrackingById(String id){
        id = encripttionService.decrypt(id);
        try {
            souvenirTrackingRepository.deleteById(Long.parseLong(id));
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La el registro de seguimiento se eliminó", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Ocurrió un error intentelo mas tarde", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }


    public Table getAllSouvenirTracking(SouvenirTrackingFilter souvenirTrackingFilter){
        try {
            List<BenefitedCollaborator> benefitedCollaborators = benefitedCollaboratorRepository.findAll();
            //benefitedCollaborators.removeIf(ta -> ta.getStatus() == null || !ta.getStatus().equals(Status.ACTIVE));
            return this.getBenefitedSeedsInFormat(benefitedCollaborators);
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    public Table getAllBenefitedSeeds(SouvenirTrackingFilter souvenirTrackingFilter){
        try {
            List<BenefitedCollaborator> benefitedCollaborators = benefitedCollaboratorRepository.findAll();
            //benefitedCollaborators.removeIf(ta -> ta.getStatus() == null || !ta.getStatus().equals(Status.ACTIVE));
            return this.getBenefitedSeedsInFormat(benefitedCollaborators);
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    public Table getBenefitedSeedsInFormat(List<BenefitedCollaborator> benefitedCollaborators){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (BenefitedCollaborator benefitedCollaborator: benefitedCollaborators){
            List<Cell> cells = new ArrayList<Cell>();
            cells.add(new Cell(
                    new CellHeader("No",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,String.valueOf(index),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Semilla beneficiada",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            benefitedCollaborator.getContributor().getUser().getName() + ' '+benefitedCollaborator.getContributor().getUser().getLastname(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Ciudad",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, benefitedCollaborator.getCity(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha de selección",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,formatter.format(benefitedCollaborator.getSelected_date()) ,
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable de registro",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            benefitedCollaborator.getRegisterVolunteer().getUser().getName() + ' '+benefitedCollaborator.getRegisterVolunteer().getUser().getLastname(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getActions(benefitedCollaborator))
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private List<CellContent> getActions(BenefitedCollaborator benefitedCollaborator){
        List<CellContent> contents = new ArrayList<>();
                contents.add(new CellContent("iconAccion",
                        "edit", ColorCode.EDIT.value, true,
                        "editVolunter","Editar", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("benefitedCollaboratorId",
                                        encripttionService.encrypt(benefitedCollaborator.getBenefited_collaborator_id().toString()))))));
                contents.add(new CellContent("iconAccion",
                        "delete",ColorCode.DELETE.value, true,
                        "delete","Desactivar", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("benefitedCollaboratorId",
                                        encripttionService.encrypt(benefitedCollaborator.getBenefited_collaborator_id().toString()))))));

        return contents;
    }

    public List<ComboSeed> findBenefitedSeeds(){
        List<BenefitedCollaborator> contributors = benefitedCollaboratorRepository.findAll();
        List<ComboSeed> activecontr= new ArrayList<>();
        for (BenefitedCollaborator contributor:contributors){
            activecontr.add(new ComboSeed(
                    encripttionService.encrypt( contributor.getBenefited_collaborator_id().toString())
                    ,contributor.getContributor().getUser().getName(),contributor.getContributor().getUser().getLastname(),
                    contributor.getContributor().getUser().getName()+ ' ' + contributor.getContributor().getUser().getLastname(),
                    contributor.getContributor().getUser().getEmail(),
                    contributor.getContributor().getUser().getPhone(),
                    contributor.getContributor().getUser().getDni()));
        }
        return activecontr;
    }
}

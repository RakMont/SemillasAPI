package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.SeedSouvenirTrackingDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.models.enums.TrackingStatus;
import com.seedproject.seed.models.filters.SouvenirTrackingFilter;
import com.seedproject.seed.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SouvenirService {
    @Inject
    EncripttionService encripttionService;
    @Inject
    SouvenirTrackingRepository seedSouvenirTrackingRepository;
    @Inject
    ContributorRepository contributorRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    CommentRecordRepository commentRecordRepository;

   /* public ResponseEntity<RequestResponseMessage> createSouvenirTracking(SouvenirTrackingDao souvenirTrackingDao){
        souvenirTrackingDao.setBenefitedCollaboratorId(encripttionService.decrypt(souvenirTrackingDao.getBenefitedCollaboratorId()));
        souvenirTrackingDao.setVolunteerInChargeId(encripttionService.decrypt(souvenirTrackingDao.getVolunteerInChargeId()));
        SeedSouvenirTracking seedSouvenirTracking = new SeedSouvenirTracking();
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
            seedSouvenirTracking.setSouvenirTrackingComments(souvenirTrackingComments);
        }
        seedSouvenirTracking.setSpentAmount(souvenirTrackingDao.getSpentAmount());
        seedSouvenirTracking.setSouvenirSendDate(souvenirTrackingDao.getSouvenir_send_date());
        seedSouvenirTracking.setTrackingStatus(souvenirTrackingDao.getTrackingStatus());
        seedSouvenirTracking.setBenefitedCollaborator(benefitedCollaborator);
        try {
            souvenirTrackingRepository.save(seedSouvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La creado registro", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Ocurrio un error intentelo mas tarde", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }*/

    public Table findAllSouvenirsTracking(SouvenirTrackingFilter souvenirTrackingFilter){
        try{
            List<SeedSouvenirTracking> seedSouvenirTrackingList = seedSouvenirTrackingRepository.findAll();
            seedSouvenirTrackingList.removeIf(seedSouvenirTracking -> !seedSouvenirTracking.getRegister_exist());
            seedSouvenirTrackingList.removeIf(seedSouvenirTracking -> !seedSouvenirTracking.getTrackingStatus().equals(souvenirTrackingFilter.getTrackingStatus()));
            return this.getSouvenirTrackingStatus(seedSouvenirTrackingList);
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private Table getSouvenirTrackingStatus(List<SeedSouvenirTracking> seedSouvenirTrackingList) {
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (SeedSouvenirTracking seedSouvenirTracking: seedSouvenirTrackingList){
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
                    new CellHeader("Semilla beneficiada",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            seedSouvenirTracking.getBenefitedContributor().getSeedFullName(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Ciudad envío",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, seedSouvenirTracking.getChosenCity(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable a cargo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, seedSouvenirTracking.getVolunteerInCharge().getFullName(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado de envío",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList( seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_DELIVERED) ?
                                            new CellContent("chipContent",
                                                    null, ColorCode.SOUVENIR_DELIVERED.value,false,
                                                    null,null, "Souvenir Entregado",
                                                    null
                                            )
                                            :  seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_PENDING) ?
                                            new CellContent(
                                                    "chipContent",
                                                    null, ColorCode.SOUVENIR_PENDING.value,false,
                                                    null,null, "Pendiente de envío",
                                                    null
                                            ) :new CellContent(
                                            "chipContent",
                                            null, ColorCode.SOUVENIR_SENT.value,false,
                                            null,null, "Souvenir Enviado",
                                            null
                                    )
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha envío",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, formatter.format(seedSouvenirTracking.getSouvenirSendDate()),
                                            null)
                            )
                    )
            ));

            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedActions(seedSouvenirTracking))
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private List<CellContent> getSeedActions(SeedSouvenirTracking seedSouvenirTracking) {
        List<CellContent> contents = new ArrayList<>();
        contents.add(
                new CellContent("iconAccion", "description",
                        ColorCode.VIEW_CONTR.value, true,
                        "ViewSouvenirTracking","Ver Detalle", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                                encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));

        contents.add(
                new CellContent("iconAccion", "edit",
                        ColorCode.EDIT.value, true,
                        "EditSouvenirTracking","Editar", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                                encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));
        contents.add(
                new CellContent("iconAccion", "delete",
                        ColorCode.STATE_REJECTED.value, true,
                        "DeleteSouvenirTracking","Eliminar", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                                encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));



        return contents;
    }

    public ResponseEntity<RequestResponseMessage> createSeedSouvenirTracking(Principal principal, SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        try{
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getBenefitedContributorId())));
            Optional<Volunter> volunteerInCharge = volunterRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getVolunteerInChargeId())));
            SeedSouvenirTracking seedSouvenirTracking = new SeedSouvenirTracking();
            //seedSouvenirTracking.setSelectedDate(seedSouvenirTrackingDao.getSelectedDate());
            seedSouvenirTracking.setSelectedDate(new Date());
            seedSouvenirTracking.setSouvenirSendDate(seedSouvenirTrackingDao.getSouvenirSendDate());
            seedSouvenirTracking.setTrackingStatus(seedSouvenirTrackingDao.getTrackingStatus());
            seedSouvenirTracking.setSpentAmount(seedSouvenirTrackingDao.getSpentAmount());
            seedSouvenirTracking.setChosenCity(seedSouvenirTrackingDao.getChosenCity());
            seedSouvenirTracking.setObservation(seedSouvenirTrackingDao.getObservation());
            seedSouvenirTracking.setRegister_exist(true);
            seedSouvenirTracking.setBenefitedContributor(contributor.get());
            seedSouvenirTracking.setVolunteerInCharge(volunteerInCharge.get());
            seedSouvenirTrackingRepository.save(seedSouvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Los datos se guardaron correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
            //throw new RuntimeException(e);

        }
    }

    public ResponseEntity<RequestResponseMessage> updateSeedSouvenirTracking(Principal principal, SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        try{
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getBenefitedContributorId())));
            Optional<Volunter> volunteerInCharge = volunterRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getVolunteerInChargeId())));
            SeedSouvenirTracking seedSouvenirTracking = new SeedSouvenirTracking();
            seedSouvenirTracking.setSeedSouvenirTrackingId(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getSeedSouvenirTrackingId())));
            seedSouvenirTracking.setSelectedDate(seedSouvenirTrackingDao.getSelectedDate());
            seedSouvenirTracking.setSouvenirSendDate(seedSouvenirTrackingDao.getSouvenirSendDate());
            seedSouvenirTracking.setTrackingStatus(seedSouvenirTrackingDao.getTrackingStatus());
            seedSouvenirTracking.setSpentAmount(seedSouvenirTrackingDao.getSpentAmount());
            seedSouvenirTracking.setChosenCity(seedSouvenirTrackingDao.getChosenCity());
            seedSouvenirTracking.setObservation(seedSouvenirTrackingDao.getObservation());
            seedSouvenirTracking.setRegister_exist(true);
            seedSouvenirTracking.setBenefitedContributor(contributor.get());
            seedSouvenirTracking.setVolunteerInCharge(volunteerInCharge.get());

            seedSouvenirTrackingDao.getSouvenirTrackingComments().forEach(commentRecordDTO -> {
                if (commentRecordDTO.getCommentId()!= null )commentRecordDTO.setCommentId(encripttionService.decrypt(commentRecordDTO.getCommentId()));
                seedSouvenirTracking.getSouvenirTrackingComments().add(commentRecordDTO.getCommentRecord(commentRecordDTO, volunteerInCharge.get()));
            });

            seedSouvenirTrackingRepository.save(seedSouvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Los datos se guardaron correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> deleteSeedSouvenirTracking(String id) {
        try{
            Optional<SeedSouvenirTracking> seedSouvenirTracking = seedSouvenirTrackingRepository.findById(Long.parseLong(encripttionService.decrypt(id)));
            seedSouvenirTracking.get().setRegister_exist(false);
            seedSouvenirTrackingRepository.save(seedSouvenirTracking.get());
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Se elimino Correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error eliminando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
            //throw new RuntimeException(e);

        }
    }

    public SeedSouvenirTrackingDTO getSeedSouvenirTracking(String id) {
        try{
            Optional<SeedSouvenirTracking> seedSouvenirTracking = seedSouvenirTrackingRepository.findById(Long.parseLong(encripttionService.decrypt(id)));
            //seedSouvenirTracking.get().setRegister_exist(false);
            //seedSouvenirTrackingRepository.save(seedSouvenirTracking.get());
            SeedSouvenirTrackingDTO seedSouvenirTrackingDTO = new SeedSouvenirTrackingDTO(seedSouvenirTracking.get());
            seedSouvenirTrackingDTO.setSeedSouvenirTrackingId(encripttionService.encrypt(seedSouvenirTrackingDTO.getSeedSouvenirTrackingId()));
            seedSouvenirTrackingDTO.setBenefitedContributorId(encripttionService.encrypt(seedSouvenirTrackingDTO.getBenefitedContributorId()));
            seedSouvenirTrackingDTO.setVolunteerInChargeId(encripttionService.encrypt(seedSouvenirTrackingDTO.getVolunteerInChargeId()));
            seedSouvenirTrackingDTO.setBenefitedContributorLabel(new ComboSeed(seedSouvenirTrackingDTO.getBenefitedContributorId(), seedSouvenirTracking.get().getBenefitedContributor()));
            seedSouvenirTrackingDTO.setVolunteerInChargeLabel(new ComboVolunteer(seedSouvenirTrackingDTO.getVolunteerInChargeId(), seedSouvenirTracking.get().getVolunteerInCharge()));
            seedSouvenirTracking.get().getSouvenirTrackingComments().forEach(commentRecord -> {
                seedSouvenirTrackingDTO.getSouvenirTrackingComments().add(
                        new CommentRecordDTO(encripttionService.encrypt(commentRecord.getComment_record_id().toString()),commentRecord));
            });
            return seedSouvenirTrackingDTO;
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}

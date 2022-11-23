package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.SouvenirTrackingDao;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
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

}

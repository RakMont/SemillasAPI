package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.ActivityNewDTO;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.entities.ActivityNew;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.repositories.ActivityNewRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityNewService {
    @Inject
    ActivityNewRepository activityNewRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    private EncripttionService encripttionService;
    public ResponseEntity<RequestResponseMessage> createNewActivity(Principal principal, ActivityNewDTO activityNewDTO) {
        try{
            ActivityNew activityNew = activityNewDTO.getActivity(activityNewDTO);
            Volunter volunteer = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
            activityNew.setRegVolunteer(volunteer);
            activityNew.setRegisterDate(new Date());
            activityNew.setIsTranslate(false);
            activityNewRepository.save(activityNew);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La actividad fue creada con éxito", ResponseStatus.SUCCESS), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error creando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> updateActivity(Principal principal, ActivityNewDTO activityNewDTO) {
        try{
            activityNewDTO.setActivityId(encripttionService.decrypt(activityNewDTO.getActivityId()));
            ActivityNew activityNew = activityNewDTO.getActivity(activityNewDTO);
            Volunter volunteer = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
            activityNew.setRegVolunteer(volunteer);
            activityNewDTO.getTranslateList().forEach(activityNewDTO1 -> {
                activityNewDTO1.setActivityId(
                        activityNewDTO1.getActivityId() != null ? encripttionService.decrypt(activityNewDTO1.getActivityId()): null);
                ActivityNew helperActivity = activityNewDTO1.getActivity(activityNewDTO1);
                helperActivity.setRegVolunteer(volunteer);
                helperActivity.setIsTranslate(true);
                activityNew.getActivityNewsList().add(helperActivity);
            });
            activityNew.setIsTranslate(false);
            activityNewRepository.save(activityNew);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La actividad fue actualizada con éxito", ResponseStatus.SUCCESS), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error actualizando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public List<ActivityNewDTO> getAllActivities() {
       try{
           List<ActivityNewDTO> activityNewDTOList = new ArrayList<>();
           List<ActivityNew> activityNews = this.activityNewRepository.findAll();
           activityNews = activityNews.stream().filter( activityNew -> !activityNew.getIsTranslate()).collect(Collectors.toList());
           activityNews.forEach(ac->{
               ActivityNewDTO activityNewDTO =new ActivityNewDTO(encripttionService.encrypt(ac.getActivityId().toString()), ac);
               ac.getActivityNewsList().forEach(translates->{
                   activityNewDTO.getTranslateList().add(new ActivityNewDTO(encripttionService.encrypt(translates.getActivityId().toString()), translates));
               });
               activityNewDTOList.add(activityNewDTO);


           });

           return activityNewDTOList;

       }catch (Exception exception){
           return null;
       }

    }

    public ActivityNewDTO getActivity(String id) {
        try{
            Optional<ActivityNew> activityNew = this.activityNewRepository.findById(Long.parseLong(encripttionService.decrypt(id)));
            ActivityNewDTO activityNewDTO = new ActivityNewDTO(this.encripttionService.encrypt(activityNew.get().getActivityId().toString()), activityNew.get());
            activityNew.get().getActivityNewsList().forEach(ac->{
                activityNewDTO.getTranslateList().add(new ActivityNewDTO(encripttionService.encrypt(ac.getActivityId().toString()), ac));
            });
            return activityNewDTO;
        }catch (Exception exception){
            return null;
        }
    }

    public ResponseEntity<RequestResponseMessage> deleteActivity(String id) {
        id = encripttionService.decrypt(id);
        try {
            this.activityNewRepository.deleteById(Long.parseLong(id));
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La actividad fue eliminada", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception exception){
            //throw exception;
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error eliminando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }
}

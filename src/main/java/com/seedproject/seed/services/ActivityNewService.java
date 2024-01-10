package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ActivityNew;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.repositories.ActivityNewRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
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
    public List<ActivityNewDTO> getAllActivities() {
        try{
            List<ActivityNewDTO> activityNewDTOList = new ArrayList<>();
            List<ActivityNew> activityNews = this.activityNewRepository.findByOrderByRegisterDateAsc();
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
    public Table getAllActivitiesTable() {
        try{
            List<ActivityNewDTO> activityNewDTOList = new ArrayList<>();
            List<ActivityNew> activityNews = this.activityNewRepository.findByOrderByRegisterDateAsc();
            activityNews = activityNews.stream().filter( activityNew -> !activityNew.getIsTranslate()).collect(Collectors.toList());
            activityNews.forEach(ac->{
                ActivityNewDTO activityNewDTO =new ActivityNewDTO(encripttionService.encrypt(ac.getActivityId().toString()), ac);
                ac.getActivityNewsList().forEach(translates->{
                    activityNewDTO.getTranslateList().add(new ActivityNewDTO(encripttionService.encrypt(translates.getActivityId().toString()), translates));
                });
                activityNewDTOList.add(activityNewDTO);


            });

            return this.getAllActivitiesTableInFormat(activityNews);

        }catch (Exception exception){
            return null;
        }
    }

    private Table getAllActivitiesTableInFormat(List<ActivityNew> activityNewDTOList){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (ActivityNew activityNew: activityNewDTOList){
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
                    new CellHeader("Título",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            activityNew.getTitle(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha de registro",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            activityNew.getRegisterDate() !=null ?
                                                    formatter.format(activityNew.getRegisterDate()) : " ",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tiene traducción",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, activityNew.getActivityNewsList().isEmpty() ? "NO" : "SÍ",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Traducción",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(Arrays.asList(
                            new CellContent("iconAccion",
                                    "g_translate", "#1c263c", true,
                                    "seeVolunter","Ver Info", null,
                                    new ArrayList<CellParam>(Arrays.asList(
                                            new CellParam("activityId", encripttionService.encrypt(activityNew.getActivityId().toString()))
                                    )))
                    ))
            ));
            cells.add(new Cell(
                    new CellHeader("Es público",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, activityNew.getIsVisible() ? "SÍ" : "NO",
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
                                            "edit", ColorCode.EDIT.value, true,
                                            "editActivity","Reactivar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId", encripttionService.encrypt(activityNew.getActivityId().toString()))
                                            ))),
                                    new CellContent("iconAccion",
                                            "delete",ColorCode.DELETE.value, true,
                                            "deleteActivity","Eliminar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId", encripttionService.encrypt(activityNew.getActivityId().toString()))
                                            ))),
                                    new CellContent("iconAccion",
                                            "visibility",ColorCode.VIEW.value, true,
                                            "viewActivity","Ver Info", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId", encripttionService.encrypt(activityNew.getActivityId().toString()))
                                            )))
                            )
                    )
            ));
            resultList.add(new TableRow(cells));
            index++;
        }

        return new Table(resultList);
    }
}

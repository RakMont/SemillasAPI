package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ExitMessage;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.User;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.RoleName;
import com.seedproject.seed.models.enums.Status;
import com.seedproject.seed.models.filters.VolunterFilter;
import com.seedproject.seed.repositories.ExitMessageRepository;
import com.seedproject.seed.repositories.UserRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VolunterService {
    @Inject
    VolunterRepository volunterRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ExitMessageRepository exitMessageRepository;


    public Table findAllVolunter(){
        Table resultTable = this.getVoluntersInformat(volunterRepository.findAll(), false);
        return resultTable;
    }

    public Table findVoluntersByFilter(VolunterFilter volunterFilter){
        List<Volunter> volunters = volunterRepository.findAll();
        if (volunterFilter.getStatus() != null){
            volunters.removeIf(v -> v.getStatus()!= volunterFilter.getStatus());
        }
        if (volunterFilter.getRoleId() != null){
            volunters.removeIf(v -> !this.gotTheRol(volunterFilter.getRoleId(),v.getRoles()));
        }
        Table resultTable = this.getVoluntersInformat(volunters, false);
        return resultTable;
    }

    public Table findAlltrackingVolunters(){
        List<Volunter> volunters =  volunterRepository.findAll();
        volunters.removeIf(v -> !this.gotTheRol(RoleName.R_SEGUIMIENTOS,v.getRoles()));
        Table resultTable = this.getVoluntersInformat(volunters, true);
        return resultTable;
    }
    public boolean gotTheRol(RoleName roleName, List<Role> roles){
        roles.removeIf(r -> !(r.getRole_name().equals(roleName)));
        return roles.size() > 0;
    }
    private Table getVoluntersInformat(List<Volunter> volunters, Boolean isTracking){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (Volunter volunter: volunters){
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
                    new CellHeader("Nombre",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            volunter.getUser().getName() + ' '+volunter.getUser().getLastname(),
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
                                            null,null, volunter.getUser().getEmail(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsabilidades",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getVolunterRoles(volunter.getRoles()))
            ));
            cells.add(new Cell(
                    new CellHeader("Ingreso",0,"Date",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            volunter.getEntryDate() != null ?
                                                    formatter.format(volunter.getEntryDate()) : " ",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedActions(volunter,isTracking)
                            /*Arrays.asList(
                                    new CellContent("iconAccion",
                                            "edit", ColorCode.EDIT.value, true,
                                            "editVolunter","Editar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                             new CellParam("volunterId",volunter.getVolunter_id().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "delete",ColorCode.DELETE.value, true,
                                            "deleteVolunter","Eliminar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId",volunter.getVolunter_id().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "remove_red_eye",ColorCode.VIEW.value, true,
                                            "seeVolunter","Ver Info", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId",volunter.getVolunter_id().toString())
                                            )))
                            )*/
                    )
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private List<CellContent> getVolunterRoles(List<Role> roles){
        List<CellContent> contents = new ArrayList<>();
        for (Role role : roles){
            if(role.getRole_name().equals(RoleName.R_PRINCIPAL)){
                contents.add(new CellContent(
                        "chipContent",
                        null, ColorCode.R_PRINCIPAL_COLOR.value,false,
                        null,null, role.getRole_name().value,
                        null
                ));
            }
            else if(role.getRole_name().equals(RoleName.R_REGISTROS)){
                contents.add(new CellContent(
                        "chipContent",
                        null,ColorCode.R_REGISTROS_COLOR.value, false,
                        null,null, role.getRole_name().value,
                        null
                ));
            }
            else if(role.getRole_name().equals(RoleName.R_SEGUIMIENTOS)){
                contents.add(new CellContent(
                        "chipContent",
                        null,ColorCode.R_SEGUIMIENTOS_COLOR.value, false,
                        null,null, role.getRole_name().value,
                        null
                ));
            }
            else if(role.getRole_name().equals(RoleName.R_SOUVENIRS)){
                contents.add(new CellContent(
                        "chipContent",
                        null,ColorCode.R_SOUVENIRS_COLOR.value, false,
                        null,null, role.getRole_name().value,
                        null
                ));
            }
        }
        return contents;
    }

    private List<CellContent> getSeedActions(Volunter volunter, Boolean isTracking){
        List<CellContent> contents = new ArrayList<>();
        if (isTracking){
            contents.add(new CellContent("iconAccion",
                    "group", ColorCode.VIEW_ASSIGNED_SEEDS.value, true,
                    "ViewAssignedSeeds","Ver semillas asignadas", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId",volunter.getVolunterId().toString())
                    ))
            ));
            contents.add(new CellContent("iconAccion",
                    "group_add", ColorCode.ASSIGN_SEED.value, true,
                    "AssignSeed","Asignar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId",volunter.getVolunterId().toString())
                    ))
            ));
        }
        else {
            contents.add(new CellContent("iconAccion",
                    "edit", ColorCode.EDIT.value, true,
                    "editVolunter","Editar", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId",volunter.getVolunterId().toString())
                    ))));
            contents.add(new CellContent("iconAccion",
                    "delete",ColorCode.DELETE.value, true,
                    "deleteVolunter","Eliminar", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId",volunter.getVolunterId().toString())
                    ))));
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW.value, true,
                    "seeVolunter","Ver Info", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId",volunter.getVolunterId().toString())
                    ))));
        }

        return contents;
    }


    public Volunter findOneVolunter(Long volunter_id){
        return volunterRepository.getById(volunter_id);
    }

    public Volunter saveVolunter (Volunter volunter) throws Exception{
        Volunter duplicateVol = volunterRepository.getByUsername(volunter.getUsername());
        if (duplicateVol != null){
            throw new Exception("El username ya existe");
        }
        else {
            userRepository.save(volunter.getUser());
            volunter.setStatus(Status.ACTIVO);
            volunter.setEntryDate(new Date());
        }
        return volunterRepository.save(volunter);
    }

    public Volunter updateVolunter (Volunter volunter){
        userRepository.save(volunter.getUser());
        Volunter saveVolunter = volunterRepository.getById(volunter.getVolunterId());
        saveVolunter.setRoles(volunter.getRoles());
        return volunterRepository.save(saveVolunter);
    }

    public void deleteVolunter(Long id){
        volunterRepository.deleteById(id);
    }

    public void exitVolunter(ExitPost exitPost){
        Volunter volunter=volunterRepository.getById(exitPost.getVolunteerId());
        ExitMessage exitMessage =  new ExitMessage();
        exitMessage.setMessage(exitPost.getMessage());
        exitMessage.setVolunter(volunter);
        exitMessage.setRegisterDate(new Date());
        exitMessage.setMessage_id(exitPost.getMessage_id() != null ? exitPost.getMessage_id() :  null);
        try {
            exitMessageRepository.save(exitMessage);
            volunter.setStatus(Status.INACTIVO);
            volunter.setExitDate(new Date());
            try {
                volunterRepository.save(volunter);
            } catch (Exception exc){
                System.out.println("exc " + exc);
            }
        }catch (Exception e){
            System.out.println("Exception " + e);
        }

    }

    public void activateVolunteer(ExitPost exitPost){
        Volunter volunter=volunterRepository.getById(exitPost.getVolunteerId());
        try {
            volunter.setStatus(Status.ACTIVO);
            volunterRepository.save(volunter);
        }catch (Exception e){
            System.out.println("Exception " + e);
        }

    }

    public Volunter getVolunterById(Long id){
        Volunter volunter=volunterRepository.getById(id);
        /*volunter.setStatus(Status.ACTIVO);
        volunter.setEntry_date(new Date());
        volunterRepository.save(volunter);*/
        return volunter;
    }


    public Volunter findVolunterRoles(String email) {
        System.out.println("email"+email);
        User user = userRepository.findByEmail(email).get();
        System.out.println("Se encontro persona " + user.getName() + user.getLastname() + user.getUserId());
        Optional<Volunter> volunter=volunterRepository.findById(2123123L);

        return volunter.get();
    }

    public List<ExitPost> getExitMessages(Long volunterId){
        Volunter volunter = volunterRepository.getById(volunterId);
        List<ExitMessage> exitMessageList = exitMessageRepository.findByVolunter(volunter);
        List<ExitPost> finalList = new ArrayList<>();
        for (ExitMessage exitMessage: exitMessageList){
            finalList.add(new ExitPost(exitMessage));
        }
        return finalList;
    }
}
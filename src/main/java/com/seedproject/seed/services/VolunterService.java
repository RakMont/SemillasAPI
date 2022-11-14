package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ExitMessage;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.User;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.models.enums.RoleName;
import com.seedproject.seed.models.enums.Status;
import com.seedproject.seed.models.filters.VolunterFilter;
import com.seedproject.seed.repositories.ExitMessageRepository;
import com.seedproject.seed.repositories.RoleRepository;
import com.seedproject.seed.repositories.UserRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EncripttionService encripttionService;

    @Inject
    ExitMessageRepository exitMessageRepository;

    @Inject
    RoleRepository roleRepository;
    public Table findAllVolunter(){
        List<Volunter> volunters = volunterRepository.findAll();
        volunters.forEach((volunter -> volunter.setRoles(roleRepository.getVolunterRoles(volunter.getVolunterId()))));
        Table resultTable = this.getVoluntersInformat(volunters, false);
        return resultTable;
    }

    public Table findVoluntersByFilter(VolunterFilter volunterFilter){
        List<Volunter> volunters = volunterRepository.findAll();
        if (volunterFilter!= null && volunterFilter.getStatus() != null){
            volunters.removeIf(v -> v.getStatus()!= volunterFilter.getStatus());
        } else if (volunterFilter != null && volunterFilter.getRoleId() != null) {
            volunters.removeIf(v -> !this.gotTheRol(volunterFilter.getRoleId(),v.getRoles()));
        }
        volunters.forEach((volunter -> volunter.setRoles(roleRepository.getVolunterRoles(volunter.getVolunterId()))));
        Table resultTable = this.getInactiveVoluntersInformat(volunters);
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
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))
            ));
            contents.add(new CellContent("iconAccion",
                    "group_add", ColorCode.ASSIGN_SEED.value, true,
                    "AssignSeed","Asignar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))
            ));
        }
        else {
            contents.add(new CellContent("iconAccion",
                    "edit", ColorCode.EDIT.value, true,
                    "editVolunter","Editar", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))));
            contents.add(new CellContent("iconAccion",
                    "delete",ColorCode.DELETE.value, true,
                    "deleteVolunter","Eliminar", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))));
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW.value, true,
                    "seeVolunter","Ver Info", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))));
        }

        return contents;
    }


    public Volunter findOneVolunter(String volunter_id){
        volunter_id = encripttionService.decrypt(volunter_id);
        try {
            return volunterRepository.getById(Long.parseLong(volunter_id));
        } catch (Exception e){
            throw  e;
        }
    }

    public ResponseEntity<RequestResponseMessage> saveVolunter (Volunter volunter) throws Exception{

        Volunter duplicateVol = volunterRepository.getByUsername(volunter.getUsername());
        if (duplicateVol != null){
            //throw new VolunteerFoundException("El username ya existe");
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El username ya existe", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        } else if (userRepository.getByEmail(volunter.getUser().getEmail()) != null) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El correo ya existe", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        } else if (volunter.getRoles().isEmpty()) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El voluntario debe tener al menos un rol", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        } else {
            try {
               // User user =  userRepository.save(volunter.getUser());
                volunter.setStatus(Status.ACTIVE);
                volunter.setEntryDate(new Date());
                volunter.setPassword(this.bCryptPasswordEncoder.encode(volunter.getPassword()));
                 volunterRepository.save(volunter);
                return new ResponseEntity<>(new RequestResponseMessage(
                        "El voluntario fue creado", ResponseStatus.SUCCESS),HttpStatus.CREATED);

            }catch (Exception exception){
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Error creando", ResponseStatus.SUCCESS),HttpStatus.BAD_REQUEST);
            }
        }
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

    public  ResponseEntity<RequestResponseMessage> exitVolunter(ExitPost exitPost){
        exitPost.setVolunteerId(encripttionService.decrypt(exitPost.getVolunteerId()));
        Volunter volunter=volunterRepository.getById(Long.parseLong(exitPost.getVolunteerId()));
        ExitMessage exitMessage =  new ExitMessage();
        exitMessage.setMessage(exitPost.getMessage());
        exitMessage.setVolunter(volunter);
        exitMessage.setRegisterDate(new Date());
        exitMessage.setMessage_id(exitPost.getMessage_id() != null ? exitPost.getMessage_id() :  null);
        try {
            exitMessageRepository.save(exitMessage);
            volunter.setStatus(Status.INACTIVE);
            volunter.setExitDate(new Date());
            try {
                volunterRepository.save(volunter);

                return new ResponseEntity<>(new RequestResponseMessage(
                        "El voluntario fue desactivado", ResponseStatus.SUCCESS),HttpStatus.CREATED);
            } catch (Exception exc){
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Error al desactivar al voluntario", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error al desactivar al voluntario", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }

    }

    public void activateVolunteer(ExitPost exitPost){
        exitPost.setVolunteerId(encripttionService.decrypt(exitPost.getVolunteerId()));
        Volunter volunter=volunterRepository.getById(Long.parseLong(exitPost.getVolunteerId()));
        try {
            volunter.setStatus(Status.ACTIVE);
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

    private Table getInactiveVoluntersInformat(List<Volunter> volunters){
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
                    new CellHeader("CI",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, volunter.getUser().getDni(),
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
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("iconAccion",
                                            "brightness_5", ColorCode.EDIT.value, true,
                                            "activateVolunter","Reactivar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                             new CellParam("volunterId",volunter.getVolunterId().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "delete",ColorCode.DELETE.value, true,
                                            "deleteVolunter","Eliminar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId",volunter.getVolunterId().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "remove_red_eye",ColorCode.VIEW.value, true,
                                            "seeVolunter","Ver Info", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId",volunter.getVolunterId().toString())
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
package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.*;
import com.seedproject.seed.models.filters.VolunterFilter;
import com.seedproject.seed.repositories.ExitMessageRepository;
import com.seedproject.seed.repositories.RoleRepository;
import com.seedproject.seed.repositories.UserRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class VolunterService {
    @Inject
    VolunterRepository volunterRepository;

    @Inject
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EncripttionService encripttionService;

    @Inject
    MenuService menuService;
    @Inject
    ExitMessageRepository exitMessageRepository;

    @Inject
    RoleRepository roleRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    public Table findAllVolunteer(VolunterFilter volunterFilter){
        List<Volunter> volunteers = volunterRepository.findAll();
        volunteers.removeIf(v -> !v.getRegister_exist());
        if (volunterFilter!= null && volunterFilter.getStatus() != null){
            volunteers.removeIf(v -> v.getStatus()!= volunterFilter.getStatus());
        }
        volunteers.forEach((volunteer -> volunteer.setRoles(roleRepository.getVolunterRoles(volunteer.getVolunterId()))));

        return this.getVolunteersInFormat(volunteers);
    }

    public List<ComboVolunteer> findComboTrackingVolunteers(){
        List<Volunter> volunteers = volunterRepository.findAll();
        volunteers.removeIf(p -> !(menuService.hasRole( p.getRoles(), RoleName.R_REGISTROS)));
        List<ComboVolunteer> comboVolunteers= new ArrayList<>();
        for (Volunter volunteer:volunteers){
            comboVolunteers.add(new ComboVolunteer(
                    encripttionService.encrypt( volunteer.getVolunterId().toString())
                    ,volunteer));
        }
        return comboVolunteers;
    }

   /* public Table findVoluntersByFilter(VolunterFilter volunterFilter){
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
*/
    public Table findAllTrackingVolunteers(){
        List<Volunter> volunters =  volunterRepository.findByOrderByUsernameAsc();
        volunters.forEach((volunter -> volunter.setRoles(roleRepository.getVolunterRoles(volunter.getVolunterId()))));
        volunters.removeIf(v -> !this.gotTheRol(RoleName.R_SEGUIMIENTOS,v.getRoles()));
        volunters.removeIf(v -> !v.getStatus().equals(Status.ACTIVE));
        return this.getTrackingVolunteersInFormat(volunters);
    }
    public boolean gotTheRol(RoleName roleName, List<Role> roles){
        //roles.removeIf(r -> !(r.getRole_name().equals(roleName)));
        //return roles.size() > 0;
        Role res = roles.stream().filter( r-> roleName.equals(r.getRole_name())).findAny().orElse(null);
        return res != null;
    }
    private Table getVolunteersInFormat(List<Volunter> volunteers){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
       // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (Volunter volunter: volunteers){
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
                    new ArrayList<CellContent>(this.getVolunteerRoles(volunter.getRoles()))
            ));
            cells.add(new Cell(
                    new CellHeader("Usuario",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, volunter.getUsername(),
                                            null),
                                    new CellContent("iconAccion",
                                            "rate_review", ColorCode.EDIT_PASSWORD.value, true,
                                            "updatePassword","Cambiar contraseña", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                                            ))
                                    )
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList( volunter.getStatus().equals(Status.ACTIVE) ?
                                    new CellContent(
                                            "chipContent",
                                            null, ColorCode.STATE_ACEPTED.value,false,
                                            null,null, "Activo",
                                            null
                                    )
                                    : new CellContent(
                                            "chipContent",
                                            null, ColorCode.STATE_REJECTED.value,false,
                                            null,null, "Inactivo",
                                            null
                                    )
                            )
                    )
                    /*new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            volunter.getEntryDate() != null ?
                                                    formatter.format(volunter.getEntryDate()) : " ",
                                            null)
                            )
                    )*/
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedActions(volunter,false))
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private List<CellContent> getVolunteerRoles(List<Role> roles){
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
            /*contents.add(new CellContent("iconAccion",
                    "group_add", ColorCode.ASSIGN_SEED.value, true,
                    "AssignSeed","Asignar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                    ))
            ));*/
        }
        else {
            if (volunter.getStatus().equals(Status.ACTIVE)){
                contents.add(new CellContent("iconAccion",
                        "edit", ColorCode.EDIT.value, true,
                        "editVolunter","Editar", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("volunterId", encripttionService.encrypt(volunter.getVolunterId().toString()))
                        ))));
                contents.add(new CellContent("iconAccion",
                        "clear",ColorCode.DELETE.value, true,
                        "inactiveVolunter","Desactivar", null,
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
            else {
                contents.add(new CellContent("iconAccion",
                        "how_to_reg", ColorCode.EDIT.value, true,
                        "activateVolunter","Reactivar", null,
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
        }

        return contents;
    }


    public VolunterDTO findOneVolunteer(String volunter_id, Principal principal){
        if (!volunter_id.isEmpty()){
            volunter_id = encripttionService.decrypt(volunter_id);
            try {
                VolunterDTO volunterDTO = new VolunterDTO(volunterRepository.getById(Long.parseLong(volunter_id)));
                volunterDTO.getRoles().forEach(role -> role.setName(role.getRole_name().value));
                return volunterDTO;
            } catch (Exception exception){
                throw new RuntimeException(exception);
            }
        }
        else{
            try{
                VolunterDTO volunteerDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
                volunteerDTO.getRoles().forEach(role -> role.setName(role.getRole_name().value));
                return volunteerDTO;
            } catch (UsernameNotFoundException e) {
                throw new RuntimeException(e);
            }
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
                volunter.setRegister_exist(true);
                volunter.setEntryDate(new Date());
                volunter.setPassword(this.bCryptPasswordEncoder.encode(volunter.getPassword()));
                 volunterRepository.save(volunter);
                return new ResponseEntity<>(new RequestResponseMessage(
                        "El voluntario fue creado", ResponseStatus.SUCCESS),HttpStatus.CREATED);

            }catch (Exception exception){
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Error creando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }
    }

    public ResponseEntity<RequestResponseMessage> updateVolunter (Volunter volunter) throws Exception{
        if (volunter.getRoles().isEmpty()) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El voluntario debe tener al menos un rol", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        } else {
            try {
                userRepository.save(volunter.getUser());
                Volunter saveVolunter = volunterRepository.getById(volunter.getVolunterId());
                saveVolunter.setRoles(volunter.getRoles());
                volunterRepository.save(saveVolunter);
                return new ResponseEntity<>(new RequestResponseMessage(
                        "El voluntario fue actualizado", ResponseStatus.SUCCESS),HttpStatus.CREATED);

            }catch (Exception exception){
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Error actualizando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }

    }

    public ResponseEntity<RequestResponseMessage> deleteVolunter(String id){
        id = encripttionService.decrypt(id);
        try {
            volunterRepository.setRegisterFalse(Long.parseLong(id));
            //volunterRepository.deleteById(Long.parseLong(id));
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El voluntario fue eliminado", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception exception){
            //throw exception;
            return new ResponseEntity<>(new RequestResponseMessage(
                    "rror", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public  ResponseEntity<RequestResponseMessage> exitVolunteer(ExitPost exitPost){
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

    public ResponseEntity<RequestResponseMessage>  activateVolunteer(ExitPost exitPost){
        exitPost.setVolunteerId(encripttionService.decrypt(exitPost.getVolunteerId()));
        Volunter volunter=volunterRepository.getById(Long.parseLong(exitPost.getVolunteerId()));
        try {
            volunter.setStatus(Status.ACTIVE);
            volunterRepository.save(volunter);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El voluntario fue reactivado", ResponseStatus.SUCCESS),HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error al reactivar al voluntario", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

        }

    }

    public Volunter getVolunterById(Long id){
       try{
           return volunterRepository.getById(id);
       }catch (Exception e){
           throw new RuntimeException(e.getMessage());
       }
    }


    public Volunter findVolunterRoles(String email) {
        System.out.println("email"+email);
        User user = userRepository.findByEmail(email).get();
        System.out.println("Se encontro persona " + user.getName() + user.getLastname() + user.getUserId());
        Optional<Volunter> volunter=volunterRepository.findById(2123123L);

        return volunter.get();
    }

    public List<ExitPost> getExitMessages(String id){
        String volunter_id = encripttionService.decrypt(id);
        Volunter volunter = volunterRepository.getById(Long.parseLong(volunter_id));
        List<ExitMessage> exitMessageList = exitMessageRepository.findByVolunter(volunter);
        List<ExitPost> finalList = new ArrayList<>();
        for (ExitMessage exitMessage: exitMessageList){
            finalList.add(new ExitPost(exitMessage));
        }
        return finalList;
    }

    private Table getTrackingVolunteersInFormat(List<Volunter> volunteers){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (Volunter volunter: volunteers){
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
                    new CellHeader("Celular",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, volunter.getUser().getPhone(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsabilidades",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getVolunteerRoles(volunter.getRoles()))
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    this.getSeedActions(volunter, true)
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    public ResponseEntity<RequestResponseMessage> updateVolunteerPassword(Principal principal,VolunteerPasswordDTO volunteerPasswordDTO){
        try{
            VolunterDTO volunteerDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
            if(volunteerDTO.getRoles().stream().anyMatch(rol->rol.getRole_name().equals(RoleName.R_PRINCIPAL))){
                Long id = Long.parseLong(encripttionService.decrypt(volunteerPasswordDTO.getUpdatedVolunteerId()));
                volunterRepository.setNewPassword(id,this.bCryptPasswordEncoder.encode(volunteerPasswordDTO.getUpdatedVolunteerPassword()));
                return new ResponseEntity<>(new RequestResponseMessage(
                        "La contraseña fue actualizada", ResponseStatus.SUCCESS),HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(new RequestResponseMessage(
                        "Usted no tiene la autorización para este proceso", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error actualizando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public List<Volunter> getVolunteerEmails(){
        List<String> emailsList = new ArrayList<>();
        List<Volunter> list = volunterRepository.findAll();
        list.forEach(a->{
            emailsList.add(a.getUser().getEmail());
        });
        return list;
    }
}
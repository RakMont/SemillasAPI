package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.DesMenu;
import com.seedproject.seed.models.dto.MenuItem;
import com.seedproject.seed.models.dto.VolunterDTO;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MenuService {
    @Autowired
    UserDetailsServiceImpl userDetailsService;


    public Object getMenu(Principal principal){
        try{
            VolunterDTO volunteerDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
            return this.getMenuByRoles(volunteerDTO.getRoles());
        }catch (Exception e){
            return null;
        }


        /*if (this.hasRole(volunteerDTO.getRoles(), RoleName.R_PRINCIPAL)){
            return this.getPrincipalMenu();
        }
        else return null;*/
    }


    List<Object> getMenuByRoles(List<Role> roles){
        List<Object> principalMenu = new ArrayList<>();
        if(hasRole(roles,RoleName.R_PRINCIPAL ) || hasRole(roles,RoleName.R_REGISTROS ) ){
            DesMenu desMenu = new DesMenu("Semillas", "wc");
            List<MenuItem> menuItemList = new ArrayList<>();
            if (hasRole(roles,RoleName.R_REGISTROS ))
                menuItemList.add(new MenuItem("Gestionar Aplicantes", "supervised_user_circle", "admin/aplicantes"));
            if (hasRole(roles,RoleName.R_PRINCIPAL ))
                menuItemList.add(new MenuItem("Gestionar Semillas", "how_to_reg", "admin/semillas"));
            if (hasRole(roles,RoleName.R_REGISTROS ))
                menuItemList.add(new MenuItem("Registrar Semilla","person_add","/admin/new-seed"));

            desMenu.setChildren(menuItemList);
            principalMenu.add(desMenu);
            if (hasRole(roles,RoleName.R_PRINCIPAL )){
                DesMenu respMenu = new DesMenu("Responsables", "supervised_user_circle");
                respMenu.setChildren(Arrays.asList(
                        new MenuItem("Responsables", "group", "admin/ver-voluntarios"))
                );
                principalMenu.add(respMenu);
            }
        }
        if (hasRole(roles,RoleName.R_PRINCIPAL ) || hasRole(roles,RoleName.R_SEGUIMIENTOS )){
            DesMenu segMenu = new DesMenu("Seguimiento", "track_changes");
            List<MenuItem> menuItemList = new ArrayList<>();
            if (hasRole(roles,RoleName.R_PRINCIPAL ))
                menuItemList.add(new MenuItem("Responsables de seguimiento", "supervisor_account", "/admin/tracking"));
            if (hasRole(roles,RoleName.R_SEGUIMIENTOS )) {
                menuItemList.add(new MenuItem("Semillas asignadas", "supervised_user_circle", "/admin/tracking/volunteer-seeds"));
                menuItemList.add(new MenuItem("Recordatorios", "contact_mail", "/admin/tracking/reminder-emails"));

            }
            menuItemList.add(new MenuItem("Aportes recibidos", "layers", "/admin/tracking/donations"));
            segMenu.setChildren(menuItemList);
            principalMenu.add(segMenu);

            if (hasRole(roles,RoleName.R_PRINCIPAL )){
                DesMenu adminMenu = new DesMenu("Administración", "inventory_2");
                adminMenu.setChildren(Arrays.asList(
                        new MenuItem("Souvenirs", "card_giftcard", "/admin/souvenirs/benefited-seeds"),
                        new MenuItem("Actividades", "local_activity", "/admin/activities/manage-activities"))
                );
                principalMenu.add(adminMenu);
            }
        }


        return principalMenu;
    }

    List<Object> getPrincipalMenu(){
        List<Object> principalMenu = new ArrayList<>();
        DesMenu desMenu = new DesMenu("Semillas", "wc");
        desMenu.setChildren(Arrays.asList(
                new MenuItem("Gestionar Aplicantes", "supervised_user_circle", "admin/aplicantes"),
                new MenuItem("Gestionar Semillas", "how_to_reg", "admin/semillas"),
                new MenuItem("Registrar Semilla","person_add","/admin/new-seed"))
        );
        principalMenu.add(desMenu);



        DesMenu respMenu = new DesMenu("Responsables", "supervised_user_circle");
        respMenu.setChildren(Arrays.asList(
                new MenuItem("Responsables", "group", "admin/ver-voluntarios"))
        );
        principalMenu.add(respMenu);


        DesMenu segMenu = new DesMenu("Seguimiento", "track_changes");
        segMenu.setChildren(Arrays.asList(
                new MenuItem("Responsables de seguimiento", "supervisor_account", "/admin/tracking"),
                new MenuItem("Semillas asignadas", "supervised_user_circle", "/admin/tracking/volunteer-seeds"),
                new MenuItem("Aportes recibidos", "layers", "/admin/tracking/donations"),
                new MenuItem("Recordatorios", "contact_mail", "/admin/tracking/reminder-emails"))
        );
        principalMenu.add(segMenu);

        DesMenu adminMenu = new DesMenu("Administración", "inventory_2");
        adminMenu.setChildren(Arrays.asList(
                new MenuItem("Souvenirs", "card_giftcard", "/admin/souvenirs/benefited-seeds"),
                new MenuItem("Actividades", "local_activity", "/admin/activities/manage-activities"))
        );
        principalMenu.add(adminMenu);

        return principalMenu;
    }

    boolean hasRole(List<Role> roles, RoleName verifyRol){
        return roles.stream().anyMatch(rol->rol.getRole_name().equals(verifyRol));
    }
}

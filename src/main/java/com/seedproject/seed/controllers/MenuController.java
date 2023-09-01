package com.seedproject.seed.controllers;

import com.seedproject.seed.services.MenuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/menu")
public class MenuController {

    @Inject
    MenuService menuService;

    @GetMapping(path = {"/getVolunteerMenu"})
    public Object getVolunteerMenu(Principal principal){
        return this.menuService.getMenu(principal);
    }

}

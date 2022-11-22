package com.seedproject.seed.controllers;

import com.seedproject.seed.security.JwtUtil;
import com.seedproject.seed.exceptions.VolunteerNotFoundException;
import com.seedproject.seed.models.dto.JwtRequest;
import com.seedproject.seed.models.dto.JwtResponse;
import com.seedproject.seed.models.dto.VolunterDTO;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.services.RoleService;
import com.seedproject.seed.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/oauth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OauthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Inject
    RoleService roleService;
    @GetMapping(path = {"/roles"})
    public List<Role> findAllApplicants(){
        return roleService.getRoles();
    }

    @PostMapping(path = {"/generate-token"})
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        System.out.println("hkjhk"+ jwtRequest);
        try {
            authenticate(jwtRequest.getUsername(),jwtRequest.getPassword());
        }catch (VolunteerNotFoundException volunteerNotFoundException) {
            volunteerNotFoundException.printStackTrace();
            throw new Exception("usuario no encontrado");
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        }catch (DisabledException disabledException){
            throw new Exception("USUARIO DESHABILITADO" + disabledException.getMessage());
        }catch (BadCredentialsException badCredentialsException){
            throw new Exception("credenciales invalidas" + badCredentialsException.getMessage());
        }
    }
    @GetMapping("/actual-usuario")
    public VolunterDTO getCurrentVolunter(Principal principal){
        VolunterDTO volunterDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
        return volunterDTO;
    }
}

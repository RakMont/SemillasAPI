package com.seedproject.seed.validators;

import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.entities.User;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.repositories.UserRepository;
import com.seedproject.seed.repositories.VolunterRepository;

import javax.inject.Inject;

public class VolunterValidator {

    @Inject
    UserRepository userRepository;

    @Inject
    VolunterRepository volunterRepository;

    public RequestResponseMessage validateCreate(Volunter volunter){
        RequestResponseMessage requestResponseMessage = new RequestResponseMessage();
        Volunter duplicateVol = volunterRepository.getByUsername(volunter.getUsername());
        if (volunterRepository.getByUsername(volunter.getUsername()) != null){
            requestResponseMessage.setMessage("El username ya existe");
            requestResponseMessage.setStatus(ResponseStatus.ERROR);
        } else if (userRepository.getByEmail(volunter.getUser().getEmail()) != null) {
            requestResponseMessage.setMessage("El correo ya existe");
            requestResponseMessage.setStatus(ResponseStatus.ERROR);
        } else if (volunter.getRoles().isEmpty()) {
            requestResponseMessage.setMessage("El voluntario debe tener al menos un rol");
            requestResponseMessage.setStatus(ResponseStatus.ERROR);
        } else {
            requestResponseMessage = null;
        }
        return  requestResponseMessage;
    }
}

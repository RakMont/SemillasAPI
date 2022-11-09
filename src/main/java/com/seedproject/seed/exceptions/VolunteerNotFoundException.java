package com.seedproject.seed.exceptions;

public class VolunteerNotFoundException extends Exception{
    public VolunteerNotFoundException(){
        super("El voluntario no existe en la base de datos");
    }

    public VolunteerNotFoundException(String message){
        super(message);
    }
}

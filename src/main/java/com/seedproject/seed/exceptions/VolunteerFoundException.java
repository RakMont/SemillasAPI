package com.seedproject.seed.exceptions;

public class VolunteerFoundException extends Exception {
    public VolunteerFoundException(){
        super("El voluntario ya existe");
    }

    public VolunteerFoundException(String message){
        super(message);
    }
}

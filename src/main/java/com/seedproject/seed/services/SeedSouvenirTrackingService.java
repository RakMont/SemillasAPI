package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.SeedSouvenirTrackingDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.SeedSouvenirTracking;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.models.enums.TrackingStatus;
import com.seedproject.seed.models.filters.SouvenirTrackingFilter;
import com.seedproject.seed.repositories.ContributorRepository;
import com.seedproject.seed.repositories.SeedSouvenirTrackingRepository;
import com.seedproject.seed.repositories.UserRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SeedSouvenirTrackingService {
    @Inject
    VolunterRepository volunterRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    SeedSouvenirTrackingRepository seedSouvenirTrackingRepository;

    @Inject
    ContributorRepository contributorRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EncripttionService encripttionService;

    public Table findAllSouvenirsTracking(SouvenirTrackingFilter souvenirTrackingFilter){
        try{
            List<SeedSouvenirTracking> seedSouvenirTrackingList = seedSouvenirTrackingRepository.findAll();
            return this.getSouvenirTrackingStatus(seedSouvenirTrackingList);
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private Table getSouvenirTrackingStatus(List<SeedSouvenirTracking> seedSouvenirTrackingList) {
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (SeedSouvenirTracking seedSouvenirTracking: seedSouvenirTrackingList){
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
                    new CellHeader("Semilla beneficiada",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            seedSouvenirTracking.getBenefitedContributor().getSeedFullName(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Ciudad envío",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, seedSouvenirTracking.getChosenCity(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable a cargo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, seedSouvenirTracking.getVolunteerInCharge().getFullName(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado de envío",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList( seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_DELIVERED) ?
                                   new CellContent("chipContent",
                                                    null, ColorCode.SOUVENIR_DELIVERED.value,false,
                                                    null,null, "Entregado",
                                                    null
                                            )
                                            :  seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_PENDING) ?
                                    new CellContent(
                                            "chipContent",
                                            null, ColorCode.SOUVENIR_PENDING.value,false,
                                            null,null, "Pendiente",
                                            null
                                    ) :new CellContent(
                                            "chipContent",
                                            null, ColorCode.SOUVENIR_SENT.value,false,
                                            null,null, "Enviado",
                                            null
                                    )
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha envío",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, formatter.format(seedSouvenirTracking.getSouvenirSendDate()),
                                            null)
                            )
                    )
            ));

            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedActions(seedSouvenirTracking))
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }

    private List<CellContent> getSeedActions(SeedSouvenirTracking seedSouvenirTracking) {
        List<CellContent> contents = new ArrayList<>();
        contents.add(
                new CellContent("iconAccion", "description",
                        ColorCode.VIEW_CONTR.value, true,
                        "ViewSouvenirTracking","Ver Detalle", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                        encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));

        contents.add(
                new CellContent("iconAccion", "edit",
                        ColorCode.EDIT.value, true,
                        "EditSouvenirTracking","Editar", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                                encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));
        contents.add(
                new CellContent("iconAccion", "delete",
                        ColorCode.STATE_REJECTED.value, true,
                        "deleteSouvenirTracking","Eliminar", null,
                        new ArrayList<CellParam>(Arrays.asList(new CellParam("seedSouvenirTrackingId",
                                encripttionService.encrypt(seedSouvenirTracking.getSeedSouvenirTrackingId().toString()))))));



        return contents;
    }

    public ResponseEntity<RequestResponseMessage> createSeedSouvenirTracking(Principal principal, SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        try{
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getBenefitedContributorId())));
            Optional<Volunter> volunteerInCharge = volunterRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getVolunteerInCharge())));
            SeedSouvenirTracking seedSouvenirTracking = new SeedSouvenirTracking();
            seedSouvenirTracking.setSelectedDate(seedSouvenirTrackingDao.getSelectedDate());
            seedSouvenirTracking.setSouvenirSendDate(seedSouvenirTrackingDao.getSouvenirSendDate());
            seedSouvenirTracking.setTrackingStatus(seedSouvenirTrackingDao.getTrackingStatus());
            seedSouvenirTracking.setSpentAmount(seedSouvenirTrackingDao.getSpentAmount());
            seedSouvenirTracking.setChosenCity(seedSouvenirTrackingDao.getChosenCity());
            seedSouvenirTracking.setObservation(seedSouvenirTrackingDao.getObservation());
            seedSouvenirTracking.setRegister_exist(true);
            seedSouvenirTracking.setBenefitedContributor(contributor.get());
            seedSouvenirTracking.setVolunteerInCharge(volunteerInCharge.get());
            seedSouvenirTrackingRepository.save(seedSouvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Los datos se guardaron correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
            //throw new RuntimeException(e);

        }
    }

    public ResponseEntity<RequestResponseMessage> updateSeedSouvenirTracking(Principal principal, SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        try{
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getBenefitedContributorId())));
            Optional<Volunter> volunteerInCharge = volunterRepository.findById(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getVolunteerInCharge())));
            SeedSouvenirTracking seedSouvenirTracking = new SeedSouvenirTracking();
            seedSouvenirTracking.setSeedSouvenirTrackingId(Long.parseLong(encripttionService.decrypt(seedSouvenirTrackingDao.getSeedSouvenirTrackingId())));
            seedSouvenirTracking.setSelectedDate(seedSouvenirTrackingDao.getSelectedDate());
            seedSouvenirTracking.setSouvenirSendDate(seedSouvenirTrackingDao.getSouvenirSendDate());
            seedSouvenirTracking.setTrackingStatus(seedSouvenirTrackingDao.getTrackingStatus());
            seedSouvenirTracking.setSpentAmount(seedSouvenirTrackingDao.getSpentAmount());
            seedSouvenirTracking.setChosenCity(seedSouvenirTrackingDao.getChosenCity());
            seedSouvenirTracking.setObservation(seedSouvenirTrackingDao.getObservation());
            seedSouvenirTracking.setRegister_exist(true);
            seedSouvenirTracking.setBenefitedContributor(contributor.get());
            seedSouvenirTracking.setVolunteerInCharge(volunteerInCharge.get());
            seedSouvenirTrackingRepository.save(seedSouvenirTracking);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Los datos se guardaron correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
            //throw new RuntimeException(e);

        }
    }

    public ResponseEntity<RequestResponseMessage> deleteSeedSouvenirTracking(String id) {
        try{
            Optional<SeedSouvenirTracking> seedSouvenirTracking = seedSouvenirTrackingRepository.findById(Long.parseLong(encripttionService.decrypt(id)));
            seedSouvenirTracking.get().setRegister_exist(false);
            seedSouvenirTrackingRepository.save(seedSouvenirTracking.get());
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Se elimino Correctamente", ResponseStatus.SUCCESS),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error eliminando los datos, por favor intentelo de nuevo",
                    ResponseStatus.ERROR), HttpStatus.BAD_REQUEST);
            //throw new RuntimeException(e);

        }
    }

    public SeedSouvenirTrackingDTO getSeedSouvenirTracking(String id) {
        try{
            Optional<SeedSouvenirTracking> seedSouvenirTracking = seedSouvenirTrackingRepository.findById(Long.parseLong(encripttionService.decrypt(id)));
            seedSouvenirTracking.get().setRegister_exist(false);
            seedSouvenirTrackingRepository.save(seedSouvenirTracking.get());
            SeedSouvenirTrackingDTO seedSouvenirTrackingDTO = new SeedSouvenirTrackingDTO(seedSouvenirTracking.get());
            seedSouvenirTrackingDTO.setSeedSouvenirTrackingId(encripttionService.encrypt(seedSouvenirTrackingDTO.getSeedSouvenirTrackingId()));
            seedSouvenirTrackingDTO.setBenefitedContributorId(encripttionService.encrypt(seedSouvenirTrackingDTO.getBenefitedContributorId()));
            seedSouvenirTrackingDTO.setVolunteerInChargeId(encripttionService.encrypt(seedSouvenirTrackingDTO.getVolunteerInChargeId()));
            return seedSouvenirTrackingDTO;
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}

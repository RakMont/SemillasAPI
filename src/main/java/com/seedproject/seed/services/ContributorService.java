package com.seedproject.seed.services;


import com.seedproject.seed.models.dao.UniqueAplicantHolderDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.*;
import com.seedproject.seed.models.filters.SeedFilter;
import com.seedproject.seed.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;
import java.util.*;

@Service
public class ContributorService {
    @Inject
    ContributorRepository contributorRepository;
    @Inject
    RejectedApplicantRepository rejectedApplicantRepository;
    @Inject
    ProcessedContributorRepository processedContributorRepository;
    @Inject
    ContributionConfigService contributionConfigService;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    VolunterService volunterService;
    @Inject
    ConstantContributionRepository constantContributionRepository;
    @Inject
    EncripttionService encripttionService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public ResponseEntity<RequestResponseMessage> saveConstantContributor(
            Principal principal,ConstantApplicantHolder constantApplicantHolder){

        Volunter volunteer;
        ContributionConfig contributionConfig;
        ConstantContribution constantContribution = new ConstantContribution();
        constantContribution.setStart_month(constantApplicantHolder.getBeginMonth());
        constantContribution.setPaymentDate(PaymentDate.CADA_10_DEL_MES);
        constantContribution.setRemainderType(constantApplicantHolder.getReminderMethod());
        constantContribution.setContribution(new Contribution(
                constantApplicantHolder.getContribution_amount(),
                constantApplicantHolder.getPaymentMethod(),
                constantApplicantHolder.getSend_news(),
                constantApplicantHolder.getSendNewsType()
        ));
        contributionConfig = contributionConfigService.saveConstantContributionConfig(constantContribution);
        Contributor contributor = constantApplicantHolder.getContributor();
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.setContributionConfig(contributionConfig);
        contributor.setRegister_exist(true);
        if (principal != null ){
            return this.saveConstantContributionSeed(principal,contributor);
        }else{
            volunteer = volunterRepository.getById(Long.parseLong("1"));
            contributor.setContributorState(ContributorState.PENDING.value);
            contributor.setRegisterVolunter(volunteer);

            try {
                Contributor resp = contributorRepository.save(contributor);
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Sus datos fueron enviados, estan pendientes de revision." +
                                        "Un voluntario del programa se pondrá en contacto con usted.",
                                ResponseStatus.SUCCESS), HttpStatus.CREATED);
            }
            catch(Exception e) {
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Ocurrió un error enviando sus datos, porfavor intente mas tarde.",
                                ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }
    }

    private ResponseEntity<RequestResponseMessage> saveConstantContributionSeed(Principal principal,Contributor contributor){
        Volunter volunteer = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
        contributor.setRegisterVolunter(volunteer);
        contributor.setContributorState(ContributorState.ACCEPTED.value);
        try{
            Contributor contributorResp = contributorRepository.save(contributor);
            ProcessedContributor processedContributor = new ProcessedContributor();
            processedContributor.setProcessed_date(new Date());
            processedContributor.setContributor(contributorResp);
            processedContributor.setProcess_reason("SEMILLA INGRESADA DE MANERA DIRECTA");
            processedContributor.setProcess_volunter(volunteer);

            ConstantContribution constantContribution = contributorResp.getContributionConfig().getConstantContribution();
            constantContribution.setContributionStartDate(new Date());
            constantContribution.setContributionEndDate(new Date());
            constantContribution.getContributionEndDate().setYear(constantContribution.getContributionEndDate().getYear() + 1);
            constantContributionRepository.save(constantContribution);
            processedContributorRepository.save(processedContributor);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La semilla fue registrada", ResponseStatus.SUCCESS),HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando a la semilla", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<RequestResponseMessage> savUniqueContributor(
            Principal principal, UniqueAplicantHolderDao uniqueAplicantHolderDao){
        Volunter volunteer;
        ContributionConfig contributionConfig;
        UniqueContribution uniqueContribution = new UniqueContribution();
        uniqueContribution.setDate_contribution(uniqueAplicantHolderDao.getDate_contribution());
        uniqueContribution.setContribution(new Contribution(
                uniqueAplicantHolderDao.getContribution_amount(),
                uniqueAplicantHolderDao.getPaymentMethod(),
                uniqueAplicantHolderDao.getSend_news(),
                uniqueAplicantHolderDao.getSendNewsType()
        ));
        contributionConfig=contributionConfigService.saveUniqueContributionConfig(uniqueContribution);
        Contributor contributor = uniqueAplicantHolderDao.getContributor();
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.setContributionConfig(contributionConfig);
        contributor.setRegister_exist(true);
        if (principal != null ){
            return this.saveUniqueContributionSeed(principal,contributor);
        }
        else{
            volunteer = volunterRepository.getById(Long.parseLong("1"));
            contributor.setContributorState(ContributorState.PENDING.value);
            contributor.setRegisterVolunter(volunteer);
            try {
                Contributor resp = contributorRepository.save(contributor);
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Sus datos fueron enviados, estan pendientes de revision.\" +\n" +
                                        "                                    \"Un voluntario del programa se pondrá en contacto con usted.",
                                ResponseStatus.SUCCESS), HttpStatus.CREATED);
            }
            catch(Exception e) {
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Ocurrió un error enviando sus datos, porfavor intente mas tarde.",
                                ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }

    }

    private ResponseEntity<RequestResponseMessage> saveUniqueContributionSeed(Principal principal,Contributor contributor){
        Volunter volunteer = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName()) ;
        contributor.setRegisterVolunter(volunteer);
        contributor.setContributorState(ContributorState.ACCEPTED.value);
        try{
            Contributor contributorReg = contributorRepository.save(contributor);
            ProcessedContributor processedContributor = new ProcessedContributor();
            processedContributor.setProcessed_date(new Date());
            processedContributor.setContributor(contributorReg);
            processedContributor.setProcess_reason("SEMILLA INGRESADA DE MANERA DIRECTA");
            processedContributor.setProcess_volunter(volunteer);
            processedContributorRepository.save(processedContributor);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "La semilla fue registrada", ResponseStatus.SUCCESS),HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando a la semilla", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }


    public Contributor rejectApplicant(Long applicant_id, String reason){
        Contributor contributor = contributorRepository.getById(applicant_id);
        DeactivatedContributor deactivatedContributor =new DeactivatedContributor();
        //deactivatedContributor.setReject_reason(reason);
        //deactivatedContributor.setReject_date(new Date());
        deactivatedContributor.setContributor(contributor);
        rejectedApplicantRepository.save(deactivatedContributor);
        //contributor.setContributorState(ContributorState.RECHAZADO);
        System.out.println("an" + ContributorState.REJECTED);
        return contributorRepository.save(contributor);
    }

    public ResponseEntity<RequestResponseMessage> acceptApplicant(ProcessSeedDTO processSeedDTO) {
        processSeedDTO.setContributor_id(encripttionService.decrypt(processSeedDTO.getContributor_id()));
        processSeedDTO.setProcessVolunterId(encripttionService.decrypt(processSeedDTO.getProcessVolunterId()));
        Contributor contributor = contributorRepository.findById(Long.parseLong(processSeedDTO.getContributor_id())).get();

        contributor.setContributorState(processSeedDTO.getState());
        ProcessedContributor processedContributor = new ProcessedContributor();
        processedContributor.setProcessed_date(new Date());
        processedContributor.setContributor(contributor);
        processedContributor.setProcess_reason(processSeedDTO.getProcess_reason());
        processedContributor.setProcess_volunter(
                volunterService.getVolunterById(Long.parseLong(processSeedDTO.getProcessVolunterId()))
        );
        if (contributor.getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)){
            ConstantContribution constantContribution = contributor.getContributionConfig().getConstantContribution();
            constantContribution.setContributionStartDate(processSeedDTO.getContributionStartDate());
            constantContribution.setContributionEndDate(processSeedDTO.getContributionEndDate());
            constantContributionRepository.save(constantContribution);
        }
        ResponseMessage response;
       try {
           processedContributorRepository.save(processedContributor);
           contributorRepository.save(contributor);
           return new ResponseEntity<>(new RequestResponseMessage(
                   "La semilla fue procesada", ResponseStatus.SUCCESS),HttpStatus.CREATED);
       }catch (Exception exception){
           return new ResponseEntity<>(new RequestResponseMessage(
                   "Error creando", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
       }
    }

    public List<ContributorDTO> findAcceptedApplicants(){
        List<ContributorDTO> contributorDTOS=new ArrayList<>();
        List<ProcessedContributor>contributors= processedContributorRepository.findAll();
        for (ProcessedContributor contributor:contributors){
            ContributorDTO rejectedApplicantDTO=new ContributorDTO(contributor.getContributor());
            contributorDTOS.add(rejectedApplicantDTO);
        }
        return contributorDTOS;
    }
    public Table findRejectedSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.REJECTED.value));
        Table resultTable = this.getContributtorsInFormat(contributors, false, false);
        return resultTable;
    }

    public Table findAllPendingSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.PENDING.value));
        Table resultTable = this.getContributtorsInFormat(contributors, false, false);
        return resultTable;
    }

    public Table findAceptedSeeds(SeedFilter volunteerFilter){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == volunteerFilter.status.value));
        contributors.removeIf(p -> !(p.getRegister_exist()));

        Table resultTable = this.getContributtorsInFormat(contributors,false, false);
        return resultTable;
    }

    private Table getContributtorsInFormat(List<Contributor> contributors, Boolean isTracking, Boolean isApplicantView){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        for (Contributor contributor: contributors){
            List<Cell> cells = new ArrayList<Cell>();
            cells.add(new Cell(
                    new CellHeader("#",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,String.valueOf(index),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Semilla",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributor.getUser().getName() + ' '+contributor.getUser().getLastname(),
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
                                            null,null, contributor.getUser().getPhone(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedStatus(contributor))
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable Registro",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, "SISTEMA",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tipo aporte",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null,
                                            contributor.getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value : ColorCode.UNIQUE_CONTRIBUTION.value, false,
                                            null,null,
                                            contributor.getContributionConfig().getContribution_key().toString(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedActions(contributor, isTracking,isApplicantView)
                            /*Arrays.asList(
                                    new CellContent("iconAccion",
                                            "offline_pin", ColorCode.ACCEPT_CONTR.value, true,
                                            "AceptSeed","Aceptar semilla", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",contributor.getContributor_id().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "highlight_off",ColorCode.REJECT_CONTR.value, true,
                                            "RejectSeed","Rechazar semilla", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",contributor.getContributor_id().toString())
                                            ))),
                                    new CellContent("iconAccion",
                                            "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                                            "SeedInfo","Ver información", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",contributor.getContributor_id().toString())
                                            )))
                            )*/
                    )
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }
    private List<CellContent> getSeedStatus(Contributor contributor){
        List<CellContent> contents = new ArrayList<>();
        if (contributor.getContributorState() == ContributorState.ACCEPTED.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_ACEPTED.value, false,
                    null,null, "Aceptado",
                    null
            ));
        } else if (contributor.getContributorState() == ContributorState.PENDING.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PENDING.value, false,
                    null,null, "Pendiente",
                    null
            ));
        } else if (contributor.getContributorState() == ContributorState.REJECTED.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Rechazado",
                    null
            ));
        }
        return contents;
    }

    private List<CellContent> getSeedActions(Contributor contributor, Boolean isTracking, Boolean isApplicantView){
        List<CellContent> contents = new ArrayList<>();
        if (isTracking){
            contents.add(new CellContent("iconAccion",
                    "library_books", ColorCode.EDIT.value, true,
                    "Donations","Seguimiento de aportes", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))
            ));
        }
        else if ((contributor.getContributorState() == ContributorState.ACCEPTED.value)){
            if (isApplicantView){
                contents.add(new CellContent("iconAccion",
                        "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                        "SeedInfo","Ver información", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributorId",
                                        encripttionService.encrypt(contributor.getContributor_id().toString()))
                        )
                        )));
            }else {
                contents.add(new CellContent("iconAccion",
                        "edit", ColorCode.EDIT.value, true,
                        "EditContr","Editar Datos", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributorId",
                                        encripttionService.encrypt(contributor.getContributor_id().toString()))
                        ))
                ));
                contents.add(new CellContent("iconAccion",
                        "voice_over_off", ColorCode.DELETE.value, true,
                        "Unactive","Desactivar", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributorId",
                                        encripttionService.encrypt(contributor.getContributor_id().toString()))
                        ))
                ));
                contents.add(new CellContent("iconAccion",
                        "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                        "SeedInfo","Ver información", null,
                        new ArrayList<CellParam>(Arrays.asList(
                                new CellParam("contributorId",
                                        encripttionService.encrypt(contributor.getContributor_id().toString()))
                        )
                        )));
            }
        } else if ((contributor.getContributorState() == ContributorState.PENDING.value)){
            contents.add(new CellContent("iconAccion",
                    "check", ColorCode.ACCEPT_CONTR.value, true,
                    "AcceptSeed","Aceptar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
            contents.add(new CellContent("iconAccion",
                    "clear",ColorCode.REJECT_CONTR.value, true,
                    "RejectSeed","Rechazar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                    "SeedInfo","Ver información", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
        } else if (contributor.getContributorState() == ContributorState.REJECTED.value){
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                    "SeedInfo","Ver información", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
        }
        else if (contributor.getContributorState() == ContributorState.PAUSED.value){
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                    "SeedInfo","Ver información", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
        }
        else if (contributor.getContributorState() == ContributorState.DESERTER.value){
            contents.add(new CellContent("iconAccion",
                    "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                    "SeedInfo","Ver información", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
        }

        return contents;
    }

    public List<ComboSeed> findActiveSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.ACCEPTED.value));
        List<ComboSeed> activecontr= new ArrayList<>();
        for (Contributor contributor:contributors){
            activecontr.add(new ComboSeed(
                    encripttionService.encrypt( contributor.getContributor_id().toString())
                    ,contributor.getUser().getName(),contributor.getUser().getLastname(),
                    contributor.getUser().getName()+ ' ' + contributor.getUser().getLastname(),
                    contributor.getUser().getEmail(),
                    contributor.getUser().getPhone(),
                    contributor.getUser().getDni()));
        }
        return activecontr;
    }

    public ContributorDTO getSeedById(String id){
        id = encripttionService.decrypt(id);
        Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(id));
        ContributorDTO contributorDTO = new ContributorDTO(contributor.get());
        ContributionConfigDTO contributionConfigDTO = new ContributionConfigDTO(contributor.get().getContributionConfig());
        if (contributor.get().getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)){
            ContributionDTO contributionDTO = new ContributionConstDTO(contributor.get().getContributionConfig().getConstantContribution());
            contributionConfigDTO.setContribution(contributionDTO);
        }
        else {
            ContributionDTO contributionDTO = new ContributionUniqDTO(contributor.get().getContributionConfig().getUniqueContribution());
            contributionConfigDTO.setContribution(contributionDTO);
        }
        contributorDTO.setContributionConfig(contributionConfigDTO);
        return contributorDTO;
    }

    public VolunterDTO getCurrentVolunter(Principal principal){
        VolunterDTO volunterDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
        volunterDTO.setVolunterId(encripttionService.encrypt(volunterDTO.getVolunterId()));
        volunterDTO.setUserId(encripttionService.encrypt(volunterDTO.getUserId()));
        return volunterDTO;
    }

    public Table findAllApplicants(SeedFilter volunterFilter){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == volunterFilter.status.value));
        Table resultTable = this.getContributtorsInFormat(contributors,false,
                volunterFilter.viewPage.equals("applicant"));
        return resultTable;
    }

    /*CONFIRMED SEEDS*/
    public ResponseEntity<RequestResponseMessage> updateUniqueContributor(UniqueAplicantHolderDao uniqueAplicantHolderDao){
        Long id = Long.parseLong(this.encripttionService.decrypt(uniqueAplicantHolderDao.getContributorId()));

        try{
            Contributor contributorHelper = this.contributorRepository.getById(id);

            contributorHelper.getContributionConfig().getUniqueContribution().setDate_contribution(uniqueAplicantHolderDao.getDate_contribution());
            contributorHelper.getContributionConfig().getUniqueContribution().getContribution().setContribution_amount(uniqueAplicantHolderDao.getContribution_amount());
            contributorHelper.getContributionConfig().getUniqueContribution().getContribution().setPaymentMethod(uniqueAplicantHolderDao.getPaymentMethod());
            contributorHelper.getContributionConfig().getUniqueContribution().getContribution().setSend_news(uniqueAplicantHolderDao.getSend_news());
            contributorHelper.getContributionConfig().getUniqueContribution().getContribution().setSendNewsType(uniqueAplicantHolderDao.getSendNewsType());
            contributorHelper.setAddress(uniqueAplicantHolderDao.getContributor().getAddress());
            contributorHelper.setCountry(uniqueAplicantHolderDao.getContributor().getCountry());
            contributorHelper.setCity(uniqueAplicantHolderDao.getContributor().getCity());
            contributorHelper.getUser().setName(uniqueAplicantHolderDao.getContributor().getUser().getName());
            contributorHelper.getUser().setLastname(uniqueAplicantHolderDao.getContributor().getUser().getLastname());
            contributorHelper.getUser().setEmail(uniqueAplicantHolderDao.getContributor().getUser().getEmail());
            contributorHelper.getUser().setPhone(uniqueAplicantHolderDao.getContributor().getUser().getPhone());
            contributorHelper.getUser().setDni(uniqueAplicantHolderDao.getContributor().getUser().getDni());
            contributorHelper.getUser().setBirthdate(uniqueAplicantHolderDao.getContributor().getUser().getBirthdate());


            try {
                Contributor resp = contributorRepository.save(contributorHelper);
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Sus datos fueron actualizados",
                                ResponseStatus.SUCCESS), HttpStatus.CREATED);
            }
            catch(Exception e) {
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Ocurrió un error actualizando los datos, porfavor intente mas tarde",
                                ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            "Ocurrió un error actualizando los datos, porfavor intente mas tarde",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity<RequestResponseMessage> updateConstantContributor(ConstantApplicantHolder constantApplicantHolder){
        Long id = Long.parseLong(this.encripttionService.decrypt(constantApplicantHolder.getContributorId()));
        try{
            Contributor contributorHelper = this.contributorRepository.getById(id);

            contributorHelper.getContributionConfig().getConstantContribution().setStart_month(constantApplicantHolder.getBeginMonth());
            contributorHelper.getContributionConfig().getConstantContribution().setPaymentDate(constantApplicantHolder.getPaymentDay());
            contributorHelper.getContributionConfig().getConstantContribution().setRemainderType(constantApplicantHolder.getReminderMethod());
            contributorHelper.getContributionConfig().getConstantContribution().getContribution().setContribution_amount(constantApplicantHolder.getContribution_amount());
            contributorHelper.getContributionConfig().getConstantContribution().getContribution().setPaymentMethod(constantApplicantHolder.getPaymentMethod());
            contributorHelper.getContributionConfig().getConstantContribution().getContribution().setSend_news(constantApplicantHolder.getSend_news());
            contributorHelper.getContributionConfig().getConstantContribution().getContribution().setSendNewsType(constantApplicantHolder.getSendNewsType());
            contributorHelper.setAddress(constantApplicantHolder.getContributor().getAddress());
            contributorHelper.setCountry(constantApplicantHolder.getContributor().getCountry());
            contributorHelper.setCity(constantApplicantHolder.getContributor().getCity());
            contributorHelper.getUser().setName(constantApplicantHolder.getContributor().getUser().getName());
            contributorHelper.getUser().setLastname(constantApplicantHolder.getContributor().getUser().getLastname());
            contributorHelper.getUser().setEmail(constantApplicantHolder.getContributor().getUser().getEmail());
            contributorHelper.getUser().setPhone(constantApplicantHolder.getContributor().getUser().getPhone());
            contributorHelper.getUser().setDni(constantApplicantHolder.getContributor().getUser().getDni());
            contributorHelper.getUser().setBirthdate(constantApplicantHolder.getContributor().getUser().getBirthdate());

            try {
                Contributor resp = contributorRepository.save(contributorHelper);
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Sus datos fueron actualizados, estan pendientes de revision.",
                                ResponseStatus.SUCCESS), HttpStatus.CREATED);
            }
            catch(Exception e) {
                return new ResponseEntity<>(
                        new RequestResponseMessage(
                                "Ocurrió un error actualizando los datos, porfavor intente mas tarde",
                                ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            "Ocurrió un erro actualizando los datos, porfavor intente mas tarde",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }
}

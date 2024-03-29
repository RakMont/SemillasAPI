package com.seedproject.seed.services;


import com.seedproject.seed.models.dao.ConstantApplicantHolder;
import com.seedproject.seed.models.dao.EnterpriseApplicantHolderDao;
import com.seedproject.seed.models.dao.UniqueApplicantHolderDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.dto.interfaces.ContributionDTO;
import com.seedproject.seed.models.dto.interfaces.SeedDTO;
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
    DeactivatedContributorRepository deactivatedContributorRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    VolunterService volunterService;
    @Inject
    ConstantContributionRepository constantContributionRepository;
    @Inject
    EncripttionService encripttionService;
    @Inject
    EmailSenderService emailSenderService;
    @Inject
    MenuService menuService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public ResponseEntity<RequestResponseMessage> saveConstantContributor(
            Principal principal, ConstantApplicantHolder constantApplicantHolder){

        Volunter volunteer;
        ContributionConfig contributionConfig;
        ConstantContribution constantContribution = new ConstantContribution();
        constantContribution.setStart_month(constantApplicantHolder.getBeginMonth());
        constantContribution.setPaymentDate(constantApplicantHolder.getPaymentDay());
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
        contributor.setIsForeign(constantApplicantHolder.getContributor().getIsForeign());
        contributor.getSeedConfigurations().add(contributionConfig);
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

            ConstantContribution constantContribution = contributorResp.getActiveContribution().getConstantContribution();
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

    public ResponseEntity<RequestResponseMessage> saveEnterpriseContributor(
            Principal principal, EnterpriseApplicantHolderDao enterpriseApplicantHolderDao){
        Volunter volunteer;
        ContributionConfig contributionConfig;
        EnterpriseContribution enterpriseContribution = new EnterpriseContribution();
        enterpriseContribution.setDate_contribution(enterpriseApplicantHolderDao.getDate_contribution());
        enterpriseContribution.setContribution(new Contribution(
                enterpriseApplicantHolderDao.getContribution_amount(),
                enterpriseApplicantHolderDao.getPaymentMethod(),
                enterpriseApplicantHolderDao.getSend_news(),
                enterpriseApplicantHolderDao.getSendNewsType()
        ));
        contributionConfig=contributionConfigService.saveEnterpriseContributionConfig(enterpriseContribution);
        Contributor contributor = enterpriseApplicantHolderDao.getContributor();
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.getSeedConfigurations().add(contributionConfig);
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
    public ResponseEntity<RequestResponseMessage> savUniqueContributor(
            Principal principal, UniqueApplicantHolderDao uniqueApplicantHolderDao){
        Volunter volunteer;
        ContributionConfig contributionConfig;
        UniqueContribution uniqueContribution = new UniqueContribution();
        uniqueContribution.setDate_contribution(uniqueApplicantHolderDao.getDate_contribution());
        uniqueContribution.setContribution(new Contribution(
                uniqueApplicantHolderDao.getContribution_amount(),
                uniqueApplicantHolderDao.getPaymentMethod(),
                uniqueApplicantHolderDao.getSend_news(),
                uniqueApplicantHolderDao.getSendNewsType()
        ));
        contributionConfig=contributionConfigService.saveUniqueContributionConfig(uniqueContribution);
        Contributor contributor = uniqueApplicantHolderDao.getContributor();
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.getSeedConfigurations().add(contributionConfig);
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
        if (processSeedDTO.getState()!= 2 && contributor.getSeedConfigurations().get(0).getContribution_key().equals(ContributionType.APORTE_CONSTANTE)){
            ConstantContribution constantContribution = contributor.getSeedConfigurations().get(0).getConstantContribution();
            constantContribution.setContributionStartDate(processSeedDTO.getContributionStartDate());
            constantContribution.setContributionEndDate(processSeedDTO.getContributionEndDate());
            constantContributionRepository.save(constantContribution);
        }
       try {
           processedContributorRepository.save(processedContributor);
           contributorRepository.save(contributor);
           if(processSeedDTO.getState() == 1){
               this.emailSenderService.sendSeedConfirmationEmailWithInlineImage(contributor.getUser());
           }
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
        Table resultTable = this.getContributorsInFormat(contributors, false, false);
        return resultTable;
    }

    public Table findAllPendingSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.PENDING.value));
        Table resultTable = this.getContributorsInFormat(contributors, false, false);
        return resultTable;
    }

    public Table findAcceptedSeeds(SeedFilter volunteerFilter){
        try{
            //List<Contributor> contributors = contributorRepository.findAll();
            List<SeedDTO> allSeeds = contributorRepository.getSeedsAsc(volunteerFilter.status.value);
            //contributors.removeIf(p -> !(p.getContributorState() == volunteerFilter.status.value));
            //contributors.removeIf(p -> !(p.getRegister_exist()));
            return  this.getAllSeedsInFormat(allSeeds);
            //return this.getContributorsInFormat(contributors,false, false);

        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }
    private Table getAllSeedsInFormat(List<SeedDTO> contributors){
        List<TableRow> resultList = new ArrayList<TableRow>();
        int index=1;
        for (SeedDTO contributor: contributors){
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
                                            contributor.getSeed_name(),
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
                                            null,null, contributor.getPhone(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Estado",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(this.getSeedStatus(contributor.getContributor_state()))
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable de seguimiento",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributor.getVol_name() != null ? contributor.getVol_name() : "Sin responsable asignado",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Config. Seguimiento",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            new ArrayList<CellContent>(
                                    //contributor.getContribution_key().equals(ContributionType.APORTE_CONSTANTE) ?
                                    Arrays.asList(
                                            new CellContent("iconAccion",
                                                    "group_add", ColorCode.ASSIGN_SEED.value, true,
                                                    "AssignSeed","Asignar responsable de seguimiento",
                                                    null,
                                                    new ArrayList<CellParam>(Arrays.asList(
                                                            new CellParam("seedId", encripttionService.encrypt(contributor.getContributor_id().toString()))
                                                            ,new CellParam("trackingAssignmentId", contributor.getTracking_assignment_id() != null ?
                                                                    encripttionService.encrypt(contributor.getTracking_assignment_id().toString()) : null)
                                                    ))
                                            )/*,
                                            new CellContent("iconAccion",
                                                    "date_range", "#009d71", true,
                                                    "ChangeDates","Cambiar Fechas de aporte constante", null,
                                                    new ArrayList<CellParam>(Arrays.asList(
                                                            new CellParam("contributionConfigId",
                                                                    encripttionService.encrypt(contributor.getContribution_config_id().toString()))
                                                    ))
                                            )*/
                                    )
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
                                            contributor.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value :
                                                    contributor.getContribution_key().equals(ContributionType.APORTE_UNICO) ?
                                                            ColorCode.UNIQUE_CONTRIBUTION.value : ColorCode.ENTERPRISE_CONTRIBUTION.value, false,
                                            null,null,
                                            contributor.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? "Aporte Constante" :
                                                    contributor.getContribution_key().equals(ContributionType.APORTE_UNICO) ?
                                                            "Aporte Único" : "Aporte Empresas",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("iconAccion",
                                            "edit", ColorCode.EDIT.value, true,
                                            "EditContr","Editar Datos", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",
                                                            encripttionService.encrypt(contributor.getContributor_id().toString()))
                                            ))
                                    ),
                                    new CellContent("iconAccion",
                                            "voice_over_off", ColorCode.DELETE.value, true,
                                            "Unactive","Desactivar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",
                                                            encripttionService.encrypt(contributor.getContributor_id().toString()))
                                            ))
                                    ),
                                    new CellContent("iconAccion", "remove_red_eye",ColorCode.VIEW_CONTR.value, true,
                            "SeedInfo","Ver información", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributorId",
                                                            encripttionService.encrypt(contributor.getContributor_id().toString()))
                                            )))
                            )
                    ))
            );
            resultList.add(new TableRow(cells));
            index++;
        }
        return new Table(resultList);
    }
    private Table getContributorsInFormat(List<Contributor> contributors, Boolean isTracking, Boolean isApplicantView){
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
                    new ArrayList<CellContent>(this.getSeedStatus(contributor.getContributorState()))
            ));
            cells.add(new Cell(
                    new CellHeader("Responsable Registro",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributor.getRegisterVolunter().getUser().getName()+ " " +
                                                    contributor.getRegisterVolunter().getUser().getLastname(),
                                            null)
                            )
                    )
            ));
            ContributionConfig contributionConfig = contributor.getActiveContribution(); /*this.getActiveContribution(contributor.getSeedConfigurations());*/
            cells.add(new Cell(
                    new CellHeader("Tipo aporte",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null,
                                            contributionConfig.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value :
                                                    contributionConfig.getContribution_key().equals(ContributionType.APORTE_UNICO) ?
                                                            ColorCode.UNIQUE_CONTRIBUTION.value : ColorCode.ENTERPRISE_CONTRIBUTION.value, false,
                                            null,null,
                                            contributionConfig.getContribution_key().toString(),
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
    private List<CellContent> getSeedStatus(int contributorState){
        List<CellContent> contents = new ArrayList<>();
        if (contributorState == ContributorState.ACCEPTED.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_ACEPTED.value, false,
                    null,null, "Aceptado",
                    null
            ));
        } else if (contributorState == ContributorState.PENDING.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PENDING.value, false,
                    null,null, "Pendiente",
                    null
            ));
        } else if (contributorState == ContributorState.REJECTED.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Rechazado",
                    null
            ));
        }else if (contributorState == ContributorState.PAUSED.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PAUSED.value, false,
                    null,null, "En pausa",
                    null
            ));
        }else if (contributorState == ContributorState.DESERTER.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Desertante",
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
                    ,contributor));
        }
        return activecontr;
    }

    public ContributorDTO getSeedById(String id){
        id = encripttionService.decrypt(id);
        Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(id));
        ContributorDTO contributorDTO = new ContributorDTO(contributor.get());
        ContributionConfig contributionConfig = contributor.get().getActiveContribution();/*this.getActiveContribution(contributor.get().getSeedConfigurations());*/
        ContributionConfigDTO contributionConfigDTO = new ContributionConfigDTO(contributionConfig);
        if (contributionConfig.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)){
            ContributionDTO contributionDTO = new ContributionConstDTO(contributionConfig.getConstantContribution());
            contributionConfigDTO.setContribution(contributionDTO);
        }
        else if (contributionConfig.getContribution_key().equals(ContributionType.APORTE_UNICO)){
            ContributionDTO contributionDTO = new ContributionUniqDTO(contributionConfig.getUniqueContribution());
            contributionConfigDTO.setContribution(contributionDTO);
        } else {
            ContributionDTO contributionDTO = new ContributionEnterpriseDTO(contributionConfig.getEnterpriseContribution());
            contributionConfigDTO.setContribution(contributionDTO);
        }
        contributorDTO.setContributionConfig(contributionConfigDTO);
        return contributorDTO;
    }

    public VolunterDTO getCurrentVolunter(Principal principal){
        VolunterDTO volunterDTO = new VolunterDTO((Volunter) this.userDetailsService.loadUserByUsername(principal.getName()));
        volunterDTO.setVolunterId(encripttionService.encrypt(volunterDTO.getVolunterId()));
        volunterDTO.setUserId(encripttionService.encrypt(volunterDTO.getUserId()));
        volunterDTO.setMenu(menuService.getMenuByRoles(volunterDTO.getRoles()));
        return volunterDTO;
    }

    public Table findAllApplicants(SeedFilter volunterFilter){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == volunterFilter.status.value));
        Table resultTable = this.getContributorsInFormat(contributors,false,
                volunterFilter.viewPage.equals("applicant"));
        return resultTable;
    }

    /*CONFIRMED SEEDS*/
    public ResponseEntity<RequestResponseMessage> updateUniqueContributor(UniqueApplicantHolderDao uniqueApplicantHolderDao){
        Long id = Long.parseLong(this.encripttionService.decrypt(uniqueApplicantHolderDao.getContributorId()));

        try{
            Contributor contributorHelper = this.contributorRepository.getById(id);

            contributorHelper.getActiveContribution().getUniqueContribution().setDate_contribution(uniqueApplicantHolderDao.getDate_contribution());
            contributorHelper.getActiveContribution().getUniqueContribution().getContribution().setContribution_amount(uniqueApplicantHolderDao.getContribution_amount());
            contributorHelper.getActiveContribution().getUniqueContribution().getContribution().setPaymentMethod(uniqueApplicantHolderDao.getPaymentMethod());
            contributorHelper.getActiveContribution().getUniqueContribution().getContribution().setSend_news(uniqueApplicantHolderDao.getSend_news());
            contributorHelper.getActiveContribution().getUniqueContribution().getContribution().setSendNewsType(uniqueApplicantHolderDao.getSendNewsType());

            contributorHelper.setAddress(uniqueApplicantHolderDao.getContributor().getAddress());
            contributorHelper.setCountry(uniqueApplicantHolderDao.getContributor().getCountry());
            contributorHelper.setCity(uniqueApplicantHolderDao.getContributor().getCity());
            contributorHelper.getUser().setName(uniqueApplicantHolderDao.getContributor().getUser().getName());
            contributorHelper.getUser().setLastname(uniqueApplicantHolderDao.getContributor().getUser().getLastname());
            contributorHelper.getUser().setEmail(uniqueApplicantHolderDao.getContributor().getUser().getEmail());
            contributorHelper.getUser().setPhone(uniqueApplicantHolderDao.getContributor().getUser().getPhone());
            contributorHelper.getUser().setDni(uniqueApplicantHolderDao.getContributor().getUser().getDni());
            contributorHelper.getUser().setBirthdate(uniqueApplicantHolderDao.getContributor().getUser().getBirthdate());


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

    public ResponseEntity<RequestResponseMessage>updateEnterpriseContributor(EnterpriseApplicantHolderDao enterpriseApplicantHolderDao){
        Long id = Long.parseLong(this.encripttionService.decrypt(enterpriseApplicantHolderDao.getContributorId()));

        try{
            Contributor contributorHelper = this.contributorRepository.getById(id);

            contributorHelper.getActiveContribution().getEnterpriseContribution().setDate_contribution(enterpriseApplicantHolderDao.getDate_contribution());
            contributorHelper.getActiveContribution().getEnterpriseContribution().getContribution().setContribution_amount(enterpriseApplicantHolderDao.getContribution_amount());
            contributorHelper.getActiveContribution().getEnterpriseContribution().getContribution().setPaymentMethod(enterpriseApplicantHolderDao.getPaymentMethod());
            contributorHelper.getActiveContribution().getEnterpriseContribution().getContribution().setSend_news(enterpriseApplicantHolderDao.getSend_news());
            contributorHelper.getActiveContribution().getEnterpriseContribution().getContribution().setSendNewsType(enterpriseApplicantHolderDao.getSendNewsType());

            contributorHelper.setAddress(enterpriseApplicantHolderDao.getContributor().getAddress());
            contributorHelper.setCountry(enterpriseApplicantHolderDao.getContributor().getCountry());
            contributorHelper.setCity(enterpriseApplicantHolderDao.getContributor().getCity());
            contributorHelper.getUser().setName(enterpriseApplicantHolderDao.getContributor().getUser().getName());
            contributorHelper.getUser().setLastname(enterpriseApplicantHolderDao.getContributor().getUser().getLastname());
            contributorHelper.getUser().setEmail(enterpriseApplicantHolderDao.getContributor().getUser().getEmail());
            contributorHelper.getUser().setPhone(enterpriseApplicantHolderDao.getContributor().getUser().getPhone());
            contributorHelper.getUser().setDni(enterpriseApplicantHolderDao.getContributor().getUser().getDni());
            contributorHelper.getUser().setBirthdate(enterpriseApplicantHolderDao.getContributor().getUser().getBirthdate());

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

            contributorHelper.getActiveContribution().getConstantContribution().setStart_month(constantApplicantHolder.getBeginMonth());
            contributorHelper.getActiveContribution().getConstantContribution().setPaymentDate(constantApplicantHolder.getPaymentDay());
            contributorHelper.getActiveContribution().getConstantContribution().setRemainderType(constantApplicantHolder.getReminderMethod());
            contributorHelper.getActiveContribution().getConstantContribution().getContribution().setContribution_amount(constantApplicantHolder.getContribution_amount());
            contributorHelper.getActiveContribution().getConstantContribution().getContribution().setPaymentMethod(constantApplicantHolder.getPaymentMethod());
            contributorHelper.getActiveContribution().getConstantContribution().getContribution().setSend_news(constantApplicantHolder.getSend_news());
            contributorHelper.getActiveContribution().getConstantContribution().getContribution().setSendNewsType(constantApplicantHolder.getSendNewsType());

            contributorHelper.getActiveContribution().getConstantContribution().setContributionStartDate(constantApplicantHolder.getContribution_start_date());
            contributorHelper.getActiveContribution().getConstantContribution().setContributionEndDate(constantApplicantHolder.getContribution_end_date());


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
                            "Ocurrió un error actualizando los datos, porfavor intente mas tarde",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<RequestResponseMessage> deactivateContributor(
            Principal principal, DeactivateContributorDTO deactivateContributorDTO){
        Volunter volunteer = (Volunter) this.userDetailsService.loadUserByUsername(principal.getName());
        Long id = Long.parseLong(this.encripttionService.decrypt(deactivateContributorDTO.getContributorId()));
        Contributor contributor = this.contributorRepository.getById(id);
        //contributor.setContributorState();
        DeactivatedContributor deactivateContributor = new DeactivatedContributor();
        deactivateContributor.setContributor(contributor);
        deactivateContributor.setRegVolunter(volunteer);
        deactivateContributor.setReactivationDate(deactivateContributorDTO.getReactivationDate());
        deactivateContributor.setDeactivationDate(deactivateContributorDTO.getDeactivationDate());
        deactivateContributor.setDeactivationReason(deactivateContributorDTO.getDeactivationReason());
        try{
            this.deactivatedContributorRepository.save(deactivateContributor);
            this.contributorRepository.updateContributorState(id,deactivateContributorDTO.getContributorState().value);
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            deactivateContributorDTO.getContributorState().equals(ContributorState.PAUSED) ?
                                    "El estado de la semilla fue cambiado a en pausa." :
                            "El estado de la semilla fue cambiado a desertante",
                            ResponseStatus.SUCCESS), HttpStatus.ACCEPTED);

        }catch (Exception exception){
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            "Ocurrió un error realizando esta acción",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

        }
    }

    public BadgeDTO getPendingSeedsNumber() {
       try {
           List<Long> contributorList = contributorRepository.getPendingSeeds();
           BadgeDTO badgeDTO = new BadgeDTO();
           badgeDTO.setPendingSeedsNumber((long) contributorList.size());

           return  badgeDTO;
       }catch (Exception exception){
           throw new RuntimeException(exception.getMessage());
       }
    }
}

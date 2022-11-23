package com.seedproject.seed.services;


import com.seedproject.seed.models.dao.UniqueAplicantHolderDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.*;
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

    public ResponseEntity<RequestResponseMessage> saveConstantContributtor(ConstantAplicantHolder constantAplicantHolder){
        ConstantContribution constantContribution = new ConstantContribution();
        constantContribution.setStart_month(constantAplicantHolder.getBeginMonth());
        constantContribution.setPaymentDate(PaymentDate.CADA_10_DEL_MES);
        constantContribution.setRemainderType(constantAplicantHolder.getReminderMethod());
        constantContribution.setContribution(new Contribution(
                constantAplicantHolder.getContribution_amount(),
                constantAplicantHolder.getPaymentMethod(),
                constantAplicantHolder.getSend_news(),
                constantAplicantHolder.getSendNewsType()
        ));

        ContributionConfig contributionConfig=contributionConfigService.saveConstantContributionConfig(constantContribution);
        //System.out.println("salvo la configuracion" + contributionConfig.getContribution_key());
        Volunter volunter = volunterRepository.getById(Long.parseLong("1"));
        Contributor contributor = constantAplicantHolder.getContributor();
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.setContributorState(ContributorState.PENDIENTE.value);
        contributor.setContributionConfig(contributionConfig);
        contributor.setRegisterVolunter(volunter);
        ResponseMessage response;
        try {
            Contributor resp = contributorRepository.save(contributor);
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            "Sus datos fueron registrados, estan pendientes de revision." +
                                    "Un responsable se pondrá en contacto con usted",
                            ResponseStatus.SUCCESS), HttpStatus.CREATED);
        }
        catch(Exception e) {
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                            "Ocurrió un erro registrando sus datos, porfavor intente mas tarde",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<RequestResponseMessage> savUniqueContributtor(UniqueAplicantHolderDao uniqueAplicantHolderDao){
        UniqueContribution uniqueContribution = new UniqueContribution();
        uniqueContribution.setDate_contribution(uniqueAplicantHolderDao.getDate_contribution());
        uniqueContribution.setContribution(new Contribution(
                uniqueAplicantHolderDao.getContribution_amount(),
                uniqueAplicantHolderDao.getPaymentMethod(),
                uniqueAplicantHolderDao.getSend_news(),
                uniqueAplicantHolderDao.getSendNewsType()
        ));
        ContributionConfig contributionConfig=contributionConfigService.saveUniqueContributionConfig(uniqueContribution);
        System.out.println("salvo la configuracion " + contributionConfig.getContribution_key());
        Contributor contributor = uniqueAplicantHolderDao.getContributor();
        //Contributor contributor = new Contributor();
        Volunter volunter = volunterRepository.getById(Long.parseLong("1"));
        contributor.setSend_date(new Date());
        contributor.setRegister_date(new Date());
        contributor.setContributorState(ContributorState.PENDIENTE.value);
        contributor.setContributionConfig(contributionConfig);
        contributor.setRegisterVolunter(volunter);
        ResponseMessage response;
        try {
            Contributor resp = contributorRepository.save(contributor);
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                    "Sus datos fueron registrados, estan pendientes de revision." +
                                "Un responsable se pondrá en contacto con usted",
                    ResponseStatus.SUCCESS), HttpStatus.CREATED);
            /*response=new ResponseMessage(
                    "Created",
                    "El aplicante fue creado",
                    200
            );
            return response;*/
        }
        catch(Exception e) {
            return new ResponseEntity<>(
                    new RequestResponseMessage(
                    "Ocurrió un erro registrando sus datos, porfavor intente mas tarde",
                            ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
            /*response=new ResponseMessage(
                    "Error",
                    "El aplicante fue creado",
                    400
            );
            return response;*/
        }
    }

    public Table findRejectedSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.RECHAZADO.value));
        Table resultTable = this.getContributtorsInFormat(contributors, false);
        return resultTable;
    }

    public Table findAllPendingSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.PENDIENTE.value));
        Table resultTable = this.getContributtorsInFormat(contributors, false);
        return resultTable;
    }

    public Table findAceptedSeeds(){
        List<Contributor> contributors = contributorRepository.findAll();
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.ACEPTADO.value));
        Table resultTable = this.getContributtorsInFormat(contributors,false);
        return resultTable;
    }

    public Contributor rejectApplicant(Long applicant_id, String reason){
        Contributor contributor = contributorRepository.getById(applicant_id);
        DeactivatedContributor deactivatedContributor =new DeactivatedContributor();
        //deactivatedContributor.setReject_reason(reason);
        //deactivatedContributor.setReject_date(new Date());
        deactivatedContributor.setContributor(contributor);
        rejectedApplicantRepository.save(deactivatedContributor);
        //contributor.setContributorState(ContributorState.RECHAZADO);
        System.out.println("an" + ContributorState.RECHAZADO);
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

    private Table getContributtorsInFormat(List<Contributor> contributors, Boolean isTracking){
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
                    new ArrayList<CellContent>(this.getSeedActions(contributor, isTracking)
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
        if (contributor.getContributorState() == ContributorState.ACEPTADO.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_ACEPTED.value, false,
                    null,null, "Aceptado",
                    null
            ));
        } else if (contributor.getContributorState() == ContributorState.PENDIENTE.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_PENDING.value, false,
                    null,null, "Pendiente",
                    null
            ));
        } else if (contributor.getContributorState() == ContributorState.RECHAZADO.value){
            contents.add(new CellContent(
                    "chipContent",
                    null, ColorCode.STATE_REJECTED.value, false,
                    null,null, "Rechazado",
                    null
            ));
        }
        return contents;
    }

    private List<CellContent> getSeedActions(Contributor contributor, Boolean isTracking){
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
        else if ((contributor.getContributorState() == ContributorState.ACEPTADO.value)){
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
        } else if ((contributor.getContributorState() == ContributorState.PENDIENTE.value)){
            contents.add(new CellContent("iconAccion",
                    "offline_pin", ColorCode.ACCEPT_CONTR.value, true,
                    "AceptSeed","Aceptar semilla", null,
                    new ArrayList<CellParam>(Arrays.asList(
                            new CellParam("contributorId",
                                    encripttionService.encrypt(contributor.getContributor_id().toString()))
                    ))));
            contents.add(new CellContent("iconAccion",
                    "highlight_off",ColorCode.REJECT_CONTR.value, true,
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
        } else if (contributor.getContributorState() == ContributorState.RECHAZADO.value){
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
        contributors.removeIf(p -> !(p.getContributorState() == ContributorState.ACEPTADO.value));
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
}

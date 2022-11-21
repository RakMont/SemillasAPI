package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.ColorCode;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.ResponseStatus;
import com.seedproject.seed.repositories.ContributionConfigRepository;
import com.seedproject.seed.repositories.ContributionRecordRepository;
import com.seedproject.seed.repositories.TrackingAssignmentRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContributionRecordService {
    @Inject
    ContributionRecordRepository contributionRecordRepository;
    @Inject
    TrackingAssignmentRepository trackingAssignmentRepository;
    @Inject
    ContributionConfigRepository contributionConfigRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    EncripttionService encripttionService;

    public Table getSeedContributionRecords(String SeedId){
        //Long id = Long.parseLong(encripttionService.decrypt(SeedId));
        List<ContributionRecord> contributionRecords = contributionRecordRepository.findAll();
        return this.getContributionsInformat(contributionRecords);
    }

    public Table getContributionsInformat(List<ContributionRecord> contributionRecords){
        List<TableRow> resultList = new ArrayList<TableRow>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int index=1;
        for (ContributionRecord contributionRecord : contributionRecords){
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
                    new CellHeader("Fecha prevista",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributionRecord.getExpected_payment_date() != null ?
                                                    formatter.format(contributionRecord.getExpected_payment_date()) : " ", null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha pago",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributionRecord.getPayment_date() !=null ?
                                                    formatter.format(contributionRecord.getPayment_date()) : " ", null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto de pago",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getContribution_ammount().toString(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto extra",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getExtra_income_ammount(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Codigo recibo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, contributionRecord.getReceipt_code(),
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Número recibo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null, contributionRecord.getReceipt_number(),
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
                                            null, ColorCode.PAYMENT_METHODS.value,false,
                                            null,null,
                                            contributionRecord.getPaymentMethod().equals(PaymentMethod.CODIGO_QR) ?
                                                    "Codigo QR" : contributionRecord.getPaymentMethod().equals(PaymentMethod.DEPOSITO_BANCARIO) ?
                                                    "Depósito Bancario" : contributionRecord.getPaymentMethod().equals(PaymentMethod.EFECTIVO) ?
                                                    "Efectivo" : "Transferencia",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Opciones",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    /*new CellContent("iconAccion",
                                            "edit", ColorCode.EDIT.value, true,
                                            "editRecord","Editar", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributionRecordId",
                                                            encripttionService.encrypt(contributionRecord.getContribution_record_id().toString()))
                                            ))),*/
                                    new CellContent("iconAccion",
                                            "delete",ColorCode.DELETE.value, true,
                                            "deleteRecord","Rechazar semilla", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributionRecordId",
                                                            encripttionService.encrypt(contributionRecord.getContribution_record_id().toString()))
                                            ))),
                                    new CellContent("iconAccion",
                                            "remove_red_eye","#efc561", true,
                                            "SeeRecord","Ver información", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributionRecordId",
                                                            encripttionService.encrypt(contributionRecord.getContribution_record_id().toString()))
                                            )))
                            )
                    )
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        resultList.add(this.getFooter(contributionRecords));
        return new Table(resultList);
    }

    public ResponseEntity<RequestResponseMessage> saveContributionRecord(ContributionRecordDao contributionRecordDao){
        ContributionRecord contributionRecord = new ContributionRecord();

        contributionRecordDao.setTracking_assignment_id(encripttionService.decrypt( contributionRecordDao.getTracking_assignment_id()));
        contributionRecordDao.setContribution_config_id(encripttionService.decrypt(contributionRecordDao.getContribution_config_id()));
        contributionRecordDao.setContributor_id(encripttionService.decrypt(contributionRecordDao.getContributor_id()));

        TrackingAssignment trackingAssignment = trackingAssignmentRepository.getById(Long.parseLong(contributionRecordDao.getTracking_assignment_id()));
        ContributionConfig contributionConfig = contributionConfigRepository.getById(Long.parseLong(contributionRecordDao.getContribution_config_id()));
        Volunter volunter = volunterRepository.getById(trackingAssignment.getVolunter_id());
        contributionRecord.setContributor_id(Long.parseLong(contributionRecordDao.getContributor_id()));
        contributionRecord.setPayment_date(contributionRecordDao.getPayment_date());
        contributionRecord.setExpected_payment_date(contributionRecordDao.getExpected_payment_date());
        contributionRecord.setPaymentMethod(contributionRecordDao.getPaymentMethod());
        contributionRecord.setContribution_ammount(contributionRecordDao.getContribution_ammount());
        contributionRecord.setReceipt_number(contributionRecordDao.getReceipt_number());
        contributionRecord.setReceipt_code(contributionRecordDao.getReceipt_code());
        contributionRecord.setExtra_income_ammount(contributionRecordDao.getExtra_income_ammount());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_obtained());
        contributionRecord.setSent_payment_proof(contributionRecordDao.getSent_payment_proof());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_ammount() > 0);
        contributionRecord.setVolunter(volunter);
        contributionRecord.setContributionConfig(contributionConfig);
        contributionRecord.setTrackingAssignment(trackingAssignment);

        try {
            contributionRecordRepository.save(contributionRecord);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El aporte fue registrado", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando el aporte", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public  void updateContributionRecord(ContributionRecordDao contributionRecordDao){
        contributionRecordDao.setContribution_record_id(encripttionService.decrypt(contributionRecordDao.getContribution_record_id()));
        ContributionRecord contributionRecord = contributionRecordRepository.findById(Long.parseLong(contributionRecordDao.getContribution_record_id())).get();

        contributionRecord.setPayment_date(contributionRecordDao.getPayment_date());
        contributionRecord.setExpected_payment_date(contributionRecordDao.getExpected_payment_date());
        contributionRecord.setPaymentMethod(contributionRecordDao.getPaymentMethod());
        contributionRecord.setContribution_ammount(contributionRecordDao.getContribution_ammount());
        contributionRecord.setReceipt_number(contributionRecordDao.getReceipt_number());
        contributionRecord.setReceipt_code(contributionRecordDao.getReceipt_code());
        contributionRecord.setExtra_income_ammount(contributionRecordDao.getExtra_income_ammount());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_obtained());
        contributionRecord.setSent_payment_proof(contributionRecordDao.getSent_payment_proof());
        try {
            contributionRecordRepository.save(contributionRecord);
        }catch (Exception exception){
            System.out.println("exception.getMessage();" + exception.getMessage());
        }
    }


    public void deleteContributionRecord(String id){
        id = encripttionService.decrypt(id);
        try {
            contributionRecordRepository.deleteById(Long.parseLong(id));
        }catch (Exception exception){
            System.out.println("dasdasdasdaerror" + exception);
        }
    }

    public TableRow getFooter(List<ContributionRecord> contributionRecords){
        int total = 0;
        int totalExtra = 0;
        for (ContributionRecord contributionRecord: contributionRecords){
            total = total+contributionRecord.getContribution_ammount().intValue();
            totalExtra = totalExtra + Integer.parseInt(contributionRecord.getExtra_income_ammount());
        }
        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(
                new CellHeader("#",0,"Integer",false,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,
                                        null,null,"",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Fecha prevista",0,"String",true,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Fecha pago",0,"String",true,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "Total", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto de pago",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        total + " BOB",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto extra",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        totalExtra + " BOB",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Codigo recibo",0,"String",true,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Número recibo",0,"String",true,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Tipo aporte",0,"String",true,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Opciones",0,"String",false,null),
                new CellProperty("#161d2b",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "",
                                        null)
                        )
                )
        ));
        return new TableRow(cells);

    }
}

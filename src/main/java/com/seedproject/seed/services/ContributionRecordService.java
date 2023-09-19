package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.ContributionRecordDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.*;
import com.seedproject.seed.models.enums.*;
import com.seedproject.seed.models.filters.ContributionRecordFilter;
import com.seedproject.seed.models.reports.ContributionRecordReportDTO;
import com.seedproject.seed.repositories.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    ContributorRepository contributorRepository;
    @Inject
    EncripttionService encripttionService;

    public ResponseEntity<byte[]> getContributionRecordsReport(ContributionRecordFilter contributionRecordFilter){
        ResponseEntity<byte[]> finalResult = null;
        switch (contributionRecordFilter.getReportType()){
            case TOTAL_AMOUNT_PDF:
                finalResult = this.getTOTAL_AMOUNT_PDF(contributionRecordFilter);
                break;
            case TOTAL_AMOUNT_CSV:
                finalResult = this.getTOTAL_AMOUNT_CSV(contributionRecordFilter);
                break;
            case SEED_RECORD_PDF:
                finalResult = this.getSEED_RECORD_PDF(contributionRecordFilter);
                break;
            case SEED_RECORD_CSV:
                finalResult = this.getSEED_RECORD_CSV(contributionRecordFilter);
                break;
        }

        return  finalResult;
    }

    public ResponseEntity<byte[]> getSEED_RECORD_PDF(ContributionRecordFilter contributionRecordFilter){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Long seedId = Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId()));
            List<ContributionRecordReportDTO> contributionRecordReportDTOList = this.getSeedRecords(seedId);
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId())));
            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_seed_description", "Se muestran todos los aportes obtenidos de la semilla:");
            empParams.put("label_seed_name", contributor.get().getUser().getName()+ " " + contributor.get().getUser().getLastname());
            empParams.put("label_title", "REPORTE: APORTES DE SEMILLA");
            empParams.put("label_seed_phone",contributor.get().getUser().getPhone());
            empParams.put("label_seed_id", contributor.get().getUser().getDni());
            empParams.put("label_seed_country",contributor.get().getCountry());
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));

            String resourceUtils = ResourceUtils.getFile("classpath:./templates/reports/report_seed_contributionsPDF.jrxml")
                    .getAbsolutePath();

            JasperPrint jprint = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(resourceUtils)
                    , empParams, new JRBeanCollectionDataSource(contributionRecordReportDTOList));


            HttpHeaders headers = new HttpHeaders();
            //set the PDF format
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "seed_records_report.pdf");

            return new ResponseEntity<byte[]>
                    (JasperExportManager.exportReportToPdf(jprint), headers, HttpStatus.OK);


        } catch(JRException | FileNotFoundException ex){
            System.out.println("ON ERROR " + ex.getMessage());
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<ContributionRecordReportDTO> getSeedRecords(Long seed_Id){
        List<ContributionReportDTO> contributionRecords =
                contributionRecordRepository.findContributionsBySeed(seed_Id);
        List<ContributionRecordReportDTO> contributionRecordReportDTOS = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        int index = 0;
        Long total_amount= 0L;
        Long total_extra=0L;
        Long total_spent=0L;

        for(ContributionReportDTO contributionRecord : contributionRecords){
            index++;
            total_amount = total_amount + contributionRecord.getPayment_amount().intValue();
            total_extra = total_extra + (contributionRecord.getExtra_amount() != null ?
                    Integer.parseInt(contributionRecord.getExtra_amount()) : 0);
            total_spent = total_spent + (contributionRecord.getSpent_amount() != null ?
                    Integer.parseInt(contributionRecord.getSpent_amount()) : 0 );
            contributionRecordReportDTOS.add(new ContributionRecordReportDTO(Integer.toString(index), contributionRecord, formatter));
        }

        ContributionRecordReportDTO lastLine= new ContributionRecordReportDTO();
        lastLine.setNro("");
        lastLine.setPayment_date(" TOTAL : ");
        lastLine.setPayment_amount(total_amount);
        lastLine.setExtra_amount(total_extra);
        lastLine.setSpent_amount(total_spent);
        contributionRecordReportDTOS.add(lastLine);
        return  contributionRecordReportDTOS;
    }

    private  List<ContributionRecordReportDTO> getContributionsReportList(ContributionRecordFilter contributionRecordFilter){
        try{
            List<ContributionReportDTO> contributionReportDTOS =
                    contributionRecordRepository.findContributionRecord(contributionRecordFilter.getBeginDate(),contributionRecordFilter.getEndDate());

            List<ContributionRecordReportDTO> contributionRecordReportDTOS = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            int index = 0;
            Long total_amount=0L;
            Long total_extra=0L;
            Long total_spent=0L;

            for(ContributionReportDTO contributionRecord : contributionReportDTOS){
                index++;
                total_amount = total_amount + contributionRecord.getPayment_amount().intValue();
                total_extra = total_extra + (contributionRecord.getExtra_amount() != null ?
                        Integer.parseInt(contributionRecord.getExtra_amount()) : 0);
                total_spent = total_spent + (contributionRecord.getSpent_amount() != null ?
                        Integer.parseInt(contributionRecord.getSpent_amount()) : 0 );
                contributionRecordReportDTOS.add(new ContributionRecordReportDTO(Integer.toString(index), contributionRecord, formatter));
            }

            ContributionRecordReportDTO lastLine= new ContributionRecordReportDTO();
            lastLine.setNro("");
            lastLine.setPayment_date(" TOTAL : ");
            lastLine.setPayment_amount(total_amount);
            lastLine.setExtra_amount(total_extra);
            lastLine.setSpent_amount(total_spent);
            contributionRecordReportDTOS.add(lastLine);
            return  contributionRecordReportDTOS;
        }
        catch (Exception e){
            throw e;
        }
    }
    public ResponseEntity<byte[]> getSEED_RECORD_CSV(ContributionRecordFilter contributionRecordFilter){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Long seedId = Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId()));
            List<ContributionRecordReportDTO> contributionRecordReportDTOList = this.getSeedRecords(seedId);
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId())));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_seed_description", "Se muestran todos los aportes obtenidos de la semilla:");
            empParams.put("label_seed_name", contributor.get().getUser().getName()+ " " + contributor.get().getUser().getLastname());
            empParams.put("label_title", "REPORTE: APORTES DE SEMILLA");
            empParams.put("label_seed_phone",contributor.get().getUser().getPhone());
            empParams.put("label_seed_id", contributor.get().getUser().getDni());
            empParams.put("label_seed_country",contributor.get().getCountry());
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));

            String resourceUtils = ResourceUtils.getFile("classpath:./templates/reports/report_seed_contributionsCSV.jrxml")
                    .getAbsolutePath();
            JasperPrint jprint = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(resourceUtils)
                    , empParams, new JRBeanCollectionDataSource(contributionRecordReportDTOList));


            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jprint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            byte[] bs = stream.toByteArray();
            return new ResponseEntity<byte[]>(bs, HttpStatus.ACCEPTED);
        }catch (JRException exception){
            exception.getMessage();
            throw  new RuntimeException(exception);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public ResponseEntity<byte[]> getTOTAL_AMOUNT_CSV(ContributionRecordFilter contributionRecordFilter){
        try {
            List<ContributionRecordReportDTO> contributionRecordReportDTOS = this.getContributionsReportList(contributionRecordFilter);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_description", "Se muestran todos los reportes obtenidos en los siguientes filtros.");
            empParams.put("contribution_records", "fhgjgjhgj");
            empParams.put("label_title", "REPORTE DE APORTES OBTENIDOS");
            empParams.put("label_dates", "F.Inicio: " +
                    formatter.format(contributionRecordFilter.getBeginDate()) +
                    " - F.Fin " +formatter.format(contributionRecordFilter.getEndDate()));
            empParams.put("label_payment_method",contributionRecordFilter.getPaymentMethod() != null ? contributionRecordFilter.getPaymentMethod() : "TODOS");
            empParams.put("total_extra", contributionRecordReportDTOS.get(contributionRecordReportDTOS.size()-1).getExtra_amount());
            empParams.put("label_contr_type", contributionRecordFilter.getContributionType() != null ? contributionRecordFilter.getContributionType() : "TODOS");

            String resourceUtils = ResourceUtils.getFile("classpath:./templates/reports/report_generalCSV.jrxml")
                    .getAbsolutePath();
            JasperPrint jprint = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(resourceUtils)
                    , empParams, new JRBeanCollectionDataSource(contributionRecordReportDTOS));


            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jprint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(stream));
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            byte[] bs = stream.toByteArray();
            return new ResponseEntity<byte[]>(bs, HttpStatus.ACCEPTED);
        }catch (JRException exception){
            exception.getMessage();
            return null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private ResponseEntity<byte[]>getTOTAL_AMOUNT_PDF(ContributionRecordFilter contributionRecordFilter){
        try {
            List<ContributionRecordReportDTO> contributionRecordReportDTOS = this.getContributionsReportList(contributionRecordFilter);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_description", "Se muestran todos los reportes obtenidos en los siguientes filtros.");
            empParams.put("contribution_records", "fhgjgjhgj");
            empParams.put("label_title", "REPORTE DE APORTES OBTENIDOS");
            empParams.put("label_dates", "F.Inicio: " +
                    formatter.format(contributionRecordFilter.getBeginDate()) +
                    " - F.Fin " +formatter.format(contributionRecordFilter.getEndDate()));
            empParams.put("label_payment_method",contributionRecordFilter.getPaymentMethod() != null ? contributionRecordFilter.getPaymentMethod() : "TODOS");
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));
            empParams.put("label_contr_type", contributionRecordFilter.getContributionType() != null ? contributionRecordFilter.getContributionType() : "TODOS");

            String resourceUtils = ResourceUtils.getFile("classpath:./templates/reports/report_generalPDF.jrxml")
                    .getAbsolutePath();

            JasperPrint jprint = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(resourceUtils)
                    , empParams, new JRBeanCollectionDataSource(contributionRecordReportDTOS));


            HttpHeaders headers = new HttpHeaders();
            //set the PDF format
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "contributionrecords.pdf");

            return new ResponseEntity<byte[]>
                    (JasperExportManager.exportReportToPdf(jprint), headers, HttpStatus.OK);


        } catch(JRException | FileNotFoundException ex){
            ex.getMessage();
            System.out.println("ON ERROR" + ex.getMessage());
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public Table getSeedContributionRecords(String SeedId){
        Long id = Long.parseLong(encripttionService.decrypt(SeedId));
       try{
           List<ContributionReportDTO> contributionRecords = contributionRecordRepository.findContributionsBySeed(id);

           if (!contributionRecords.isEmpty()) return this.getContributionsInFormat(contributionRecords);
           else return null;
       }catch (Exception exception){
            return null;
        }
    }



    public Table getAllDonations(ContributionRecordFilter contributionRecordFilter){
       // List<ContributionRecord> contributionRecords = contributionRecordRepository.findAll();
        List<ContributionReportDTO> contributionReportDTOS =
                contributionRecordRepository.findContributionRecord(contributionRecordFilter.getBeginDate(),contributionRecordFilter.getEndDate());
        return this.getAllContributionsFormat(contributionReportDTOS);
    }

    /*public Table getExportRecords(ContributionRecordFilter contributionRecordFilter){
        List<ContributionRecord> contributionRecords = contributionRecordRepository.findAll();
        return this.getExportRecordsFormat(contributionRecords);
    }*/

    public Table getAllContributionsFormat(List<ContributionReportDTO> contributionRecords){
        List<TableRow> resultList = new ArrayList<TableRow>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int index=1;
        for (ContributionReportDTO contributionRecord : contributionRecords){
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
                    new CellHeader("Semilla",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getSeed_name(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tipo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null,
                                            "#eaae4e",false,null, null,
                                            contributionRecord.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? "Aporte Constante" : "Aporte unico", null
                                            /*contributionRecord.getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value : ColorCode.UNIQUE_CONTRIBUTION.value, false,
                                            null,null,
                                            contributionRecord.getContributionConfig().getContribution_key().toString(),
                                            null*/)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha pago",0,"String",false,null),
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
                                            contributionRecord.getPayment_amount().toString(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto extra",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getExtra_amount(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Gasto",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getSpent_amount() != null ?
                                                    contributionRecord.getSpent_amount()
                                                    : "0"
                                            ,null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Codigo recibo",0,"String",false,null),
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
                    new CellHeader("Número recibo",0,"String",false,null),
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
                    new CellHeader("Tipo aporte",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null, ColorCode.PAYMENT_METHODS.value,false,
                                            null,null,
                                            contributionRecord.getPayment_method().equals(PaymentMethod.CODIGO_QR) ?
                                                    "Codigo QR" : contributionRecord.getPayment_method().equals(PaymentMethod.DEPOSITO_BANCARIO) ?
                                                    "Depósito Bancario" : contributionRecord.getPayment_method().equals(PaymentMethod.EFECTIVO) ?
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
                                    new CellContent("iconAccion",
                                            "description","#efc561", true,
                                            "SeeRecord","Ver detalles", null,
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
        resultList.add(this.getFooter(contributionRecords, true, false));
        return new Table(resultList);
    }
    public Table getContributionsInFormat(List<ContributionReportDTO> contributionRecords){
        List<TableRow> resultList = new ArrayList<TableRow>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int index=1;
        for (ContributionReportDTO contributionRecord : contributionRecords){
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
                                            contributionRecord.getPayment_amount().toString(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto extra",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getExtra_amount(),null)
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
                                            contributionRecord.getPayment_method().equals(PaymentMethod.CODIGO_QR) ?
                                                    "Codigo QR" : contributionRecord.getPayment_method().equals(PaymentMethod.DEPOSITO_BANCARIO) ?
                                                    "Depósito Bancario" : contributionRecord.getPayment_method().equals(PaymentMethod.EFECTIVO) ?
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
                                    new CellContent("iconAccion",
                                            "delete",ColorCode.STATE_REJECTED.value, true,
                                            "deleteRecord","Eliminar aporte", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributionRecordId",
                                                            encripttionService.encrypt(contributionRecord.getContribution_record_id().toString()))
                                            ))),
                                    new CellContent("iconAccion",
                                            "description",ColorCode.VIEW_CONTR.value, true,
                                            "SeeRecord","Ver Detalle", null,
                                            new ArrayList<CellParam>(Arrays.asList(
                                                    new CellParam("contributionRecordId",
                                                            encripttionService.encrypt(contributionRecord.getContribution_record_id().toString()))
                                            ))),
                                    new CellContent("iconAccion",
                                            "edit", ColorCode.EDIT.value, true,
                                            "UpdateRecord", "Actualizar aporte", null,
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
        resultList.add(this.getFooter(contributionRecords, false, false));
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
        Contributor contributor = contributorRepository.getById(Long.parseLong(contributionRecordDao.getContributor_id()));
        contributionRecord.setContributor(contributor);
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
        contributionRecord.setRegister_exist(true);
        try {
            contributionRecordRepository.save(contributionRecord);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El aporte fue registrado", ResponseStatus.SUCCESS), HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error registrando el aporte", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);
        }
    }

    public  ResponseEntity<RequestResponseMessage> updateContributionRecord(ContributionRecordDao contributionRecordDao){
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
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El aporte fue actualizado", ResponseStatus.SUCCESS), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error actualizando el aporte", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

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

    public TableRow getFooter(List<ContributionReportDTO> contributionRecords, Boolean isAll, Boolean isReport){
        int total = 0;
        int totalExtra = 0;
        for (ContributionReportDTO contributionRecord: contributionRecords){
            total = total+contributionRecord.getPayment_amount().intValue();
            totalExtra = totalExtra + Integer.parseInt(contributionRecord.getExtra_amount());
        }
        List<Cell> cells = new ArrayList<Cell>();
        if (!isAll){
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
            if (!isReport){
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
            }
        }
        else {
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
                    new CellHeader("Semilla",0,"Integer",false,null),
                    new CellProperty("#161d2b",false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,
                                            null,null,"",null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tipo",0,"Integer",false,null),
                    new CellProperty("#161d2b",false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,
                                            null,null,"",null)
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
                    new CellHeader("Gasto",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            "0" + " BOB",null)
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
            if  (!isReport) {
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
            }

        }
        return new TableRow(cells);
    }

    public Table getExportRecordsFormat(List<ContributionReportDTO> contributionRecords){
        List<TableRow> resultList = new ArrayList<TableRow>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int index=1;
        for (ContributionReportDTO contributionRecord : contributionRecords){
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
                    new CellHeader("Semilla",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getSeed_name()
                                            ,null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Tipo",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null,
                                            "#eaae4e",false,null, null,
                                            contributionRecord.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? "Aporte Constante" : "Aporte unico", null
                                           )
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Fecha pago",0,"String",false,null),
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
                                            contributionRecord.getPayment_amount().toString(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto extra",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getExtra_amount(),null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Gasto",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getSpent_amount() != null ?
                                                    contributionRecord.getSpent_amount()
                                                    : "0"
                                            ,null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Codigo recibo",0,"String",false,null),
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
                    new CellHeader("Número recibo",0,"String",false,null),
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
                    new CellHeader("Tipo aporte",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("chipContent",
                                            null, ColorCode.PAYMENT_METHODS.value,false,
                                            null,null,
                                            contributionRecord.getPayment_method().equals(PaymentMethod.CODIGO_QR) ?
                                                    "Codigo QR" : contributionRecord.getPayment_method().equals(PaymentMethod.DEPOSITO_BANCARIO) ?
                                                    "Depósito Bancario" : contributionRecord.getPayment_method().equals(PaymentMethod.EFECTIVO) ?
                                                    "Efectivo" : "Transferencia",
                                            null)
                            )
                    )
            ));
            resultList.add(new TableRow(cells));
            index++;
        }
        resultList.add(this.getFooter(contributionRecords, true, true));
        return new Table(resultList);
    }
    public ContributionRecordDTO getContributionRecordById(String id){
        id = encripttionService.decrypt(id);
        try{
            Optional<ContributionRecord> contributionRecord = contributionRecordRepository.findById(Long.parseLong(id));

            ContributionRecordDTO contributionConfigDTO = new ContributionRecordDTO(contributionRecord.get());
            contributionConfigDTO.setContributionRecordId(encripttionService.encrypt(contributionConfigDTO.getContributionRecordId()));
            contributionConfigDTO.setTrackingAssignmentId(encripttionService.encrypt(contributionConfigDTO.getTrackingAssignmentId()));
            contributionConfigDTO.setContributionConfigId(encripttionService.encrypt(contributionConfigDTO.getContributionConfigId()));
            contributionConfigDTO.getContributorDTO().setSeedId(encripttionService.encrypt(contributionConfigDTO.getContributorDTO().getSeedId()));

            return contributionConfigDTO;
        }catch (Exception e){
            throw e;
        }
    }

    public List<ContributionRecord> uniqueContributorHasRegisterContribution(TrackingAssignment trackingAssignment){
        try{
            return contributionRecordRepository.findByTrackingAssignment(trackingAssignment);
        }catch (Exception e){
            throw e;
        }
    }

}































/*
    *Employee emp1 = new Employee(1, "AAA", "BBB", "A city");
            Employee emp2 = new Employee(2, "XXX", "ZZZ", "B city");

            List<Employee> empLst = new ArrayList<Employee>();
            empLst.add(emp1);
            empLst.add(emp2);

            //dynamic parameters required for report
            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("CompanyName", "TechGeekNext");
            empParams.put("employeeData", new JRBeanCollectionDataSource(empLst));

            JasperPrint empReport =
                    JasperFillManager.fillReport
                            (
                                    JasperCompileManager.compileReport(
                                            ResourceUtils.getFile("classpath:employees-details.jrxml")
                                                    .getAbsolutePath()) // path of the jasper report
                                    , empParams // dynamic parameters
                                    , new JREmptyDataSource()
                            );

            HttpHeaders headers = new HttpHeaders();
            //set the PDF format
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "employees-details.pdf");
            //create the report in PDF format
            return new ResponseEntity<byte[]>
                    (JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);
    * */
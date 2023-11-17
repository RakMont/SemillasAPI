package com.seedproject.seed.services;

import com.seedproject.seed.models.dao.ContributionRecordDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.dto.interfaces.ContributionReportDTO;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

    @Inject
    ExtraExpenseRepository extraExpenseRepository;
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
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId())));
            List<ContributionRecordReportDTO> contributionRecordReportDTOList = this.getSeedRecords(contributor.get());

            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_seed_description", "Fecha Inicio: "+
                    formatter.format(contributor.get().getActiveContribution().getConstantContribution().getContributionStartDate())+
                    " - Fecha Fin "+ formatter.format(contributor.get().getActiveContribution().getConstantContribution().getContributionEndDate()));
            empParams.put("label_seed_name", contributor.get().getUser().getName()+ " " + contributor.get().getUser().getLastname());
            empParams.put("label_title", "REPORTE: APORTES DE SEMILLA");
            empParams.put("label_seed_phone",contributor.get().getUser().getPhone());
            empParams.put("label_seed_id", contributor.get().getUser().getDni());
            empParams.put("label_seed_country",contributor.get().getCountry());
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));

            String resourceUtils = ResourceUtils.getFile("src/main/resources/templates/reports/report_seed_contributionsPDF.jrxml")
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

    private List<ContributionRecordReportDTO> getSeedRecords(Contributor contributor){
        List<ContributionReportDTO> contributionRecords =
                contributionRecordRepository.findContributionsBySeed(contributor.getContributor_id());
        List<ContributionRecordReportDTO> contributionRecordReportDTOS = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Map<String, Long> result = this.getSummarize(contributionRecords, contributor);

        int index = 0;

        for(ContributionReportDTO contributionRecord : contributionRecords){
            index++;
            contributionRecordReportDTOS.add(new ContributionRecordReportDTO(Integer.toString(index), contributionRecord, formatter));
        }

        ContributionRecordReportDTO line1= new ContributionRecordReportDTO();
        line1.setNro("");
        line1.setPayment_date("Total aportes obtenidos: ");
        line1.setPayment_amount(result.get("total"));
        line1.setExtra_amount(result.get("totalExtra"));
        line1.setSpent_amount(result.get("totalSpent"));
        line1.setReceipt_code("Total sumado:");
        line1.setValid_tr(result.get("totalSummed").toString());
        contributionRecordReportDTOS.add(line1);
        /*-----------------------------------------------------------------*/
        /*ContributionRecordReportDTO line2= new ContributionRecordReportDTO();
        line2.setNro("");
        line2.setPayment_date("Total niños alimentados: ");
        line2.setPayment_amount(result.get("total")/35);
        //line2.setExtra_amount(result.get("totalExtra"));
        //line2.setSpent_amount(result.get("totalSpent"));
        //line2.setReceipt_code("Total sumado:");
        line2.setValid_tr(String.valueOf(result.get("totalSummed")/35));
        contributionRecordReportDTOS.add(line2);*/
        /*-----------------------------------------------------------------*/
        ContributionRecordReportDTO line3= new ContributionRecordReportDTO();
        line3.setNro("");
        line3.setPayment_date("Total aportes esperados: ");
        line3.setPayment_amount(result.get("expectedAmount"));
        //line2.setExtra_amount(result.get("totalExtra"));
        //line2.setSpent_amount(result.get("totalSpent"));
        line3.setReceipt_code("Total aportes faltantes:");
        line3.setValid_tr(String.valueOf(result.get("leftAmount")));
        contributionRecordReportDTOS.add(line3);
        return  contributionRecordReportDTOS;
    }

    private  List<ContributionRecordReportDTO> getContributionsReportList(ContributionRecordFilter contributionRecordFilter){
        try{
            List<ContributionReportDTO> contributionReportDTOS =
                    contributionRecordRepository.findContributionRecord(contributionRecordFilter.getBeginDate(),contributionRecordFilter.getEndDate());

            List<ContributionRecordReportDTO> contributionRecordReportDTOS = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            int index = 0;
            Map<String, Long> result = this.getSummarize(contributionReportDTOS, null);

            for(ContributionReportDTO contributionRecord : contributionReportDTOS){
                index++;
                contributionRecordReportDTOS.add(new ContributionRecordReportDTO(Integer.toString(index), contributionRecord, formatter));
            }

            ContributionRecordReportDTO line1= new ContributionRecordReportDTO();
            line1.setNro("");
            line1.setPayment_date("Total aportes obtenidos: ");
            line1.setPayment_amount(result.get("total"));
            line1.setExtra_amount(result.get("totalExtra"));
            line1.setSpent_amount(result.get("totalSpent"));
            line1.setReceipt_code("Total sumado:");
            line1.setValid_tr(result.get("totalSummed").toString());
            contributionRecordReportDTOS.add(line1);


            ContributionRecordReportDTO line2= new ContributionRecordReportDTO();
            line2.setNro("");
            line2.setPayment_date("Total niños alimentados: ");
            line2.setPayment_amount(result.get("total")/35);
            //line2.setExtra_amount(result.get("totalExtra"));
            //line2.setSpent_amount(result.get("totalSpent"));
            //line2.setReceipt_code("Total sumado:");
            line2.setValid_tr(String.valueOf(result.get("totalSummed")/35));
            contributionRecordReportDTOS.add(line2);
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
            Optional<Contributor> contributor = contributorRepository.findById(Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getSeedId())));
            List<ContributionRecordReportDTO> contributionRecordReportDTOList = this.getSeedRecords(contributor.get());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Map<String, Object> empParams = new HashMap<String, Object>();
            empParams.put("label_seed_description", "Fecha Inicio: "+
                    formatter.format(contributor.get().getActiveContribution().getConstantContribution().getContributionStartDate())+
                    " - Fecha Fin "+ formatter.format(contributor.get().getActiveContribution().getConstantContribution().getContributionEndDate()));
            empParams.put("label_seed_name", contributor.get().getUser().getName()+ " " + contributor.get().getUser().getLastname());
            empParams.put("label_title", "REPORTE: APORTES DE SEMILLA");
            empParams.put("label_seed_phone",contributor.get().getUser().getPhone());
            empParams.put("label_seed_id", contributor.get().getUser().getDni());
            empParams.put("label_seed_country",contributor.get().getCountry());
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));

            String resourceUtils = ResourceUtils.getFile("src/main/resources/templates/reports/report_seed_contributionsCSV.jrxml")
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
            empParams.put("label_description", "Tus acciones demuestran de lo que está hecho tu corazón.");
            empParams.put("contribution_records", "fhgjgjhgj");
            empParams.put("label_title", "APORTES OBTENIDOS");
            empParams.put("label_dates", "F.Inicio: " +
                    formatter.format(contributionRecordFilter.getBeginDate()) +
                    " - F.Fin " +formatter.format(contributionRecordFilter.getEndDate()));
            empParams.put("label_payment_method",contributionRecordFilter.getPaymentMethod() != null ? contributionRecordFilter.getPaymentMethod() : "TODOS");
            empParams.put("total_extra", contributionRecordReportDTOS.get(contributionRecordReportDTOS.size()-1).getExtra_amount());
            empParams.put("label_contr_type", contributionRecordFilter.getContributionType() != null ? contributionRecordFilter.getContributionType() : "TODOS");

            String resourceUtils = ResourceUtils.getFile("src/main/resources/templates/reports/report_generalCSV.jrxml")
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
            empParams.put("label_description", "'Tus acciones demuestran de lo que está hecho tu corazón.'");
            empParams.put("contribution_records", "fhgjgjhgj");
            empParams.put("label_title", "APORTES OBTENIDOS");
            empParams.put("label_dates", "F.Inicio: " +
                    formatter.format(contributionRecordFilter.getBeginDate()) +
                    " - F.Fin " +formatter.format(contributionRecordFilter.getEndDate()));
            empParams.put("label_payment_method",contributionRecordFilter.getPaymentMethod() != null ? contributionRecordFilter.getPaymentMethod() : "TODOS");
            empParams.put("label_today_date", "Fecha: " + formatter.format(new Date()));
            empParams.put("label_contr_type", contributionRecordFilter.getContributionType() != null ? contributionRecordFilter.getContributionType() : "TODOS");

            String resourceUtils = ResourceUtils.getFile("src/main/resources/templates/reports/report_generalPDF.jrxml")
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

    public Table getAllDonations(ContributionRecordFilter contributionRecordFilter){
       try {
           List<ContributionReportDTO> contributionReportDTOS =
                   contributionRecordRepository.findContributionRecord(contributionRecordFilter.getBeginDate(),contributionRecordFilter.getEndDate());

           if (contributionRecordFilter.getVolunter_id() != null && !contributionRecordFilter.getVolunter_id().isEmpty()){
               Long id = Long.parseLong(encripttionService.decrypt(contributionRecordFilter.getVolunter_id()));
               contributionReportDTOS.removeIf(element ->
                       !element.getVolunteer_id().equals(id));
           }
           if(!(contributionRecordFilter.getContributionType() == null)) contributionReportDTOS.removeIf(element ->
                   !element.getContribution_key().equals(contributionRecordFilter.getContributionType()));


           if(!(contributionRecordFilter.getPaymentMethod() == null)) contributionReportDTOS.removeIf(element ->
                   !element.getPayment_method().equals(contributionRecordFilter.getPaymentMethod()));

           return this.getAllContributionsFormat(contributionReportDTOS);
       }catch (Exception exception){
           return null;
       }
    }

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
                                            contributionRecord.getContribution_key().equals(ContributionType.APORTE_CONSTANTE)
                                                    ? ColorCode.CONSTANT_CONTRIBUTION.value : ColorCode.UNIQUE_CONTRIBUTION.value
                                            ,false,null, null,
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
                    new CellHeader("Monto pago",0,"Integer",false,null),
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
                    new CellHeader("Monto Gasto",0,"Integer",false,null),
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
                    new CellHeader("Validado",0,"String",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributionRecord.getValid_transaction() ? "Sí" : "No",
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
                                            "description",ColorCode.VIEW_CONTR.value, true,
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
        Map<String, Long> result = this.getSummarize(contributionRecords, null);
        resultList.add(this.getFooter(result, true));
        resultList.add(this.getBenefitedChildren(result, true));
        return new Table(resultList);
    }


    public Map<String, Long> getSummarize(List<ContributionReportDTO> contributionRecords,Contributor contributor){
        Map<String, Long> resultParams = new HashMap<String, Long>();
        int total = 0;
        int totalExtra = 0;
        int totalSpent = 0;
        for (ContributionReportDTO contributionRecord: contributionRecords){
            total = total+contributionRecord.getPayment_amount().intValue();
            totalExtra = totalExtra + Integer.parseInt(contributionRecord.getExtra_amount());
            totalSpent = totalSpent + (contributionRecord.getSpent_amount() == null ?  0 :
                    Integer.parseInt(contributionRecord.getSpent_amount()));
        }
        resultParams.put("total", (long) total);
        resultParams.put("totalExtra", (long) totalExtra);
        resultParams.put("totalSpent", (long) totalSpent);
        resultParams.put("totalSummed", (long) total + totalExtra - totalSpent);
        if (contributor!= null){
            LocalDate startDate = LocalDate.ofInstant(contributor.getActiveContribution().getConstantContribution().getContributionStartDate().toInstant(), ZoneId.systemDefault());
            LocalDate endDate = LocalDate.ofInstant(contributor.getActiveContribution().getConstantContribution().getContributionEndDate().toInstant(), ZoneId.systemDefault());
            Long expectedAmount = contributor.getActiveContribution().getConstantContribution().getContribution().getContribution_amount();
            Long months = ChronoUnit.MONTHS.between(startDate, endDate);
            expectedAmount = expectedAmount * months;

            resultParams.put("expectedAmount", expectedAmount);
            resultParams.put("leftAmount", expectedAmount - total);
        }
        return resultParams;
    }

    public Table getSeedContributionRecords(String SeedId){
        Long id = Long.parseLong(encripttionService.decrypt(SeedId));
        try{
            Optional<Contributor> contributor = contributorRepository.findById(id);
            List<ContributionReportDTO> contributionRecords = contributionRecordRepository.findContributionsBySeed(id);
            if (!contributionRecords.isEmpty()) return this.getContributionsInFormat(contributionRecords, contributor.get());
            else return null;
        }catch (Exception exception){
            return null;
        }
    }
    public Table getContributionsInFormat(List<ContributionReportDTO> contributionRecords, Contributor contributor){
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
                    new CellHeader("Mes aporte Corr.",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributionRecord.getContribution_month() !=null ?
                                                    contributionRecord.getContribution_month().toString() : "-----", null)
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
                    new CellHeader("Monto pago",0,"Integer",false,null),
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
                    new CellHeader("Monto Gasto",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            contributionRecord.getSpent_amount() != null ? contributionRecord.getSpent_amount() : "0",null)
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
                    new CellHeader("Validado",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            contributionRecord.getValid_transaction() ? "Sí" : "No" ,
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
            if(index%12 == 0) resultList.add(getYearSeparator());
            index++;
        }

        Map<String, Long> result = this.getSummarize(contributionRecords, contributor);
        resultList.add(this.getFooter(result, false));
        //resultList.add(this.getBenefitedChildren(result, false));
        resultList.add(this.getExpectedContributions(result));
        return new Table(resultList);
    }

    public TableRow getExpectedContributions(Map<String, Long> summarize){
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
                    new CellHeader("Mes aporte Corr.",0,"String",true,null),
                    new CellProperty("#161d2b",false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            null, null)
                            )
                    )
            ));

        cells.add(new Cell(
                new CellHeader("Fecha pago",0,"String",true,null),
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
                new CellHeader("Fecha prevista",0,"String",true,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "Total aportes esperados", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto pago",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        summarize.get("expectedAmount") + " BOB",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto extra",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "--------",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto Gasto",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "--------",null)
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
                                        null,null, "Total aportes faltantes",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Validado",0,"String",true,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        summarize.get("leftAmount")+ " BOB",
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
    public TableRow getYearSeparator(){
        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(
                new CellHeader("#",0,"Integer",false,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,
                                        null,null,"A",null)
                        )
                )
        ));
        cells.add(new Cell(
                    new CellHeader("Mes aporte Corr.",0,"String",true,null),
                    new CellProperty("#dadbe1",false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            "Ñ", null)
                  ))));

        cells.add(new Cell(
                new CellHeader("Fecha pago",0,"String",true,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "O", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Fecha prevista",0,"String",true,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "D", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto pago",0,"Integer",false,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "E",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto extra",0,"Integer",false,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "A",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto Gasto",0,"Integer",false,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "P",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Codigo recibo",0,"String",true,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "O",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Validado",0,"String",true,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        "R",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Tipo aporte",0,"String",true,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "T",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Opciones",0,"String",false,null),
                new CellProperty("#dadbe1",false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null, "E",
                                        null)
                        )
                )
        ));
        return new TableRow(cells);
    }
    public TableRow getBenefitedChildren(Map<String, Long> summarize,Boolean isAll){
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
        if(isAll){
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
        }
        if(isAll){
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
        }
        if(!isAll){
            cells.add(new Cell(
                    new CellHeader("Mes aporte Corr.",0,"String",true,null),
                    new CellProperty("#161d2b",false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            null, null)
                            )
                    )
            ));
        }
        cells.add(new Cell(
                new CellHeader("Fecha pago",0,"String",true,null),
                new CellProperty(!isAll ? "#161d2b" : null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        !isAll ? "" : "Total niños alimentados", null)
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
                                        "Total niños alimentados", null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto pago",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        summarize.get("total")/35 + " Niños",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto extra",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "--------",null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Monto Gasto",0,"Integer",false,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",null,null,false,null,null,
                                        "--------",null)
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
                                        null,null, "--------",
                                        null)
                        )
                )
        ));
        cells.add(new Cell(
                new CellHeader("Validado",0,"String",true,null),
                new CellProperty(null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        (summarize.get("total")+
                                                summarize.get("totalExtra")-
                                                summarize.get("totalSpent"))/35 + " Niños",
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
        contributionRecord.setValidTransaction(contributionRecordDao.getValidTransaction());
        contributionRecord.setReceipt_code(contributionRecordDao.getReceipt_code());
        contributionRecord.setExtra_income_ammount(contributionRecordDao.getExtra_income_ammount());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_obtained());
        contributionRecord.setSent_payment_proof(contributionRecordDao.getSent_payment_proof());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_ammount() > 0);
        contributionRecord.setVolunter(volunter);
        contributionRecord.setContributionConfig(contributionConfig);
        contributionRecord.setTrackingAssignment(trackingAssignment);
        contributionRecord.setValidTransaction(contributionRecordDao.getValidTransaction());
        contributionRecord.setContributionMonth(contributionRecordDao.getContributionMonth());
        contributionRecord.setRegister_exist(true);
        if (contributionRecordDao.getHasExtraExpense()){
            ExtraExpense extraExpense = new ExtraExpense();
            extraExpense.setExtra_expense_amount(contributionRecordDao.getExtraExpenseAmount());
            extraExpense.setExtra_expense_reason(contributionRecordDao.getExtraExpenseReason());
            //extraExpense.setExtra_expense_date(new Date());
            contributionRecord.setExtraExpense(extraExpense);
        }
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
        contributionRecord.setValidTransaction(contributionRecordDao.getValidTransaction());
        contributionRecord.setReceipt_code(contributionRecordDao.getReceipt_code());
        contributionRecord.setExtra_income_ammount(contributionRecordDao.getExtra_income_ammount());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_obtained());
        contributionRecord.setSent_payment_proof(contributionRecordDao.getSent_payment_proof());
        contributionRecord.setValidTransaction(contributionRecordDao.getValidTransaction());
        contributionRecord.setContributionMonth(contributionRecordDao.getContributionMonth());
        if (contributionRecordDao.getHasExtraExpense()!= null){
           if(contributionRecord.getExtraExpense()!= null){
               contributionRecord.getExtraExpense().setExtra_expense_amount(contributionRecordDao.getExtraExpenseAmount());
               contributionRecord.getExtraExpense().setExtra_expense_reason(contributionRecordDao.getExtraExpenseReason());

           }else{
               ExtraExpense extraExpense = new ExtraExpense();
               extraExpense.setExtra_expense_amount(contributionRecordDao.getExtraExpenseAmount());
               extraExpense.setExtra_expense_reason(contributionRecordDao.getExtraExpenseReason());
               contributionRecord.setExtraExpense(extraExpense);
           }
        }
        else{
            contributionRecord.setExtraExpense(null);
        }

        try {
            contributionRecordRepository.save(contributionRecord);
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El aporte fue actualizado", ResponseStatus.SUCCESS), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error actualizando el aporte", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

        }
    }


    public ResponseEntity<RequestResponseMessage> deleteContributionRecord(String id){
        id = encripttionService.decrypt(id);
        try {
            contributionRecordRepository.deleteContributionRecord(Long.parseLong(id));
            return new ResponseEntity<>(new RequestResponseMessage(
                    "El aporte fue eliminado", ResponseStatus.SUCCESS), HttpStatus.CREATED);


        }catch (Exception exception){
            return new ResponseEntity<>(new RequestResponseMessage(
                    "Error eliminando el aporte", ResponseStatus.ERROR),HttpStatus.BAD_REQUEST);

        }
    }

    public TableRow getFooter(Map<String, Long> summarize, Boolean isAll){
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
            if(isAll){
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
            }
            if(isAll){
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
            }
            if(!isAll){
                cells.add(new Cell(
                        new CellHeader("Mes aporte Corr.",0,"String",true,null),
                        new CellProperty("#161d2b",false,null,null),
                        new ArrayList<CellContent>(
                                Arrays.asList(
                                        new CellContent("text",
                                                null,null,false,
                                                null,null,
                                                null, null)
                                )
                        )
                ));
            }
        cells.add(new Cell(
                new CellHeader("Fecha pago",0,"String",true,null),
                new CellProperty(!isAll ? "#161d2b" : null,false,null,null),
                new ArrayList<CellContent>(
                        Arrays.asList(
                                new CellContent("text",
                                        null,null,false,
                                        null,null,
                                        !isAll ? "" : "Total aportes", null)
                        )
                )
        ));
        if(!isAll){
            cells.add(new Cell(
                    new CellHeader("Fecha prevista",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            "Total aportes obtenidos", null)
                            )
                    )
            ));
        }
            cells.add(new Cell(
                    new CellHeader("Monto pago",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            summarize.get("total") + " BOB",null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto extra",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            summarize.get("totalExtra") + " BOB",null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Monto Gasto",0,"Integer",false,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",null,null,false,null,null,
                                            summarize.get("totalSpent") + " BOB",null)
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
                                            null,null,
                                            "Total sumado: ",
                                            null)
                            )
                    )
            ));
            cells.add(new Cell(
                    new CellHeader("Validado",0,"String",true,null),
                    new CellProperty(null,false,null,null),
                    new ArrayList<CellContent>(
                            Arrays.asList(
                                    new CellContent("text",
                                            null,null,false,
                                            null,null,
                                            summarize.get("total")+
                                                    summarize.get("totalExtra")-
                                                    summarize.get("totalSpent")+ " BOB",
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
                    new ArrayList<CellContent>(Arrays.asList(
                                        new CellContent("text",
                                                null,null,false,
                                                null,null, "",
                                                null)
                                )
                        )
                ));



        return new TableRow(cells);
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
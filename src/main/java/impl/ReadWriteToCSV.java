package impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadWriteToCSV {
    public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
        @Override
        public String[] generateHeader() {
            final int numColumns = findMaxFieldIndex();
            if (!isAnnotationDriven() || numColumns == -1) {
                return super.generateHeader();
            }

            header = new String[numColumns + 1];

            BeanField beanField;
            for (int i = 0; i <= numColumns; i++) {
                beanField = findField(i);
                String columnHeaderName = extractHeaderName(beanField);
                header[i] = columnHeaderName;
            }
            return header;
        }
    }


        private String extractHeaderName(final BeanField beanField) {
            if (beanField == null || beanField.getField() == null || beanField.getField().getDeclaredAnnotations().length == 0) {
                return StringUtils.EMPTY;
            }
            return beanField.getField().getName();
        }

    public void writeNomsToFile(String tagName, List<VCNomination> noms)  {
        String fileName = "src/main/resources/csv/noms%s.csv";
        fileName = String.format(fileName, tagName);




            Path myPath = Paths.get(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(myPath,
                    StandardCharsets.UTF_8)) {

                CustomMappingStrategy<VCNomination> mappingStrategy = new CustomMappingStrategy<>();
                mappingStrategy.setType(VCNomination.class);
                StatefulBeanToCsv<VCNomination> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withMappingStrategy(mappingStrategy)
                        .build();

                beanToCsv.write(noms);

            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException |
                    IOException ex) {
                Logger.getLogger(OpenCSVWriteBeansEx.class.getName()).log(
                        Level.SEVERE, ex.getMessage(), ex);
            }


    }

    public void writeRestrictionInfoToFile(String tagName, String text){
        String fileName = "src/main/resources/csv/restriction_%s.csv";
        fileName = String.format(fileName, tagName);



        Path myPath = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(myPath,
                StandardCharsets.UTF_8)) {

          writer.write(text);

        } catch (
                IOException ex) {

        }

    }
    public void writeMeterNetToFile(String tagName, List<MeterNomTracker> noms)  {
        String fileName = "src/main/resources/csv/meter%s.csv";
        fileName = String.format(fileName, tagName);




        Path myPath = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(myPath,
                StandardCharsets.UTF_8)) {

            CustomMappingStrategy<MeterNomTracker> mappingStrategy = new CustomMappingStrategy<>();
            mappingStrategy.setType(MeterNomTracker.class);
            StatefulBeanToCsv<MeterNomTracker> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            beanToCsv.write(noms);

        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException |
                IOException ex) {
            Logger.getLogger(OpenCSVWriteBeansEx.class.getName()).log(
                    Level.SEVERE, ex.getMessage(), ex);
        }


    }

    public List<VCNomination> buildNomsFromFile(String tagName ) {

        try {

            String fileName1 = "src/main/resources/csv/noms%s.csv";
            fileName1 = String.format(fileName1, tagName);


            Path myPath1 = Paths.get(fileName1);

            try (BufferedReader br = Files.newBufferedReader(myPath1,
                    StandardCharsets.UTF_8)) {

                HeaderColumnNameMappingStrategy<VCNomination> strategy
                        = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(VCNomination.class);

                CsvToBean csvToBean = new CsvToBeanBuilder(br)
                        .withType(VCNomination.class)
                        .withMappingStrategy(strategy)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                List<VCNomination> noms = csvToBean.parse();

                noms.forEach(System.out::println);
                return noms;
            }
        }catch (IOException ex){
            return null;
        }
        
    }
        public static void main (String[]args) throws IOException {

            String fileName = "src/main/resources/csv/cars_out.csv";
            Path myPath = Paths.get(fileName);

            try (BufferedReader br = Files.newBufferedReader(myPath,
                    StandardCharsets.UTF_8)) {

                HeaderColumnNameMappingStrategy<Car> strategy
                        = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(Car.class);

                CsvToBean csvToBean = new CsvToBeanBuilder(br)
                        .withType(Car.class)
                        .withMappingStrategy(strategy)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                List<Car> cars = csvToBean.parse();

                cars.forEach(System.out::println);
            }
        }

        public void createExcelFile(){

            try{

                File folder = new File("src/main/resources/csv");
                File[] listOfFiles = folder.listFiles();
                HSSFWorkbook workbook=new HSSFWorkbook();

                for (File file : listOfFiles) {


                    if (file.isFile()) {

                        String thisline;
                        ArrayList<String> al = null;
                        ArrayList<ArrayList<String>> arlist = new ArrayList<ArrayList<String>>();

                        HSSFSheet sheet =  workbook.createSheet(file.getName());
                        FileInputStream fis = new FileInputStream(file);
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                        int rowNumber = 0;
                        while ((thisline = br.readLine()) != null) {

                            String strar[] = thisline.split(",");

                            // create a row


                            HSSFRow row = sheet.createRow((short) rowNumber);

                            for (int j = 0; j < strar.length; j++) {

                                //for (int k = 0; k < arlist.size() ; k++) {

                                //  ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);


                                HSSFCell cell = row.createCell((short) j);
                                cell.setCellValue(strar[j]);


                            }
                            rowNumber++;


                        }





                        fis.close();
                        FileOutputStream fileOut = new FileOutputStream("src/main/resources/ValleyCrossingSchedulingModel.xls");
                        workbook.write(fileOut);
                        fileOut.flush();
                        fileOut.close();
                        br.close();
                    }
                }

                System.out.println("Your excel file has been generated!");

            } catch ( Exception ex ) {
                System.out.println(ex);

            }
        }
}
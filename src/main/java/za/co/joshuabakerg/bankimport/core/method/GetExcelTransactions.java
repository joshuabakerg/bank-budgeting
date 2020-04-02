package za.co.joshuabakerg.bankimport.core.method;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 04/03/2020
 */
@Component
public class GetExcelTransactions {

    public Collection<Transaction> execute(final byte[] bytes){
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            final XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            final XSSFSheet sheet = workbook.getSheetAt(0);

            final List<Transaction> transactions = new ArrayList<>();

            long rowNumber = 0;
            for (Row row : sheet) {
                rowNumber++;
                if (rowNumber == 1) continue;
                final Date date = row.getCell(0).getDateCellValue();
                final String type = row.getCell(1).getStringCellValue();
                final String description = row.getCell(2).getStringCellValue();
                final String reciever = row.getCell(3).getStringCellValue();
                final double amount = row.getCell(4).getNumericCellValue();

                if (description.startsWith("Epc")){
                    System.out.println();
                }

                final Transaction transaction = Transaction.builder()
                        .date(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .type(type)
                        .description(description)
                        .reciever(reciever)
                        .amount(BigDecimal.valueOf(amount))
                        .build();
                transaction.setId(transaction.hashCode());
                transactions.add(transaction);
            }
            return transactions;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class CreateUserWithPassword {
    static Connection conn;
    static Statement stmt;

    public static void main(String[] args) {
        generateMySQLUserFromExcel(new File("StudentList.xlsx"));
    }

    private static void generateMySQLUserFromExcel(File excelFile) {
        int totalRecords = 0, QRCodeGenrerated = 0, QRCodeNotGenerated = 0;
        // Specify the URL or text to generate QR Code
        String ID = null;
        String createUserQuery;
        String assignPrivilegesQuery = "GRANT ALL PRIVILEGES ON *.* TO '" + ID + "'@'%' WITH GRANT OPTION";
        initializeMySqlDB();

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(excelFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has data of " + workbook.getNumberOfSheets() + " Institutes ");

        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        System.out.println("Retrieving Students' ID");
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            System.out.println("Generating User for => " + sheet.getSheetName());
            DataFormatter dataFormatter = new DataFormatter();

            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Now let's iterate over the columns of the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = dataFormatter.formatCellValue(cell);
                    ID = cellValue;
                    createUserQuery = "CREATE USER '" + ID + "'@'%' IDENTIFIED BY '"+ ID + "';";
                    try {
                        stmt.execute(createUserQuery);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("User for " + ID + " Generated!!!");
                    totalRecords++;

                }
            }
        }
        System.out.println("Total Records: " + totalRecords);
        System.out.println("Total QRCodeGenerated: " + QRCodeGenrerated);
        System.out.println("Total QRCodeNotGenerated: " + QRCodeNotGenerated);
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean initializeMySqlDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_example", "root", "Abcd@1234");
            stmt = conn.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}

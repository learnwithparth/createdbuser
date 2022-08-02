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
        generateMySQLUserFromExcel(new File("StudentList1.xlsx"));
    }

    private static void generateMySQLUserFromExcel(File excelFile) {
        String ID = null;
        String createUserQuery, createDatabaseQuery, assignPrivilegesQuery;
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
                    createDatabaseQuery = "create database " + ID;
                    createUserQuery = "CREATE USER '" + ID + "'@'%' IDENTIFIED BY 'password'";
                    assignPrivilegesQuery = "GRANT ALL PRIVILEGES ON " + ID + ".* TO '" + ID + "'@'%' WITH GRANT OPTION";
                    try {
                        stmt.execute("drop database if exists " +ID);
                        stmt.execute("drop user if exists " + ID);
                        stmt.execute(createDatabaseQuery);
                        stmt.execute(createUserQuery);
                        stmt.execute(assignPrivilegesQuery);
                        System.out.println("Database created with user id : " + ID +" password : password" );
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                }
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean initializeMySqlDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://172.16.12.254:3306/", "root", "Mysql@123");
            stmt = conn.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}

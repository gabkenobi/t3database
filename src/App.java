import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        T3Database db = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to T3 DataBase");
        System.out.println();

        while (db == null) {
            try {
                db = new T3Database();
            } catch (SQLException e) {
                System.out.println(e.getMessage());

                System.out.println("Try again? y/n");
                char tryAgain = scanner.nextLine().charAt(0);

                if (tryAgain == 'n' || tryAgain == 'N') {
                    System.exit(0);
                }
            }
        }
        // conexão
        System.out.println("Connected");
        System.out.println();

        boolean exitProgram = false;

        while (!exitProgram) {
            System.out.println("Select a option: ");
            System.out.println("-----------------");
            System.out.println("C - Create");
            System.out.println("R - Read");
            System.out.println("U - Update");
            System.out.println("D - Delete");
            System.out.println("S - Search");
            System.out.println("0 - Exit");
            System.out.println("-----------------");
            System.out.println();

            String option = scanner.nextLine();

            if (option.equalsIgnoreCase("c")) {
                db.insertOperation();
            } else if (option.equalsIgnoreCase("r")) {
                db.selectOperation();
            } else if (option.equalsIgnoreCase("u")) {
                db.updateOperation();
            } else if (option.equalsIgnoreCase("d")) {
                db.deleteOperation();
            } else if (option.equalsIgnoreCase("s")) {
                db.searchOperation();
            } else if (option.equalsIgnoreCase("0")) {
                exitProgram = true;
            } else {
                System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}

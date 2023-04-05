import java.sql.*;
import java.util.Scanner;

public class T3Database {
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private PreparedStatement insertStatement;
	private PreparedStatement selectStatement;
	private PreparedStatement selectUserStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private PreparedStatement countStatement;
	private PreparedStatement searchByNameStatement;

	public T3Database() throws SQLException {
		String url = "jdbc:postgresql://localhost:5432/my_db";

		System.out.print("Enter admin username: ");
		String adminUsername = scanner.nextLine();

		System.out.print("Enter admin password: ");
		String password = scanner.nextLine();

		conn = DriverManager.getConnection(url, adminUsername, password);
		insertStatement = conn.prepareStatement("INSERT INTO customers VALUES (?, ?, ?, ?, ?)");
		selectStatement = conn.prepareStatement("SELECT * FROM customers LIMIT 5 OFFSET ?");
		selectUserStatement = conn.prepareStatement("SELECT * FROM customers WHERE username = ?");
		updateStatement = conn.prepareStatement(
				"UPDATE customers SET username = ?, fullname = ?, email = ?, phone = ?, age = ? WHERE username = ?");
		deleteStatement = conn.prepareStatement("DELETE FROM customers WHERE username = ?");
		countStatement = conn.prepareStatement("SELECT count(*) FROM customers");
		searchByNameStatement = conn
				.prepareStatement("SELECT * FROM customers WHERE LOWER(fullname) LIKE LOWER(?)");
	}

	public void insertOperation() {
		System.out.println("Enter customer info");

		try {
			System.out.print("Enter customer username: ");
			insertStatement.setString(1, scanner.nextLine());

			System.out.print("Enter customer full name: ");
			insertStatement.setString(2, scanner.nextLine());

			System.out.print("Enter customer e-mail: ");
			insertStatement.setString(3, scanner.nextLine());

			System.out.print("Enter customer phone: ");
			insertStatement.setString(4, scanner.nextLine());

			System.out.print("Enter customer age: ");
			insertStatement.setInt(5, scanner.nextInt());

			insertStatement.executeUpdate();
			System.out.println("User added successfully!");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void selectOperation() {
		System.out.println("List of customers:");

		int totalOfCustomers = getTotalOfCustomers();
		int totalOfPages = getPageTotal(totalOfCustomers);

		System.out.printf("Total of customers: %s\n", totalOfCustomers);

		boolean shouldQuitListing = false;
		int currentPage = 0;

		while (!shouldQuitListing) {
			try {
				selectStatement.setInt(1, 5 * currentPage);
				ResultSet tableResult = selectStatement.executeQuery();
				printTableRows(tableResult);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			System.out.println("Page: " + (currentPage + 1) + "/" + totalOfPages);

			if (currentPage > 0) {
				System.out.print("p to go to the previous page, ");
			}

			if (currentPage + 1 < totalOfPages) {
				System.out.print("n to go to the next page, ");
			}

			System.out.print("q to quit listing: ");
			String input = scanner.nextLine();

			if (input.equalsIgnoreCase("q")) {
				shouldQuitListing = true;
			} else if (input.equalsIgnoreCase("p")) {
				if (currentPage > 0) {
					currentPage -= 1;
				} else {
					System.out.println("Invalid input");
				}
			} else if (input.equalsIgnoreCase("n")) {
				if (currentPage + 1 < totalOfPages) {
					currentPage += 1;
				} else {
					System.out.println("Invalid input");
				}
			} else {
				System.out.println("Invalid input");
			}
		}
	}

	public void updateOperation() {
		System.out.print("Enter the username of the customer to be updated: ");
		String username = null;
		Customer customerToUpdate = null;

		try {
			username = scanner.nextLine();
			selectUserStatement.setString(1, username);
			ResultSet customerInfo = selectUserStatement.executeQuery();

			if (customerInfo.next()) {
				customerToUpdate = new Customer(
						customerInfo.getString(1).trim(),
						customerInfo.getString(2).trim(),
						customerInfo.getString(3).trim(),
						customerInfo.getString(4).trim(),
						customerInfo.getInt(5));

				System.out.println(customerToUpdate.toString());
			} else {
				System.out.printf("%s doesn't exist. Aborting.\n", username);
				return;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		Boolean shouldGetNextInfo;

		System.out.println("Enter the new information. Leave blank to keep current.");
		System.out.println();

		System.out.printf("New username (%s): ", customerToUpdate.getUsername());
		shouldGetNextInfo = false;

		while (!shouldGetNextInfo) {
			try {
				String newUsername = scanner.nextLine();

				if (!newUsername.equals("")) {
					customerToUpdate.setUsername(newUsername);
				}

				shouldGetNextInfo = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.printf("New full name (%s): ", customerToUpdate.getFullName());

		shouldGetNextInfo = false;

		while (!shouldGetNextInfo) {
			try {
				String newFullName = scanner.nextLine();

				if (!newFullName.equals("")) {
					customerToUpdate.setFullName(newFullName);
				}

				shouldGetNextInfo = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.printf("New email (%s): ", customerToUpdate.getEmail());

		shouldGetNextInfo = false;

		while (!shouldGetNextInfo) {
			try {
				String newEmail = scanner.nextLine();

				if (!newEmail.equals("")) {
					customerToUpdate.setEmail(newEmail);
				}

				shouldGetNextInfo = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.printf("New phone (%s): ", customerToUpdate.getPhone());

		shouldGetNextInfo = false;

		while (!shouldGetNextInfo) {
			try {
				String newPhone = scanner.nextLine();

				if (!newPhone.equals("")) {
					customerToUpdate.setPhone(newPhone);
				}

				shouldGetNextInfo = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.printf("New age (%d): ", customerToUpdate.getAge());

		shouldGetNextInfo = false;

		while (!shouldGetNextInfo) {
			try {
				String newAge = scanner.nextLine();

				if (!newAge.equals("")) {
					customerToUpdate.setAge(Integer.parseInt(newAge));
				}

				shouldGetNextInfo = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.printf("Confirm changes to %s? (y/n): ", username);

		try {
			String option = scanner.nextLine();

			if (option.equalsIgnoreCase("y")) {
				updateStatement.setString(1, customerToUpdate.getUsername());
				updateStatement.setString(2, customerToUpdate.getFullName());
				updateStatement.setString(3, customerToUpdate.getEmail());
				updateStatement.setString(4, customerToUpdate.getPhone());
				updateStatement.setInt(5, customerToUpdate.getAge());
				updateStatement.setString(6, username);

				int results = updateStatement.executeUpdate();

				if (results == 1) {
					System.out.printf("%s updated.\n", username);
				}
			} else {
				System.out.println("Update Canceled");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteOperation() {
		System.out.println("Enter the username of the customer to be deleted: ");

		try {
			String username = scanner.nextLine();
			deleteStatement.setString(1, username);

			int results = deleteStatement.executeUpdate();

			if (results == 1) {
				System.out.printf("%s was deleted.\n", username);
			} else {
				System.out.printf("%s doesn't exist. Aborting.\n", username);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public int getTotalOfCustomers() {
		int totalOfCustomers = 0;

		try {
			ResultSet countResult = countStatement.executeQuery();
			if (countResult.next()) {
				totalOfCustomers = countResult.getInt(1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return totalOfCustomers;
	}

	public static int getPageTotal(int totalOfCustomers) {
		int pages = 0;
		pages = totalOfCustomers / 5;

		if (totalOfCustomers % 5 != 0) {
			pages += 1;
		}

		return pages;
	}

	public void searchOperation() {
		try {
			System.out.println("Type your search: ");
			String searchValue = scanner.nextLine();
			searchByNameStatement.setString(1, "%" + searchValue + "%");
			ResultSet searchResults = searchByNameStatement.executeQuery();
			printTableRows(searchResults);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void printTableRows(ResultSet table) throws SQLException {
		while (table.next()) {
			Customer c = new Customer(
					table.getString(1).trim(),
					table.getString(2).trim(),
					table.getString(3).trim(),
					table.getString(4).trim(),
					table.getInt(5));
			System.out.println(c.toString());
		}
	}

}

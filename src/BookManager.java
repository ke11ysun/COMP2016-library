import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

//	TODO How to build path by coding

public class BookManager {
	MyFrame frame;

	Scanner in = null;
	Connection conn = null;
	int localPordId = 1521; // the port number to connect database, do not
							// change the port
	int defaultForwardPort = 9001; // if 9001 is occupied by other process,
									// change it (see tutorial)
	String host = "jumbo";

	String[] options = { "book search (by ISBN)", "book borrow (by ISBN)", "book return (by ISBN)",
			"book renew (by ISBN)", "book reserve (by ISBN)", "print book info (by ISBN)",
			"print student reservation (by sid)" };

	// File sqlFile = new File(".\\sql\\SQL code 1.2.txt");
	File sqlFile = new File(".\\sql\\sql.txt");
	File dataFile = new File(".\\sql\\data.txt");

	/*
	 * Login the oracle system. Do not change this function
	 * 
	 * @return boolean
	 */
	public boolean login() {
		String username = null, password = null, URL = null;

		// username = replace_with_your_jumbo_username, e.g., e12345678;
		username = JOptionPane.showInputDialog(null, "Please input SQL Username: ", "e2250201");
		if (username == null) {
			System.exit(1);
		}

		// password = replace_with_your_jumbo_password, e.g., e12345678;
		password = askPassword();
		if (password == null) {
			System.exit(1);
		}

		URL = "jdbc:oracle:thin:@" + host + ":" + localPordId + ":oracle10";
		try {
			System.out.println("logining...");
			conn = DriverManager.getConnection(URL, username, password);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			// frame.printTuple(tmp);
			JOptionPane.showMessageDialog(null, tmp, "Error in Log in", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private String askPassword() {

		JPanel panel = new JPanel();
		JLabel label = new JLabel("Please input SQL Password: ");
		JPasswordField pwd = new JPasswordField();
		pwd.setPreferredSize(new Dimension(100, 28));
		panel.add(label);
		panel.add(pwd);
		int okOrCancel = JOptionPane.showConfirmDialog(null, panel, "Enter Password", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (okOrCancel == JOptionPane.OK_OPTION) {
			String password = new String(pwd.getPassword());
			return password;
		} else {
			return null;
		}

	}

	/*
	 * Show the options.
	 */
	public void showOptions() { // TODO
		System.out.println("Please choose following option:");
		for (int i = 0; i < options.length; ++i) {
			System.out.println("(" + (i + 1) + ") " + options[i]);
		}
	}

	/**
	 * Print out the information of a book given an ISBN
	 * 
	 * @param ISBN
	 */
	private void printBookInfo(String ISBN) {// used in bookSearch

		try {
			ArrayList<String> list = new ArrayList<String>();

			Statement stm = conn.createStatement();
			String sql = "SELECT * FROM BOOK WHERE ISBN = '" + ISBN + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			String[] heads = { "ISBN", "Title", "Author", "Amount_available", "Location" };
			// String tmp = "";
			for (int i = 0; i < heads.length; i++) { // BOOK table 5 attributes

				list.add(heads[i] + ": " + rs.getString(i + 1) + " ");

				// tmp += (heads[i] + ": " + rs.getString(i + 1) + " "); //
				// attribute
				// // id
				// // starts
				// // with
				// // 1

			}
			frame.printTuple(list);
			rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
		} finally {

		}
	}

	/**
	 * List all borrow records in the database.
	 */
	private void listAllBorrow() {
		System.out.println("All borrow records in the database now:");
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT SID, ISBN FROM BORROWS";
			ResultSet rs = stm.executeQuery(sql);

			frame.printTuple(convertResultToList(rs));

			// int resultCount = 0;
			// // attribute name, need formation
			// System.out.printf("%15", "student id", "%15", "borrowed ISBN");
			// System.out.println("-----------------");
			// while (rs.next()) {
			// System.out.printf("%15", rs.getString(1), "%15",
			// rs.getString(2));// need
			// // formation
			// ++resultCount;
			// }
			// System.out.println("Total " + resultCount + " borrow
			// record(s).");
			// rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "   Code: " + e.getErrorCode() + "   Message: "
					+ e.getMessage());
			frame.printTuple(tmp);
		}
	}

	// private void listAllUnborrow() {}

	/**
	 * List all reserve records in the database.
	 */
	private void listAllReserve() {
		System.out.println("All reserve records in the database now:");
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT SID, ISBN FROM STUDENT";
			ResultSet rs = stm.executeQuery(sql);

			frame.printTuple(convertResultToList(rs));

			// int resultCount = 0;
			// // attribute name, need formation
			// System.out.printf("%15", "student id", "%15", "reserved ISBN");
			// System.out.println("------------------------------");
			// while (rs.next()) {
			// System.out.printf("%15", rs.getString(1), "%15",
			// rs.getString(2));// need
			// // formation
			// ++resultCount;
			// }
			// System.out.println("Total " + resultCount + " reserve
			// record(s).");
			// rs.close();

			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "   Code: " + e.getErrorCode() + "   Message: "
					+ e.getMessage());
			frame.printTuple(tmp);
		}
	}

	// private void listAllUnreserve() {}

	/**
	 * Select out a borrowed book according to the ISBN.
	 * 
	 * @throws SQLException
	 */
	private void printBorrowsInfo(String line) {
		// listAllBorrow(); // ???
		System.out.println("Please input the ISBN to print info:");
		// String line = in.nextLine();
		line = line.trim();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// printBookInfo(line);

		try {

			Statement stmt = conn.createStatement();
			stmt.executeQuery("SELECT * FROM BORROWS WHERE ISBN = '" + line + "'");

			// Result print
			ResultSet rs = stmt.getResultSet();
			frame.printTuple(convertResultToList(rs));

			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
		}

	}

	private void printReserveInfo(String line) {

		// listAllBorrow(); // ???
		System.out.println("Please input the ISBN to print info:");
		// String line = in.nextLine();
		line = line.trim();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// printBookInfo(line);

		try {

			Statement stmt = conn.createStatement();
			stmt.executeQuery("SELECT * FROM STUDENT WHERE ISBN = '" + line + "'");

			// Result print
			ResultSet rs = stmt.getResultSet();
			frame.printTuple(convertResultToList(rs));

			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
		}

	}
	// private void printUnborrowInfo() {} private boolean isBorrowed(){} //no
	// need, included in trigger
	// private void printUnreserveInfo() {} private boolean isReserved(){}

	private void bookSearch(String line) {
		int amountAvailable = 0;
		System.out.println("Please input ISBN you want to search: ");
		// String line = in.nextLine();
		line = line.trim(); // ISBN
		// if (line.equalsIgnoreCase("exit"))
		// return;
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT AMOUNT_AVAILABLE FROM BOOK WHERE ISBN = '" + line + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			amountAvailable = Integer.parseInt(rs.getString(1));
			rs.close();
			stm.close();
			if (amountAvailable > 0)
				printBookInfo(line);
			else {
				printBookInfo(line);
				// System.out.println("No copy of this book is available!");
				JOptionPane.showMessageDialog(null, "No copy of this book is available!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
		}
	}

	private void bookBorrow(String sid, String ISBN, String date, String due) throws ParseException {
		System.out.println("Please input your SID, ISBN you want to borrow, BORROW DATE (yyyy-mm-dd): ");
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// String[] values = line.split(",");
		String[] values = { ISBN, sid, date, due };
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();

		// SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		String dstr = values[2];
		Date borrowDate = new SimpleDateFormat("yyyy-MM-dd").parse(dstr);
		// Date borrowDate = sdf.parse(dstr);
		// GregorianCalendar gc = new GregorianCalendar();
		// gc.setTime(borrowDate);
		// gc.add(3, +4);
		// gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
		// gc.get(Calendar.DATE));
		Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[3]);

		try {
			ArrayList<String> list = new ArrayList<String>();

			Statement stm = conn.createStatement();
			String sql = "BEGIN\nborrow_before_constraint('" + values[0] + "', '" + values[1] + "', '"
					+ new SimpleDateFormat("dd-MMM-yy").format(borrowDate) + "', '"
					+ new SimpleDateFormat("dd-MMM-yy").format(dueDate) + "', 0);\nEND;";
			// String sql = "INSERT INTO BORROWS VALUES('" + values[0] + "', '"
			// + values[1] + "', '"
			// + new SimpleDateFormat("dd-MMM-yy").format(borrowDate) + "', '"
			// + new SimpleDateFormat("dd-MMM-yy").format(dueDate) + "', 0)";
			int hasResults = stm.executeUpdate(sql);// the error info is
													// contained in the
			// trigger, when error happens it jumps to
			// catch (?)
			System.out.println("Borrow succeeded.");
			JOptionPane.showMessageDialog(null, "Borrow succeeded.");

			ResultSet rs = stm.getResultSet();
			if (hasResults > 0 && rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int cols = md.getColumnCount();
				String tmp = "";
				for (int i = 1; i <= cols; i++) {
					String name = md.getColumnLabel(i);
					tmp += (name + "   ");
				}
				list.add(tmp);
				list.add(new String(new char[tmp.length()]).replace("\0", "="));

				while (rs.next()) {
					String tmp2 = "";
					for (int i = 1; i <= cols; i++) {
						String value = rs.getString(i);
						tmp2 += (value + "   ");
					}
					list.add(tmp2);
				}
			}

			if (rs != null) {
				rs.close();
			}

			frame.printTuple(list);

			stm.close();
		} catch (SQLException e) {
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
			JOptionPane.showMessageDialog(null, "Error Message: " + e.getMessage(), "SQL Error",
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}
	}

	private void bookBorrow(String sid, String ISBN, String date) throws ParseException {
		System.out.println("Please input your SID, ISBN you want to borrow, BORROW DATE (yyyy-mm-dd): ");
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// String[] values = line.split(",");
		String[] values = { sid, ISBN, date };
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();

		// SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		String dstr = values[2];
		Date borrowDate = new SimpleDateFormat("yyyy-MM-dd").parse(dstr);
		// Date borrowDate = sdf.parse(dstr);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(borrowDate);
		gc.add(3, +4);
		gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
		Date dueDate = gc.getTime();

		bookBorrow(sid, ISBN, date, new SimpleDateFormat("yyyy-MM-dd").format(dueDate));

		// try {
		// Statement stm = conn.createStatement();
		// String sql = "BEGIN\nborrow_before_constraint('" + values[0] + "', '"
		// + values[1] + "', '"
		// + new SimpleDateFormat("dd-MMM-yy").format(borrowDate) + "', '"
		// + new SimpleDateFormat("dd-MMM-yy").format(dueDate) + "', 0);\nEND;";
		//// String sql = "INSERT INTO BORROWS VALUES('" + values[0] + "', '" +
		// values[1] + "', '"
		//// + new SimpleDateFormat("dd-MMM-yy").format(borrowDate) + "', '"
		//// + new SimpleDateFormat("dd-MMM-yy").format(dueDate) + "', 0)";
		// stm.executeUpdate(sql);// the error info is contained in the
		// // trigger, when error happens it jumps to
		// // catch (?)
		// System.out.println("Borrow succeeded.");
		// JOptionPane.showMessageDialog(null, "Borrow succeeded.");
		//
		// stm.close();
		// } catch (SQLException e) {
		// String tmp = ("State: " + e.getSQLState() + " Code: " +
		// e.getErrorCode() + " Message: " + e.getMessage());
		// frame.printTuple(tmp);
		// e.printStackTrace();
		// }
	}

	private void bookReturn(String sid, String ISBN) {
		System.out.println("Please input your SID, ISBN you want to return: ");
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// String[] values = line.split(",");
		String[] values = { sid, ISBN };
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();
		try {
			ArrayList<String> list = new ArrayList<String>();

			Statement stm = conn.createStatement();
			String sql = "DELETE FROM BORROWS WHERE SID = '" + values[0] + "' AND ISBN = '" + values[1] + "'";
			int hasResults = stm.executeUpdate(sql);
			System.out.println("Return succeeded.");
			JOptionPane.showMessageDialog(null, "Return succeeded.");

			ResultSet rs = stm.getResultSet();
			if (hasResults > 0 && rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int cols = md.getColumnCount();
				String tmp = "";
				for (int i = 1; i <= cols; i++) {
					String name = md.getColumnLabel(i);
					tmp += (name + "   ");
				}
				list.add(tmp);
				list.add(new String(new char[tmp.length()]).replace("\0", "="));

				while (rs.next()) {
					String tmp2 = "";
					for (int i = 1; i <= cols; i++) {
						String value = rs.getString(i);
						tmp2 += (value + "   ");
					}
					list.add(tmp2);
				}
			}

			if (rs != null) {
				rs.close();
			}

			frame.printTuple(list);

			stm.close();
		} catch (SQLException e) {
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
			e.printStackTrace();
		}
	}

	private void bookRenew(String sid, String ISBN) throws ParseException {
		System.out.println("Please input your SID, ISBN you want to renew: ");
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// String[] values = line.split(",");
		String[] values = { sid, ISBN };
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();

		try {
			ArrayList<String> list = new ArrayList<String>();

			Statement stm = conn.createStatement();
			// String getDueDate = "SELECT DUE_DATE FROM BORROWS WHERE SID = '"
			// + values[0] + "' AND ISBN = '" + values[1]
			// + "'";
			// ResultSet rs = stm.executeQuery(getDueDate);
			// if (!rs.next())
			// return;
			// String od = rs.getString(1);
			// Date originalDue = new SimpleDateFormat("dd-MMM-yy").parse(od);
			// // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// // Date originalDue = sdf.parse(od);
			// GregorianCalendar gc = new GregorianCalendar();
			// gc.setTime(originalDue);
			// gc.add(3, +2);
			// gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
			// gc.get(Calendar.DATE));
			// Date renewDate = gc.getTime();

			String sql = "BEGIN\nrenew_constraint('" + values[1] + "', '" + values[0] + "');\nEND;";
			int hasResults = stm.executeUpdate(sql);
			System.out.println("Renew succeeded.");
			JOptionPane.showMessageDialog(null, "Renew succeeded.");

			ResultSet rs = stm.getResultSet();
			if (hasResults > 0 && rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int cols = md.getColumnCount();
				String tmp = "";
				for (int i = 1; i <= cols; i++) {
					String name = md.getColumnLabel(i);
					tmp += (name + "   ");
				}
				list.add(tmp);
				list.add(new String(new char[tmp.length()]).replace("\0", "="));

				while (rs.next()) {
					String tmp2 = "";
					for (int i = 1; i <= cols; i++) {
						String value = rs.getString(i);
						tmp2 += (value + "   ");
					}
					list.add(tmp2);
				}
			}

			if (rs != null) {
				rs.close();
			}

			frame.printTuple(list);

			// rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "  Code: " + e.getErrorCode() + "  Message: " + e.getMessage());
			frame.printTuple(tmp);
			JOptionPane.showMessageDialog(null, "Error Message: " + e.getMessage(), "SQL Error",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void bookReserve(String sid, String ISBN, String date) throws ParseException {
		System.out.println("Please input your SID, ISBN you want to reserve, RESERVE DATE (yyyy-MM-dd): ");
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// String[] values = line.split(",");
		String[] values = { sid, ISBN, date };
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i].trim();
		String dstr = values[2];
		Date reserveDate = new SimpleDateFormat("yyyy-MM-dd").parse(dstr);
		try {
			ArrayList<String> list = new ArrayList<String>();

			Statement stm = conn.createStatement();
			String sql = "BEGIN\nreserve_constraint('" + values[1] + "', '" + values[0] + "', '"
					+ new SimpleDateFormat("dd-MMM-yy").format(reserveDate) + "');\nEND;";
			// String sql = "UPDATE STUDENT SET ISBN = '" + values[1] + "' ,
			// RESERVE_DATE = '"
			// + new SimpleDateFormat("dd-MMM-yy").format(reserveDate) + "'
			// WHERE SID = '" + values[0] + "'";
			int hasResults = stm.executeUpdate(sql);
			System.out.println("Reserve succeeded.");
			JOptionPane.showMessageDialog(null, "Reserve succeeded.");

			ResultSet rs = stm.getResultSet();
			if (hasResults > 0 && rs != null) {
				ResultSetMetaData md = rs.getMetaData();
				int cols = md.getColumnCount();
				String tmp = "";
				for (int i = 1; i <= cols; i++) {
					String name = md.getColumnLabel(i);
					tmp += (name + "   ");
				}
				list.add(tmp);
				list.add(new String(new char[tmp.length()]).replace("\0", "="));

				while (rs.next()) {
					String tmp2 = "";
					for (int i = 1; i <= cols; i++) {
						String value = rs.getString(i);
						tmp2 += (value + "   ");
					}
					list.add(tmp2);
				}
			}

			if (rs != null) {
				rs.close();
			}

			frame.printTuple(list);

			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "   Code: " + e.getErrorCode() + "   Message: "
					+ e.getMessage());
			frame.printTuple(tmp);
			JOptionPane.showMessageDialog(null, "Error Message: " + e.getMessage(), "SQL Error",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	/**
	 * Close the manager. Do not change this function.
	 */
	public void close() {
		System.out.println("Thanks for using this manager! 88...");
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String tmp = ("State: " + e.getSQLState() + "Code: " + e.getErrorCode() + "Message: " + e.getMessage());
			frame.printTuple(tmp);
		}
	}

	void askSQL() throws HeadlessException {

		try {
			String msg = "Please  select a SQL file or input the file path:";

			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(400, 70));
			JLabel label = new JLabel(msg);
			JTextField path = new JTextField(sqlFile.getCanonicalPath());
			path.setPreferredSize(new Dimension(399, 28));
			panel.add(label);
			panel.add(path);

			String[] options = new String[] { "Select file", "OK", "Cancel" };

			int response = JOptionPane.showOptionDialog(null, panel, "Import set up files", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			// Where response == 0 for Yes, 1 for No, 2 for Maybe.

			if (response == 0) {

				// Create a file chooser
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // FILES_AND_DIRECTORIES
																		// &
																		// The
																		// default
																		// is
																		// FILES_ONLY.
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to read the file: " + chooser.getSelectedFile().getName());
					File f = chooser.getSelectedFile();

					if (importSQLFile(f)) {

						JOptionPane.showMessageDialog(null, "The SQL script read successfully.");

					}

				}

			} else if (response == 1) {

				if (path.getText() != null && !path.getText().trim().equalsIgnoreCase("")) {

					if (importSQLFile(new File(path.getText()))) {

						JOptionPane.showMessageDialog(null, "The SQL script read successfully.");

					}

				}

			} else if (response == 2) {

				//
				//
			}
		} catch (SQLException e) {
			System.err.println("Failed to Execute" + sqlFile + ". The error is" + e.getMessage());
			JOptionPane.showMessageDialog(null, "Failed to Execute" + sqlFile + ". The error is" + e.getMessage());
			String tmp = ("State: " + e.getSQLState() + "   Code: " + e.getErrorCode() + "   Message: "
					+ e.getMessage());
			frame.printTuple(tmp);
			e.printStackTrace();
		} catch (IOException ex) {
			System.out.println("The file does not exist.");
			JOptionPane.showMessageDialog(null, "The file does not exist.");
			ex.printStackTrace();

		}
	}

	// String sqlPath = JOptionPane.showInputDialog(null, "Please input SQL file
	// Path: ", sqlFile.getCanonicalPath());
	// // System.out.println(sqlFile.getCanonicalPath());
	// if (sqlPath == null || sqlPath.trim().equalsIgnoreCase("")) {
	// //
	// } else if ((sqlFile = new File(sqlPath)).exists()) {
	// try {
	// if (importSQLFile(sqlFile)) {
	// System.out.println("Create tables succeeded.");
	// JOptionPane.showMessageDialog(null, "Create tables succeeded.");
	//
	// } else {
	// System.err.println("Failed to Execute" + sqlFile + ".");
	// JOptionPane.showMessageDialog(null, "Failed to Execute" + sqlFile + ".");
	// }
	// } catch (SQLException e) {
	// System.err.println("Failed to Execute" + sqlFile + ". The error is" +
	// e.getMessage());
	// JOptionPane.showMessageDialog(null, "Failed to Execute" + sqlFile + ".
	// The error is" + e.getMessage());
	// String tmp = ("State: " + e.getSQLState() + "Code: " + e.getErrorCode() +
	// "Message: " + e.getMessage());
	// frame.printTuple(tmp);
	// e.printStackTrace();
	// }
	// } else {
	// System.out.println("The file does not exist.");
	// JOptionPane.showMessageDialog(null, "The file does not exist.");
	// }
	//
	// }

	// private void importSQLFile 1() throws IOException {
	// // 1)
	// try {
	// Statement stm = conn.createStatement();
	// String sql = "@" + new File("..").getCanonicalPath() + "\\sql\\SQL
	// code.sql";
	//
	// // stm.executeUpdate(sql);
	// System.out.println("Create tables succeeded.");
	// JOptionPane.showMessageDialog(null, "Create tables succeeded.");
	//
	// stm.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// String tmp = ("State: " + e.getSQLState() + "Code: " + e.getErrorCode() +
	// "Message: " + e.getMessage());
	// frame.printTuple(tmp);
	// }
	//
	// // 2) Use FileReader
	//
	// }

	ArrayList<String> convertResultToList(ResultSet rs) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();

		if (rs != null) {
			ResultSetMetaData md = rs.getMetaData();
			int cols = md.getColumnCount();
			String tmp = "";
			for (int i = 1; i <= cols; i++) {
				String name = md.getColumnLabel(i);
				tmp += (name + "   ");
			}
			list.add(tmp);
			list.add(new String(new char[tmp.length()]).replace("\0", "="));

			while (rs.next()) {
				String tmp2 = "";
				for (int i = 1; i <= cols; i++) {
					String value = rs.getString(i);
					tmp2 += (value + "   ");
				}
				list.add(tmp2);
			}
		}

		if (rs != null) {
			rs.close();
		}

		return list;
	}

	boolean importSQLFile(File aSQLScriptFilePath) throws IOException, SQLException {
		boolean isScriptExecuted = false;

		BufferedReader in = new BufferedReader(new FileReader(aSQLScriptFilePath));
		String str;
		StringBuffer sb = new StringBuffer();
		ArrayList<String> list = new ArrayList<String>();
		while ((str = in.readLine()) != null) {
			str = str.trim();

			if (str.equalsIgnoreCase("--")) {
				Statement stmt = conn.createStatement();
				boolean hasResults = false;

				System.out.println(sb.toString());
				list.add(sb.toString());

				hasResults = stmt.execute(sb.toString());

				// Result print
				ResultSet rs = stmt.getResultSet();
				if (hasResults && rs != null) {
					ResultSetMetaData md = rs.getMetaData();
					int cols = md.getColumnCount();
					String tmp = "";
					for (int i = 1; i <= cols; i++) {
						String name = md.getColumnLabel(i);
						tmp += (name + "   ");
					}
					list.add(tmp);
					list.add(new String(new char[tmp.length()]).replace("\0", "="));

					while (rs.next()) {
						String tmp2 = "";
						for (int i = 1; i <= cols; i++) {
							String value = rs.getString(i);
							tmp2 += (value + "   ");
						}
						list.add(tmp2);
					}
				}

				if (rs != null) {
					rs.close();
				}

				stmt.close();
				sb.delete(0, sb.length());
			} else {
				sb.append(str + "\n ");
			}
		}
		frame.printTuple(list);

		// hasResults = stmt.execute("DROP TABLE Book CASCADE CONSTRAINT");

		in.close();

		isScriptExecuted = true;

		return isScriptExecuted;
	}

	// boolean importSQLFile 3 (File aSQLScriptFilePath) throws IOException,
	// SQLException {
	// // 1)
	// boolean isScriptExecuted = false;
	//
	// ScriptRunner runner = new ScriptRunner(conn, false, false);
	// runner.runScript(new BufferedReader(new FileReader(aSQLScriptFilePath)));
	//
	// isScriptExecuted = true;
	//
	// // 2) Use FileReader
	// return isScriptExecuted;
	// }

	private void askConnectionMethod() {

		Object[] opts = { "Local access", "Ssh tunnel" };
		int localOrSSH = JOptionPane.showOptionDialog(null, "Please select your connecting method: ",
				"Welcome to use book manager!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts,
				opts[0]);

		switch (localOrSSH) {
		case 0:
			System.out.println("Choose: Using local access!");

			break;

		case 1:
			System.out.println("Choose: Using ssh tunnel!");
			localPordId = defaultForwardPort;
			host = "localhost";

			break;
		}

	}

	public BookManager() {

		// in = new Scanner(System.in);

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				askConnectionMethod();
				if (!login()) {
					System.out.println("Login failed, please re-examine your username and password!");
				} else {
					System.out.println("Login succeed!");

					frame = new MyFrame();
					frame.setVisible(true);

					askSQL();

					// manager.run(); // Merged to Myframe
				}

			}
		});

	}

	/**
	 * Main function
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		new BookManager();

	}

	@SuppressWarnings("serial")
	class MyFrame extends JFrame {
		BookSearch bookSearch;
		BookBorrow bookBorrow;
		BookReturn bookReturn;
		BookRenew bookRenew;
		BookReserve bookReserve;

		JPanel dashboard;
		JScrollPane display;
		Box box;

		public MyFrame() {
			this.setSize(1250, 900);
			this.setLayout(new GridLayout(2, 1));
			this.setTitle("Book Manager");
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			this.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					close();
					// isListen = false;
				}
			});

			dashboard = new JPanel();
			dashboard.setBorder(BorderFactory.createTitledBorder("Dashboard"));
			dashboard.setLayout(new GridLayout(1, 5));

			bookSearch = new BookSearch("Book Search");
			bookBorrow = new BookBorrow("Book Borrow");
			bookReturn = new BookReturn("Book Return");
			bookRenew = new BookRenew("Book Renew");
			bookReserve = new BookReserve("Book Reserve");

			dashboard.add(bookSearch);
			dashboard.add(bookBorrow);
			dashboard.add(bookReturn);
			dashboard.add(bookRenew);
			dashboard.add(bookReserve);

			box = Box.createVerticalBox();
			display = new JScrollPane(box);
			display.setBorder(BorderFactory.createTitledBorder("Result"));
			display.setPreferredSize(new Dimension(1000, 400));

			this.add(dashboard);
			this.add(display);

			// pack();

		}

		// @Override
		// public void dispose() {
		//
		// super.dispose();
		// }

		/*
		 * Run the manager
		 */
		////////////////////////////////////////////////////////////////////////////////////////////
		// private void run() throws ParseException {
		// while (true) {
		// showOptions();
		// String line = in.nextLine();
		// if (line.equalsIgnoreCase("exit"))
		// return;
		// int choice = -1;
		// try {
		// choice = Integer.parseInt(line);
		// } catch (Exception e) {
		// System.out.println("This option is not available");
		// continue;
		// }
		// if (!(choice >= 1 && choice <= options.length)) {
		// System.out.println("This option is not available");
		// continue;
		// }
		// if (options[choice - 1].equals("search book info")) {
		// bookSearch();
		// } else if (options[choice - 1].equals("book borrow")) {
		// bookBorrow();
		// } else if (options[choice - 1].equals("book return")) {
		// bookReturn();
		// } else if (options[choice - 1].equals("book renew")) {
		// bookRenew();
		// } else if (options[choice - 1].equals("book reserve")) {
		// bookReserve();
		// } else if (options[choice - 1].equals("search for borrowed book (by
		// ISBN)")) {
		// printBorrowInfo();
		// } else if (options[choice - 1].equals("search for student reservation
		// (by sid)")) {
		// printReserveInfo();
		// } // could add other info display method
		// }
		// }
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		class BookSearch extends JPanel {
			TitledBorder title;
			JButton[] buttons;
			JTextField[] textInput;
			JLabel[] notation;

			public BookSearch(String name) {
				super();
				setPreferredSize(new Dimension(300, 400));
				title = BorderFactory.createTitledBorder(name);
				setBorder(title);

				// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

				// String[] MLabels = { "Please input ISBN you want to search: "
				// };
				String[] MLabels = { "Please input ISBN you want to search:", "Please input the ISBN to print info:",
						"Please input the ISBN to print info:" };

				// String[] BLabels = { "Search book" };
				String[] BLabels = { "Search book info", "Search borrower", "Search reserver", "Show all borrow record",
						"Show all reserve record", "Import records", "Execute SQL cmd" };
				notation = new JLabel[MLabels.length];
				buttons = new JButton[BLabels.length];
				textInput = new JTextField[MLabels.length];
				for (int i = 0; i < MLabels.length; i++) {
					notation[i] = new JLabel(MLabels[i]);
					notation[i].setFont(new Font("Serif", Font.BOLD, 12));
					notation[i].setAlignmentX(Component.CENTER_ALIGNMENT);
					buttons[i] = new JButton(BLabels[i]);
					textInput[i] = new JTextField();
					textInput[i].setPreferredSize(new Dimension(100, 28));

				}

				add(notation[0]);
				add(textInput[0]);
				add(buttons[0]);

				add(notation[1]);
				add(textInput[1]);
				add(buttons[1]);

				add(notation[2]);
				add(textInput[2]);
				add(buttons[2]);

				buttons[3] = new JButton(BLabels[3]);
				add(buttons[3]);

				buttons[4] = new JButton(BLabels[4]);
				add(buttons[4]);

				buttons[5] = new JButton(BLabels[5]);
				add(buttons[5]);

				buttons[6] = new JButton(BLabels[6]);
				add(buttons[6]);

				// "Search book"
				buttons[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String ISBN = textInput[0].getText();
						bookSearch(ISBN);
					}

				});

				// "Search borrow"
				buttons[1].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String ISBN = textInput[1].getText();
						printBorrowsInfo(ISBN);
					}

				});

				// "Search reserve"
				buttons[2].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String ISBN = textInput[2].getText();
						printReserveInfo(ISBN);

					}

				});

				// "Show all borrow record"
				buttons[3].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						listAllBorrow();

					}

				});

				// "Show all reserve record"
				buttons[4].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						listAllReserve();

					}

				});

				// "Import borrow record"
				buttons[5].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {

						// Create a file chooser
						JFileChooser chooser = null;
						try {
							chooser = new JFileChooser(dataFile.getCanonicalPath());
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // FILES_AND_DIRECTORIES
																				// &
																				// The
																				// default
																				// is
																				// FILES_ONLY.
						int returnVal = chooser.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							System.out.println("You chose to read the file: " + chooser.getSelectedFile().getName());
							File f = chooser.getSelectedFile();

							try {

								if (importSQLFile(f)) {

									JOptionPane.showMessageDialog(null, "The SQL data script read successfully.");

								}

							} catch (IOException ex) {
								ex.printStackTrace();
							} catch (SQLException e) {
								System.err.println("Failed to Execute" + sqlFile + ". The error is" + e.getMessage());
								JOptionPane.showMessageDialog(null,
										"Failed to Execute" + sqlFile + ". The error is" + e.getMessage());
								String tmp = ("State: " + e.getSQLState() + "   Code: " + e.getErrorCode()
										+ "   Message: " + e.getMessage());
								frame.printTuple(tmp);
								e.printStackTrace();
							}

						}

					}

				});

				// "Execute SQL cmd"
				buttons[6].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {

						String cmd = JOptionPane.showInputDialog(null, "Please input SQL cmd: ",
								"select table_name from user_tables");
						if (cmd == null) {
							//
							//
						} else {
							ArrayList<String> list = new ArrayList<String>();

							try {
								Statement stmt = conn.createStatement();
								boolean hasResults = false;

								hasResults = stmt.execute(cmd);

								// Result print
								ResultSet rs = stmt.getResultSet();
								if (hasResults && rs != null) {
									ResultSetMetaData md = rs.getMetaData();
									int cols = md.getColumnCount();
									String tmp = "";
									for (int i = 1; i <= cols; i++) {
										String name = md.getColumnLabel(i);
										tmp += (name + "   ");
									}
									list.add(tmp);
									list.add(new String(new char[tmp.length()]).replace("\0", "="));

									while (rs.next()) {
										String tmp2 = "";
										for (int i = 1; i <= cols; i++) {
											String value = rs.getString(i);
											tmp2 += (value + "   ");
										}
										list.add(tmp2);
									}
								}

								if (rs != null) {
									rs.close();
								}

								stmt.close();
							} catch (SQLException e) {
								e.printStackTrace();
								String tmp = ("State: " + e.getSQLState() + "Code: " + e.getErrorCode() + "Message: "
										+ e.getMessage());
								frame.printTuple(tmp);
							}

							frame.printTuple(list);

						}

					}

				});

			}

		}

		class BookBorrow extends JPanel {
			TitledBorder title;
			JButton[] buttons;
			JTextField[] textInput;
			JLabel[] notation;

			public BookBorrow(String name) {
				super();
				setPreferredSize(new Dimension(300, 400));
				title = BorderFactory.createTitledBorder(name);
				setBorder(title);

				// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

				String[] MLabels = { "Please input your SID: ", " & ISBN you want to borrow: ",
						" & BORROW DATE (yyyy-mm-dd): ", " <Optional> Expected DUE DATE (yyyy-mm-dd): " };

				String[] BLabels = { "Borrow book" };
				notation = new JLabel[MLabels.length];
				buttons = new JButton[BLabels.length];
				textInput = new JTextField[MLabels.length];
				for (int i = 0; i < MLabels.length; i++) {
					notation[i] = new JLabel(MLabels[i]);
					notation[i].setFont(new Font("Serif", Font.BOLD, 12));
					notation[i].setAlignmentX(Component.CENTER_ALIGNMENT);

					textInput[i] = new JTextField();
					textInput[i].setPreferredSize(new Dimension(100, 28));

				}

				buttons[0] = new JButton(BLabels[0]);

				add(notation[0]);
				add(textInput[0]);

				add(notation[1]);
				add(textInput[1]);

				add(notation[2]);
				add(textInput[2]);

				add(notation[3]);
				add(textInput[3]);

				add(buttons[0]);

				// "Borrow book"
				buttons[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String sid = textInput[0].getText();
						String ISBN = textInput[1].getText();
						String date = textInput[2].getText();
						String due = textInput[3].getText();

						try {

							if (due == null || due.trim().equalsIgnoreCase("")) {
								bookBorrow(sid, ISBN, date);

							} else {

								bookBorrow(sid, ISBN, date, due);

							}
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}

				});

			}
		}

		class BookReturn extends JPanel {
			TitledBorder title;
			JButton[] buttons;
			JTextField[] textInput;
			JLabel[] notation;

			public BookReturn(String name) {
				super();
				setPreferredSize(new Dimension(300, 400));
				title = BorderFactory.createTitledBorder(name);
				setBorder(title);

				// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

				String[] MLabels = { "Please input your SID: ", " & ISBN you want to return: " };

				String[] BLabels = { "Return book" };
				notation = new JLabel[MLabels.length];
				buttons = new JButton[BLabels.length];
				textInput = new JTextField[MLabels.length];
				for (int i = 0; i < MLabels.length; i++) {
					notation[i] = new JLabel(MLabels[i]);
					notation[i].setFont(new Font("Serif", Font.BOLD, 12));
					notation[i].setAlignmentX(Component.CENTER_ALIGNMENT);
					textInput[i] = new JTextField();
					textInput[i].setPreferredSize(new Dimension(100, 28));

				}

				buttons[0] = new JButton(BLabels[0]);

				add(notation[0]);
				add(textInput[0]);

				add(notation[1]);
				add(textInput[1]);

				add(buttons[0]);

				// "Return book"
				buttons[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String sid = textInput[0].getText();
						String ISBN = textInput[1].getText();
						bookReturn(sid, ISBN);
					}

				});

			}
		}

		class BookRenew extends JPanel {
			TitledBorder title;
			JButton[] buttons;
			JTextField[] textInput;
			JLabel[] notation;

			public BookRenew(String name) {
				super();
				setPreferredSize(new Dimension(300, 400));
				title = BorderFactory.createTitledBorder(name);
				setBorder(title);

				// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

				String[] MLabels = { "Please input your SID: ", " & ISBN you want to renew: " };

				String[] BLabels = { "Renew book" };
				notation = new JLabel[MLabels.length];
				buttons = new JButton[BLabels.length];
				textInput = new JTextField[MLabels.length];
				for (int i = 0; i < MLabels.length; i++) {
					notation[i] = new JLabel(MLabels[i]);
					notation[i].setFont(new Font("Serif", Font.BOLD, 12));
					notation[i].setAlignmentX(Component.CENTER_ALIGNMENT);
					textInput[i] = new JTextField();
					textInput[i].setPreferredSize(new Dimension(100, 28));

				}

				buttons[0] = new JButton(BLabels[0]);

				add(notation[0]);
				add(textInput[0]);

				add(notation[1]);
				add(textInput[1]);

				add(buttons[0]);

				// "Renew book"
				buttons[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String sid = textInput[0].getText();
						String ISBN = textInput[1].getText();
						try {
							bookRenew(sid, ISBN);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}

				});

			}
		}

		class BookReserve extends JPanel {
			TitledBorder title;
			JButton[] buttons;
			JTextField[] textInput;
			JLabel[] notation;

			public BookReserve(String name) {
				super();
				setPreferredSize(new Dimension(300, 400));
				title = BorderFactory.createTitledBorder(name);
				setBorder(title);

				// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

				String[] MLabels = { "Please input your SID: ", " & ISBN you want to reserve: ",
						" & RESERVE DATE (yyyy-mm-dd): " };

				String[] BLabels = { "Reserve book" };
				notation = new JLabel[MLabels.length];
				buttons = new JButton[BLabels.length];
				textInput = new JTextField[MLabels.length];
				for (int i = 0; i < MLabels.length; i++) {
					notation[i] = new JLabel(MLabels[i]);
					notation[i].setFont(new Font("Serif", Font.BOLD, 12));
					notation[i].setAlignmentX(Component.CENTER_ALIGNMENT);
					textInput[i] = new JTextField();
					textInput[i].setPreferredSize(new Dimension(100, 28));

				}

				buttons[0] = new JButton(BLabels[0]);

				add(notation[0]);
				add(textInput[0]);

				add(notation[1]);
				add(textInput[1]);

				add(notation[2]);
				add(textInput[2]);

				add(buttons[0]);

				// "Reserve book"
				buttons[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String sid = textInput[0].getText();
						String ISBN = textInput[1].getText();
						String date = textInput[2].getText();

						try {
							bookReserve(sid, ISBN, date);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}

					}

				});

			}
		}

		void printTuple(String str) {
			box.removeAll();

			if (str == null || str.equalsIgnoreCase("")) {
				JLabel notation = new JLabel(" <EMPTY> ");
				notation.setFont(new Font("Serif", Font.BOLD, 12));
				notation.setAlignmentX(Component.CENTER_ALIGNMENT);
				box.add(notation);
			} else {
				JLabel tuple = new JLabel(str);
				box.add(tuple);

			}

			box.revalidate();
			box.repaint();

		}

		void printTuple(ArrayList<String> list) {
			box.removeAll();

			if (list == null || list.size() == 0) {
				JLabel notation = new JLabel(" <EMPTY> ");
				notation.setFont(new Font("Serif", Font.BOLD, 12));
				notation.setAlignmentX(Component.CENTER_ALIGNMENT);
				box.add(notation);
			} else {
				JLabel[] tuples = new JLabel[list.size()];
				for (int i = 0; i < list.size(); i++) {
					tuples[i] = new JLabel(list.get(i));
					box.add(tuples[i]);
				}

			}

			box.revalidate();
			box.repaint();

		}

	}

}

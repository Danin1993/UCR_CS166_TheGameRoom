
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.Math;

public class GameRental {

   // ---------------------------------------------
   // ---------------------------------------------
   // -- Other
   // ---------------------------------------------
   // ---------------------------------------------

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));

   // ---------------------------------------------
   // ---------------------------------------------
   // -- GUI
   // ---------------------------------------------
   // ---------------------------------------------

   public static final String RESET = "\033[0m";
   public static final String RED = "\033[38;5;124m";
   public static final String GREEN = "\033[38;5;118m";
   public static final String YELLOW = "\033[38;5;208m";
   public static final String BLUE = "\033[38;5;39m";
   public static final String DARK_GRAY = "\033[38;5;240m";
   public static final String PURPLE = "\033[38;5;135m";
   public static final String CYAN = "\033[38;5;51m";
   public static final String ORANGE = "\033[38;5;214m";
   public static final String PINK = "\033[38;5;205m";

   public static void print_title(String message, String color, String icon) {
      System.out.print(color);
      System.out.println("╔═══════════════════════════════════════════════╗");
      System.out.println("║  " + icon + "  " + RESET + message + color);
      System.out.println("╚═══════════════════════════════════════════════╝");
      System.out.print(RESET);
   }

   public static void print_message(String message, String color, String icon) {
      System.out.println(color);
      System.out.println("   ----------------------------------------------");
      System.out.println("   " + icon + "  " + RESET + message + color);
      System.out.println("   ----------------------------------------------");
      System.out.println(RESET);
   }

   public static void print_newLine() {
      System.out.println("");
   }

   public static void print_wait(String color) {
      final String BOLD = "\033[1m";
      System.out.print("   " + color + BOLD + "Press [ENTER] to continue: " + RESET);
      try {
         in.readLine();
      } catch (IOException e) {
         System.err.println(e.getMessage());
      }
   }

   public static void print_option(int number, String description, String color) {
      if (number < 10) {
         System.out.println(color + "   [0" + number + "️] ➝   " + RESET + description);
      } else {
         System.out.println(color + "   [" + number + "️] ➝   " + RESET + description);
      }

   }

   public static String input_string(String prompt, String color, BufferedReader in) throws IOException {
      System.out.print("   " + color + prompt + ": " + RESET);
      return in.readLine();
   }

   public static int input_int(String prompt, String color, BufferedReader in) throws IOException {
      System.out.print("   " + color + prompt + ": " + RESET);
      return Integer.parseInt(in.readLine());
   }

   public static void clear_Console() {
      System.out.print("\033[H\033[2J");
      System.out.flush();
   }

   public static void print_list_order(List<List<String>> games) {

      for (List<String> game : games) {
         System.out.printf(
               ""
                     + RED + "> %-1s" + RESET + " | "
                     + YELLOW + "📦 %-3s" + RESET + " | "
                     + GREEN + "$%-8s" + RESET + " | "
                     + BLUE + "➡️  %-1s" + RESET + " | "
                     + RESET + "🚚 %-1s" + RESET + " \n",
               game.get(0), game.get(1), game.get(2), game.get(3), game.get(4));
      }
   }

   public static void print_list_games(List<List<String>> games) {

      for (List<String> game : games) {
         System.out.printf(
               ""
                     + RED + "> %-1s" + RESET + " | "
                     + GREEN + "$%-1s" + RESET + " | "
                     + BLUE + "%-12s" + RESET + " | "
                     + PURPLE + "🎮 %-1s" + RESET + " | "
                     + RESET + "%-1s" + RESET + " \n",
               game.get(0), game.get(1), game.get(2), game.get(3), game.get(4));
      }
   }

   // ---------------------------------------------
   // ---------------------------------------------
   // -- QUERY
   // ---------------------------------------------
   // ---------------------------------------------

   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   }// end GameRental

   /**
    * Method to execute an update SQL statement. Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the update instruction
      stmt.executeUpdate(sql);

      // close the instruction
      stmt.close();
   }// end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
         if (outputHeader) {
            for (int i = 1; i <= numCol; i++) {
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i = 1; i <= numCol; ++i)
            System.out.print(rs.getString(i) + "\t");
         System.out.println();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   }// end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
         for (int i = 1; i <= numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
      } // end while
      stmt.close();
      return result;
   }// end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      int rowCount = 0;

      // iterates through the result set and count nuber of results.
      while (rs.next()) {
         rowCount++;
      } // end while
      stmt.close();
      return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement();

      ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close();
         } // end if
      } catch (SQLException e) {
         // ignored.
      } // end try
   }// end cleanup

   // ---------------------------------------------
   // ---------------------------------------------
   // -- MENU
   // ---------------------------------------------
   // ---------------------------------------------

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login
    *             file>
    */
   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println(
               "Usage: " +
                     "java [-classpath <classpath>] " +
                     GameRental.class.getName() +
                     " <dbname> <port> <user>");
         return;
      } // end if

      start_app();
      GameRental esql = null;
      try {
         // use postgres JDBC driver.
         Class.forName("org.postgresql.Driver").newInstance();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental(dbname, dbport, user, "");

         boolean keepon = true;
         while (keepon) {
            menu_auth();
            String authorisedUser = null;

            switch (select_option()) {
               case 0:
                  auth_all(esql);
                  break;
               case 1:
                  auth_signup(esql);
                  break;
               case 2:
                  authorisedUser = auth_signin(esql);
                  break;
               case 9:
                  keepon = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }// end switch

            if (authorisedUser != null) {
               boolean usermenu = true;
               // Greet the user after successful login

               while (usermenu) {
                  menu_dash(authorisedUser);

                  switch (select_option()) {
                     case 1:
                        profile_view(esql, authorisedUser);
                        break;
                     case 2:
                        profile_edit(esql, authorisedUser, in);
                        break;
                     case 3:
                        games_view(esql, in);
                        break;
                     case 4:
                        games_view_fav(esql, in, authorisedUser);
                        break;
                     case 5:
                        games_push_fav(esql, in, authorisedUser);
                        break;
                     case 6:
                        games_pop_fav(esql, in, authorisedUser);
                        break;
                     case 7:
                        order_push(esql, in, authorisedUser);
                        break;
                     case 8:
                        order_view_all(esql, authorisedUser);
                        break;
                     case 9:
                        order_view_recent(esql, authorisedUser);
                        break;
                     case 10:
                        order_view_info(esql, in, authorisedUser);
                        break;
                     case 11:
                        admin_update_user(esql, in, authorisedUser);
                        break;
                     case 12:
                        admin_update_catalog(esql, in, authorisedUser);
                        break;
                     case 13:
                        admin_update_trackingInfo(esql, in, authorisedUser);
                        break;
                     case 0:
                        usermenu = false;
                        authorisedUser = null; // Clear the authorisedUser when logging out
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            }
         } // end while

      } catch (Exception e) {
         System.err.println(e.getMessage());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try {
            if (esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   }// end main

   public static void start_app() {
      print_title("Install App", RESET, "▫️");
   }

   public static int select_option() {
      int input;
      do {

         try { // read the integer, parse it and break.
            print_newLine();
            input = input_int("👉  Select an option", RESET, in);
            print_newLine();
            break;
         } catch (Exception e) {

            print_message("Your input is invalid!", RED, "❌");
            continue;
         } // end try
      } while (true);
      return input;
   }// end select_option

   // ---------------------------------------------
   // ---------------------------------------------
   // -- Functions
   // ---------------------------------------------
   // ---------------------------------------------

   // ---- Menu

   public static void menu_auth() {
      clear_Console();
      print_title("Welcome to GameRoom", YELLOW, "🎮");
      print_option(1, "Sign Up", GREEN);
      print_option(2, "Sign In", GREEN);
      print_option(9, "Exit", RED);
      print_option(0, "debug", RESET);
   }

   public static void menu_dash(String username) {
      clear_Console();
      print_title("Welcome " + username + "!", GREEN, "👋");
      print_option(1, "View  | Profile", GREEN);
      System.out.println("-----------------------------------------");
      print_option(2, "Edit  | Profile", GREEN);

      print_newLine();
      print_title("Manage your Games!", RED, "🕹️");
      print_option(3, "View   | All Games", RED);
      System.out.println("-----------------------------------------");
      print_option(4, "View   | Fav Games", RED);
      System.out.println("-----------------------------------------");
      print_option(5, "Add    | Fav Games", RED);
      System.out.println("-----------------------------------------");
      print_option(6, "Delete | Fav Games", RED);

      print_newLine();
      print_title("Manage your Orders!", YELLOW, "📦");
      print_option(7, "Add    | New Rental", YELLOW);
      System.out.println("-----------------------------------------");
      print_option(8, "View   | All Rental", YELLOW);
      System.out.println("-----------------------------------------");
      print_option(9, "View   | Last 5 Rental", YELLOW);
      System.out.println("-----------------------------------------");
      print_option(10, "View   | Rental + Tracking", YELLOW);

      print_newLine();
      print_title("Admin Access", BLUE, "🔏");
      print_option(11, "Edit   | User", BLUE);
      System.out.println("-----------------------------------------");
      print_option(12, "Edit   | Game", BLUE);
      System.out.println("-----------------------------------------");
      print_option(13, "Edit   | Tracking", BLUE);

      print_newLine();
      print_option(0, "Log out", RED);
   }

   // ---- Auth

   // [01️] ➝ Sign Up
   public static void auth_signup(GameRental esql) {
      try {
         print_message("Create Account | Let's get you started", RESET, "🔏");
         String login = input_string("🙍 Username", GREEN, in);
         String password = input_string("🔒 Password", RED, in);
         String phoneNum = input_string("📞 PhoneNum", BLUE, in);
         String role = "customer";
         String favGames = "";
         int numOverDueGames = 0;

         if (login.isEmpty() || password.isEmpty()) {
            print_message("Username or Password cannot be empty.", RED, "❌");
            print_wait(RED);
            return;
         }

         // Create SQL statement to insert a new user into the Users table
         String query = String.format(
               "INSERT INTO Users (login, password, role, favGames, phoneNum, numOverDueGames) "
                     + "VALUES ('%s', '%s', '%s', '%s', '%s', %d)",
               login, password, role, favGames, phoneNum, numOverDueGames);

         // Execute the query
         esql.executeUpdate(query);
         print_message("Your Account has been created", GREEN, "✅");
         print_wait(GREEN);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [01️] ➝ Sign In
   public static String auth_signin(GameRental esql) {
      try {
         print_message("Login Account | Manage your games and more", RESET, "🔓");
         String login = input_string("🙍 Username", GREEN, in);
         String password = input_string("🔒 Password", RED, in);

         // Create SQL query to verify login credentials
         String query = String.format(
               "SELECT login FROM Users WHERE login = '%s' AND password = '%s'",
               login, password);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.size() > 0) {
            print_message("Login successful!", GREEN, "✅");
            print_wait(GREEN);
            return login; // Return the login to indicate successful authentication
         } else {
            print_message("Invalid login or password.", RED, "❌");
            print_wait(RED);
            return null; // Return null to indicate failed authentication
         }
      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
         return null;
      }
   }

   public static void auth_all(GameRental esql) {
      try {
         String query = "SELECT login, password, role, phoneNum FROM Users";
         List<List<String>> users = esql.executeQueryAndReturnResult(query);

         if (users.isEmpty()) {
            print_message("No users found in the database.", RED, "❌");
            return;
         }

         // Print table header
         System.out.print(GREEN);
         System.out.println("╔══════════════════════╦════════════════════╦═══════════════╦═════════════════╗");
         System.out.println("║ Username             ║ Password           ║ Role          ║ Phone Number    ║");
         System.out.println("╠══════════════════════╬════════════════════╬═══════════════╬═════════════════╣");
         System.out.print(RESET);

         // Print user data
         for (List<String> user : users) {
            System.out.println(String.format("║ %-20s ║ %-18s ║ %-13s ║ %-15s ║",
                  user.get(0), user.get(1), user.get(2), user.get(3)));
         }

         System.out.println("╚══════════════════════╩════════════════════╩═══════════════╩═════════════════╝");
         print_wait(GREEN);
      } catch (SQLException e) {
         print_message(e.getMessage(), RED, "⭕");
         print_wait(RED);
      } catch (Exception e) {
         print_message(e.getMessage(), RED, "⭕");
         print_wait(RED);
      }
   }

   // ---- Profile

   // [01️] ➝ View - Profile
   public static void profile_view(GameRental esql, String authorisedUser) {
      try {

         String query = String.format(
               "SELECT login, password, role, favGames, phoneNum, numOverDueGames FROM Users WHERE login = '%s'",
               authorisedUser);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.size() > 0) {
            List<String> userProfile = result.get(0);
            String userLogin = userProfile.get(0);
            String userPassword = userProfile.get(1);
            String userRole = userProfile.get(2);
            String userFavGames = userProfile.get(3);
            String userPhoneNum = userProfile.get(4);
            int userNumOverDueGames = Integer.parseInt(userProfile.get(5));

            // Display user profile information
            clear_Console();
            print_message("User Profile | Username : " + GREEN + userLogin, RESET, "🙍");
            System.out.println("   🔒 Password: " + userPassword);
            System.out.println("   🔑 Role: " + userRole);
            System.out.println("   📞 Phone Number: " + userPhoneNum);
            System.out.println("   🎮 Favorite Games: " + userFavGames);
            System.out.println("   ⏰ Overdue Games: " + userNumOverDueGames);
            print_newLine();
            print_wait(GREEN);
         } else {
            print_message("User profile not found.", RED, "❌");
            print_wait(RED);
         }
      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [02️] ➝ Edit - Profile
   public static void profile_edit(GameRental esql, String authorisedUser, BufferedReader in) {
      try {
         // Fetch the current profile details
         String query = String.format(
               "SELECT login, password, phoneNum FROM Users WHERE login = '%s'",
               authorisedUser);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.size() > 0) {
            List<String> userProfile = result.get(0);
            String userPassword = userProfile.get(1);
            String userPhoneNum = userProfile.get(2);

            clear_Console();
            print_message("User Profile | Username : " + GREEN + authorisedUser, RESET, "🙍");
            System.out.println("   🔑 Password: " + userPassword);
            System.out.println("   📞 Phone Number: " + userPhoneNum);
            print_newLine();

            print_message("Edit your info ( New Value | Empty )", RESET, "✏️");
            String newPassword = input_string("🔸 New Password", RESET, in);
            String newPhoneNum = input_string("🔸 New Phone Number", RESET, in);

            // If input is blank, keep current value
            if (newPassword.isEmpty()) {
               newPassword = userPassword;
            }
            if (newPhoneNum.isEmpty()) {
               newPhoneNum = userPhoneNum;
            }

            // Create SQL update query
            String updateQuery = String.format(
                  "UPDATE Users SET password = '%s', phoneNum = '%s' WHERE login = '%s'",
                  newPassword, newPhoneNum, authorisedUser);

            // Execute update
            esql.executeUpdate(updateQuery);
            print_message("Profile updated successfully.", GREEN, "✅");
            print_wait(GREEN);

         } else {
            print_message("User profile not found.", RED, "❌");
            print_wait(RED);
         }
      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // ---- Games

   // [03️] ➝ View - All Games
   public static void games_view(GameRental esql, BufferedReader in) {
      try {
         print_option(1, "Filter - Genre", GREEN);
         print_option(2, "Filter - Price", GREEN);
         print_option(3, "Sort - Price ⬆️", BLUE);
         print_option(4, "Sort - Price ⬇️", BLUE);
         print_option(5, "Show - Games", BLUE);
         print_option(6, "Back", RED);

         int choice = input_int("👉  Select an option", RESET, in);
         String query = "";

         clear_Console();

         switch (choice) {
            case 1:
               String genre = input_string("🔸  Enter Genre", ORANGE, in);
               query = String.format(
                     "SELECT gameID, price, genre, gameName, description FROM Catalog WHERE genre = '%s'", genre);
               break;
            case 2:
               double maxPrice = Double.parseDouble(input_string("🔸  Enter Maximum Price", ORANGE, in));
               query = String.format(
                     "SELECT gameID, price, genre, gameName, description FROM Catalog WHERE price <= %f", maxPrice);
               break;
            case 3:
               query = "SELECT gameID, price, genre, gameName, description FROM Catalog ORDER BY price ASC";
               break;
            case 4:
               query = "SELECT gameID, price, genre, gameName, description FROM Catalog ORDER BY price DESC";
               break;
            case 5:
               query = "SELECT gameID, price, genre, gameName, description FROM Catalog";
               break;
            case 6:
               return;
            default:
               print_message("Invalid choice!", RED, "❌");
               return;
         }

         List<List<String>> games = esql.executeQueryAndReturnResult(query);

         if (games.isEmpty()) {
            print_message("Invalid choice!", RED, "❌");
            print_wait(RED);
            return;
         }

         print_list_games(games);
         print_newLine();
         print_wait(GREEN);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [04️] ➝ View - Fav Games
   public static void games_view_fav(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         String query = String.format(
               "SELECT gameID, price, genre, gameName, description " +
                     "FROM Catalog " +
                     "WHERE gameID IN (SELECT unnest(string_to_array(favGames, ',')) " +
                     "FROM Users WHERE login = '%s')",
               authorisedUser);

         List<List<String>> favGames = esql.executeQueryAndReturnResult(query);

         if (favGames.isEmpty()) {
            print_message("No favorite games found for the user!", RED, "❌");
            print_wait(RED);
            return;
         }

         print_list_games(favGames);
         print_newLine();
         print_wait(GREEN);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [05️] ➝ Push - Fav Games
   public static void games_push_fav(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         clear_Console();
         String gameID = input_string("🔸  Enter GameID of FavGame", GREEN, in);

         // Check if the game exists in the Catalog table
         String gameCheckQuery = String.format("SELECT gameID FROM Catalog WHERE gameID = '%s'", gameID);
         List<List<String>> gameCheckResult = esql.executeQueryAndReturnResult(gameCheckQuery);

         if (gameCheckResult.isEmpty()) {
            print_message("Game not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         // Retrieve the current favorite games
         String query = String.format("SELECT favGames FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         String favGames = result.get(0).get(0);
         if (favGames == null || favGames.isEmpty()) {
            favGames = gameID;
         } else {
            favGames += "," + gameID;
         }

         // Update the favorite games
         query = String.format("UPDATE Users SET favGames = '%s' WHERE login = '%s'", favGames, authorisedUser);
         esql.executeUpdate(query);

         print_message("FavGame added successfully!", GREEN, "✅");
         print_wait(GREEN);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [06️] ➝ Pop - Fav Games
   public static void games_pop_fav(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         clear_Console();
         String gameID = input_string("🔸  Enter GameID of FavGame", RED, in);

         // Check if the game exists in the Catalog table
         String gameCheckQuery = String.format("SELECT gameID FROM Catalog WHERE gameID = '%s'", gameID);
         List<List<String>> gameCheckResult = esql.executeQueryAndReturnResult(gameCheckQuery);

         if (gameCheckResult.isEmpty()) {
            print_message("Game not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         // Retrieve the current favorite games
         String query = String.format("SELECT favGames FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         String favGames = result.get(0).get(0);
         List<String> favGamesList = new ArrayList<>(Arrays.asList(favGames.split(",")));
         favGamesList.remove(gameID);

         favGames = String.join(",", favGamesList);

         // Update the favorite games
         query = String.format("UPDATE Users SET favGames = '%s' WHERE login = '%s'", favGames, authorisedUser);
         esql.executeUpdate(query);

         print_message("FavGame removed successfully!", RED, "✅");
         print_wait(RED);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // ---------------------
   // ---- Orders
   // ---------------------

   // [07] ➝ Order - Create New
   public static void order_push(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         clear_Console();
         print_message(" | Then order now!", RESET, "🎮 ➝ 🚚 ➝ 🎁 ➝ 😊");
         String gameID = input_string("🔸 Enter Game ID: ", YELLOW, in);
         int unitsOrdered = input_int("🔸 Enter units ordered: ", ORANGE, in);

         // Check if the gameID exists in the Catalog
         String checkGameQuery = String.format("SELECT price FROM Catalog WHERE gameID = '%s'", gameID);
         List<List<String>> gameResult = esql.executeQueryAndReturnResult(checkGameQuery);

         if (gameResult.isEmpty()) {
            print_message("Game ID does not exist in the catalog.", RED, "❌");
            print_wait(RED);
            return;
         }

         if (unitsOrdered <= 0) {
            print_message("Units ordered should be greater than 0.", RED, "❌");
            print_wait(RED);
            return;
         }

         double pricePerUnit = Double.parseDouble(gameResult.get(0).get(0));
         double totalPrice = pricePerUnit * unitsOrdered;

         // Generate a unique rentalOrderID
         String rentalOrderID = UUID.randomUUID().toString();

         // Insert RentalOrder table
         String insertOrderQuery = String.format(
               "INSERT INTO RentalOrder (rentalOrderID, login, noOfGames, totalPrice, orderTimestamp, dueDate) VALUES ('%s', '%s', %d, %f, NOW(), NOW() + INTERVAL '7 days')",
               rentalOrderID, authorisedUser, unitsOrdered, totalPrice);
         esql.executeUpdate(insertOrderQuery);

         // Insert GamesInOrder table
         String insertGameQuery = String.format(
               "INSERT INTO GamesInOrder (rentalOrderID, gameID, unitsOrdered) VALUES ('%s', '%s', %d)",
               rentalOrderID, gameID, unitsOrdered);
         esql.executeUpdate(insertGameQuery);

         // Generate a unique trackingID
         String trackingID = UUID.randomUUID().toString();

         // Insert TrackingInfo table
         String insertTrackingQuery = String.format(
               "INSERT INTO TrackingInfo (trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate) VALUES ('%s', '%s', 'Order Placed', 'Out for Delivery', 'Fedex', NOW())",
               trackingID, rentalOrderID);
         esql.executeUpdate(insertTrackingQuery);

         print_message(String.format("Order placed successfully! Total price: $%.2f", totalPrice), GREEN, "✅");
         print_wait(ORANGE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [08] ➝ View - All Orders
   public static void order_view_all(GameRental esql, String authorisedUser) {
      try {
         String query = String.format(
               "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate " +
                     "FROM RentalOrder WHERE login = '%s'",
               authorisedUser);

         List<List<String>> orders = esql.executeQueryAndReturnResult(query);

         if (orders.isEmpty()) {
            print_message("No orders found for the user!", RED, "❌");
            print_wait(RED);
            return;
         }

         print_list_order(orders); // Adjust the parameters as needed for proper formatting
         print_newLine();
         print_wait(ORANGE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [09] ➝ View - Recent Orders
   public static void order_view_recent(GameRental esql, String authorisedUser) {
      try {
         clear_Console();

         // Query to get the most recent orders
         String query = String.format(
               "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate " +
                     "FROM RentalOrder WHERE login = '%s' ORDER BY orderTimestamp DESC LIMIT 5",
               authorisedUser);

         List<List<String>> recentOrders = esql.executeQueryAndReturnResult(query);

         if (recentOrders.isEmpty()) {
            print_message("No recent orders found for the user!", RED, "❌");
            print_wait(RED);
            return;
         }

         print_list_order(recentOrders); // Adjust the parameters as needed for proper formatting
         print_newLine();
         print_wait(ORANGE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [10️] ➝ View - Rental + Tracking
   public static void order_view_info(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         clear_Console();
         String rentalOrderID = input_string("🔸 Enter Rental Order ID: ", YELLOW, in);

         // Check if the rental order belongs to the authorized user
         String orderCheckQuery = String.format(
               "SELECT login FROM RentalOrder WHERE rentalOrderID = '%s'", rentalOrderID);
         List<List<String>> orderCheckResult = esql.executeQueryAndReturnResult(orderCheckQuery);

         if (orderCheckResult.isEmpty() || !orderCheckResult.get(0).get(0).equals(authorisedUser)) {
            print_message("Unauthorized access or order not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         // Query to get the rental order details
         String rentalOrderQuery = String.format(
               "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate " +
                     "FROM RentalOrder WHERE rentalOrderID = '%s'",
               rentalOrderID);
         List<List<String>> rentalOrder = esql.executeQueryAndReturnResult(rentalOrderQuery);

         // Query to get the game details
         String gameDetailsQuery = String.format(
               "SELECT g.gameID, g.gameName, g.genre, g.price, g.description " +
                     "FROM GamesInOrder gio JOIN Catalog g ON gio.gameID = g.gameID " +
                     "WHERE gio.rentalOrderID = '%s'",
               rentalOrderID);
         List<List<String>> gameDetails = esql.executeQueryAndReturnResult(gameDetailsQuery);

         // Query to get the tracking information
         String trackingInfoQuery = String.format(
               "SELECT trackingID, status, currentLocation, courierName, lastUpdateDate, additionalComments " +
                     "FROM TrackingInfo WHERE rentalOrderID = '%s'",
               rentalOrderID);
         List<List<String>> trackingInfo = esql.executeQueryAndReturnResult(trackingInfoQuery);

         // Print rental order details
         print_message("Rental Order Information:", YELLOW, "📦");
         System.out.println("   🆔 Rental Order ID: " + rentalOrder.get(0).get(0));
         System.out.println("   🎮 Number of Games: " + rentalOrder.get(0).get(1));
         System.out.println("   💵 Total Price: $" + rentalOrder.get(0).get(2));
         System.out.println("   📅 Order Timestamp: " + rentalOrder.get(0).get(3));
         System.out.println("   📅 Due Date: " + rentalOrder.get(0).get(4));

         // Print game details
         print_message("Game Information:", YELLOW, "🎮");
         for (List<String> game : gameDetails) {
            System.out.println("   🆔 Game ID: " + game.get(0));
            System.out.println("   🎮 Game Name: " + game.get(1));
            System.out.println("   🏷️ Genre: " + game.get(2));
            System.out.println("   💵 Price: $" + game.get(3));
            System.out.println("   📜 Description: " + game.get(4));
            System.out.println();
         }

         // Print tracking information
         print_message("Tracking Information:", YELLOW, "🚚");
         System.out.println("   🆔 Tracking ID: " + trackingInfo.get(0).get(0));
         System.out.println("   📦 Status: " + trackingInfo.get(0).get(1));
         System.out.println("   📍 Current Location: " + trackingInfo.get(0).get(2));
         System.out.println("   🚚 Courier Name: " + trackingInfo.get(0).get(3));
         System.out.println("   📅 Last Update Date: " + trackingInfo.get(0).get(4));
         System.out.println("   📝 Additional Comments: " + trackingInfo.get(0).get(5));

         print_newLine();
         print_wait(ORANGE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [11] ➝ Admin - Update User
   public static void admin_update_user(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         // Check if the authorisedUser has manager role
         String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);

         if (roleResult.isEmpty() || !roleResult.get(0).get(0).trim().equalsIgnoreCase("manager")) {
            print_message("Unauthorized access! Only managers can update user information.", RED, "❌");
            print_wait(RED);
            return;
         }

         // Get the login of the user to be updated
         clear_Console();
         String userLogin = input_string("🔹 Enter the login of the user to update: ", BLUE, in);

         // Fetch the current user details
         String query = String.format(
               "SELECT login, password, role, favGames, phoneNum, numOverDueGames FROM Users WHERE login = '%s'",
               userLogin);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
            print_message("User not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         List<String> userProfile = result.get(0);
         String userPassword = userProfile.get(1);
         String userRole = userProfile.get(2);
         String userFavGames = userProfile.get(3);
         String userPhoneNum = userProfile.get(4);
         int userNumOverDueGames = Integer.parseInt(userProfile.get(5));

         clear_Console();
         print_message("User Profile | Username : " + GREEN + userLogin, RESET, "🙍");
         System.out.println("   🔒 Password: " + userPassword);
         System.out.println("   🔑 Role: " + userRole);
         System.out.println("   📞 Phone Number: " + userPhoneNum);
         System.out.println("   🎮 Favorite Games: " + userFavGames);
         System.out.println("   ⏰ Overdue Games: " + userNumOverDueGames);
         print_newLine();

         print_message("Edit user info (New Value | Empty to keep current value)", RESET, "✏️");
         String newPassword = input_string("🔹 New Password: ", BLUE, in);
         String newRole = input_string("🔹 New Role (customer | manager | employee) ", BLUE, in);
         String newPhoneNum = input_string("🔹 New Phone Number: ", BLUE, in);
         String newFavGames = input_string("🔹 New Favorite Games (comma-separated)", BLUE, in);

         // If input is blank, keep current value
         if (newPassword.isEmpty()) {
            newPassword = userPassword;
         }
         if (newRole.isEmpty()) {
            newRole = userRole;
         }
         if (newPhoneNum.isEmpty()) {
            newPhoneNum = userPhoneNum;
         }
         if (newFavGames.isEmpty()) {
            newFavGames = userFavGames;
         }

         // Create SQL update query
         String updateQuery = String.format(
               "UPDATE Users SET password = '%s', role = '%s', phoneNum = '%s', favGames = '%s' WHERE login = '%s'",
               newPassword, newRole, newPhoneNum, newFavGames, userLogin);

         // Execute update
         esql.executeUpdate(updateQuery);
         print_message("User profile updated successfully.", GREEN, "✅");
         print_wait(BLUE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [12] ➝ Admin - Update Catalog
   public static void admin_update_catalog(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         // Check if the authorisedUser has manager role
         String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);

         if (roleResult.isEmpty() || !roleResult.get(0).get(0).trim().equalsIgnoreCase("manager")) {
            print_message("Unauthorized access! Only managers can update catalog information.", RED, "❌");
            print_wait(RED);
            return;
         }

         // Get the gameID of the catalog item to be updated
         clear_Console();
         String gameID = input_string("🔹 Enter the GameID of the catalog item to update: ", BLUE, in);

         // Fetch the current catalog details
         String query = String.format(
               "SELECT gameID, gameName, genre, price, description, imageURL FROM Catalog WHERE gameID = '%s'",
               gameID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
            print_message("Catalog item not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         List<String> catalogItem = result.get(0);
         String gameName = catalogItem.get(1);
         String genre = catalogItem.get(2);
         String price = catalogItem.get(3);
         String description = catalogItem.get(4);
         String imageURL = catalogItem.get(5);

         clear_Console();
         print_message("Catalog Item | GameID : " + GREEN + gameID, RESET, "🎮");
         System.out.println("   🎮 Game Name: " + gameName);
         System.out.println("   🎮 Genre: " + genre);
         System.out.println("   💵 Price: " + price);
         System.out.println("   📝 Description: " + description);
         System.out.println("   🌐 Image URL: " + imageURL);
         print_newLine();

         print_message("Edit catalog info (New Value | Empty to keep current value)", RESET, "✏️");
         String newGameName = input_string("🔹 New Game Name: ", BLUE, in);
         String newGenre = input_string("🔹 New Genre: ", BLUE, in);
         String newPrice = input_string("🔹 New Price: ", BLUE, in);
         String newDescription = input_string("🔹 New Description: ", BLUE, in);
         String newImageURL = input_string("🔹 New Image URL: ", BLUE, in);

         // If input is blank, keep current value
         if (newGameName.isEmpty()) {
            newGameName = gameName;
         }
         if (newGenre.isEmpty()) {
            newGenre = genre;
         }
         if (newPrice.isEmpty()) {
            newPrice = price;
         }
         if (newDescription.isEmpty()) {
            newDescription = description;
         }
         if (newImageURL.isEmpty()) {
            newImageURL = imageURL;
         }

         // Create SQL update query
         String updateQuery = String.format(
               "UPDATE Catalog SET gameName = '%s', genre = '%s', price = '%s', description = '%s', imageURL = '%s' WHERE gameID = '%s'",
               newGameName, newGenre, newPrice, newDescription, newImageURL, gameID);

         // Execute update
         esql.executeUpdate(updateQuery);
         print_message("Catalog item updated successfully.", GREEN, "✅");
         print_wait(BLUE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

   // [13] ➝ Admin - Update Tracking Info
   public static void admin_update_trackingInfo(GameRental esql, BufferedReader in, String authorisedUser) {
      try {
         // Check if the authorisedUser has manager or employee role
         String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);

         if (roleResult.isEmpty()) {
            print_message("Unauthorized access!", RED, "❌");
            print_wait(RED);
            return;
         }

         String userRole = roleResult.get(0).get(0).trim().toLowerCase();
         if (!userRole.equals("manager") && !userRole.equals("employee")) {
            print_message("Unauthorized access! Only managers and employees can update tracking information.", RED,
                  "❌");
            print_wait(RED);
            return;
         }

         // Get the trackingID of the tracking info to be updated
         clear_Console();
         String trackingID = input_string("🔹 Enter the TrackingID of the shipment to update: ", BLUE, in);

         // Fetch the current tracking details
         String query = String.format(
               "SELECT trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate, additionalComments FROM TrackingInfo WHERE trackingID = '%s'",
               trackingID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
            print_message("Tracking information not found!", RED, "❌");
            print_wait(RED);
            return;
         }

         List<String> trackingInfo = result.get(0);
         String rentalOrderID = trackingInfo.get(1);
         String status = trackingInfo.get(2);
         String currentLocation = trackingInfo.get(3);
         String courierName = trackingInfo.get(4);
         String lastUpdateDate = trackingInfo.get(5);
         String additionalComments = trackingInfo.get(6);

         clear_Console();
         print_message("Tracking Info | TrackingID : " + GREEN + trackingID, RESET, "🚚");
         System.out.println("   🆔 Rental Order ID: " + rentalOrderID);
         System.out.println("   📋 Status: " + status);
         System.out.println("   📍 Current Location: " + currentLocation);
         System.out.println("   🚚 Courier Name: " + courierName);
         System.out.println("   🕒 Last Update Date: " + lastUpdateDate);
         System.out.println("   📝 Additional Comments: " + additionalComments);
         print_newLine();

         print_message("Edit tracking info (New Value | Empty to keep current value)", RESET, "✏️");
         String newStatus = input_string("🔹 New Status: ", BLUE, in);
         String newCurrentLocation = input_string("🔹 New Current Location: ", BLUE, in);
         String newCourierName = input_string("🔹 New Courier Name: ", BLUE, in);
         String newAdditionalComments = input_string("🔹 New Additional Comments: ", BLUE, in);

         // If input is blank, keep current value
         if (newStatus.isEmpty()) {
            newStatus = status;
         }
         if (newCurrentLocation.isEmpty()) {
            newCurrentLocation = currentLocation;
         }
         if (newCourierName.isEmpty()) {
            newCourierName = courierName;
         }
         if (newAdditionalComments.isEmpty()) {
            newAdditionalComments = additionalComments;
         }

         // Create SQL update query
         String updateQuery = String.format(
               "UPDATE TrackingInfo SET status = '%s', currentLocation = '%s', courierName = '%s', additionalComments = '%s', lastUpdateDate = NOW() WHERE trackingID = '%s'",
               newStatus, newCurrentLocation, newCourierName, newAdditionalComments, trackingID);

         // Execute update
         esql.executeUpdate(updateQuery);
         print_message("Tracking information updated successfully.", GREEN, "✅");
         print_wait(BLUE);

      } catch (Exception e) {
         String errorMsg = e.getMessage().trim().replaceAll("\\s+", " ");
         print_message(errorMsg, RED, "⭕");
         print_wait(RED);
      }
   }

}// end GameRental

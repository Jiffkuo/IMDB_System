import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Tzu-Chi Kuo on 2017/2/25.
 * Purpose: populate dat file and insert into Oracle DB
 */
public class Populate {
    public static void main(String[] args) {
        Populate gen = new Populate();
        gen.execute(args);
        System.out.println("Finished connection");
    }

    /*
     * The skeleton code of database access
     */
    public void execute(String[] args) {
        Connection conn = null;
        try {
            // build connect
            System.out.println("DB server connecting...");
            conn = openConnect();
            System.out.println("DB server connection successfully");
            // publish data for each input file
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains("user")) {
                    publishUserData(conn, args[i]);
                } else {
                    publishData(conn, args[i]);
                }
            }
        } catch (SQLException e) {
            System.err.println("[Error]: Errors occurs when connecting to the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("[Error]: Cannot find the database driver");
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            closeConnect(conn);
        }
    }

    /*
     * Construct and return a database connection
     * throws SQL Exception if an error connection
     * throws ClassNotFoundException if DB driver not found
     */
    private Connection openConnect() throws SQLException, ClassNotFoundException {
        // Load Oracle DB driver
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        // hard-coded configuration to connect DB server
        String host = "localhost";
        String port = "1521";
        String dbName = "xe"; // Win: xe, MAC: orcl
        String uName = "hr";
        String pWord = "hr";

        // Construct JDBC URL
        String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
        return DriverManager.getConnection(dbURL, uName, pWord);
    }

    /*
     * Close the database connection
     */
    private void closeConnect(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("[Error]: Cannot close Oracle DB connection: " + e.getMessage());
        }
    }

    /*
     * Publish data to database
     * First clean up the table then populate it with new data
     */
    private void publishData(Connection conn, String fName) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);

        // remove filename extension, file name equals to table name
        String tableName = fName.replaceFirst(".dat", "");

        // deleting previous tuples
        System.out.println("[Info]: Deleting previous tuplies from " + tableName);
        stmt.executeUpdate("DELETE FROM " + tableName);

        // open file, read line and spilt by tab then inserting data
        System.out.println("[Info]: Inserting Data into " + tableName);
        String tuple = bufReader.readLine(); // ignore first line
        int fieldLen = tuple.split("\t").length;

        // case1-1: movie_locations inserts five fields but some field data is invalid
        if (tableName.equals("movie_locations")) {
            fieldLen = 5;
        }
        // read each line and insert data
        while ((tuple = bufReader.readLine()) != null) {
            // spilt String by tab
            String[] fields = tuple.split("\t");
            String prefix = "";
            StringBuilder sb = new StringBuilder();
            // case1-2: if data invalid, no insert the data
            /*
            if (fieldLen != fields.length) {
                continue;
            }
            */
            for (int i = 0; i < fieldLen; i++) {
                String attr = " ";
                // case2: replace ' symbol because SQL insert format
                if (i < fields.length && fields[i] != null) {
                    attr = fields[i].replaceAll("'", "''");
                }
                // case3: replace data \N with (null) data
                if (attr.equals("\\N")) {
                    attr = "";
                }

                sb.append(prefix + "'" + attr + "'");
                prefix = ", ";
            }
            stmt.executeUpdate("INSERT INTO " + tableName + " VALUES (" + sb.toString() + ")");
        }

        // close file and statement
        fReader.close();
        stmt.close();
    }

    /*
     * Publish User data to database
     * First clean up the table then populate it with new data
     */
    private void publishUserData(Connection conn, String fName) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);

        // remove filename extension, file name equals to table name
        String tableName = fName.replaceFirst(".dat", "");
        boolean timestamp = false;
        if (tableName.contains("-timestamps")) {
            System.out.print("[Info]: " + tableName + "share table with ");
            timestamp = true;
            tableName = tableName.replaceFirst("-timestamps", "");
            System.out.println(tableName);
        }

        // deleting previous tuples
        System.out.println("[Info]: Deleting previous tuplies from " + tableName);
        stmt.executeUpdate("DELETE FROM " + tableName);

        // open file, read line and spilt by tab then inserting data
        System.out.println("[Info]: Inserting Data into " + tableName);
        String tuple = bufReader.readLine(); // ignore first line
        int fieldLen = 3; // user_* field length is fixed to 3
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO " + tableName + " VALUES(?,?,?,?)");
        // read each line and insert data
        while ((tuple = bufReader.readLine()) != null) {
            // spilt String by tab
            String[] fields = tuple.split("\t");
            for (int i = 0; i < fieldLen; i++) {
                pstmt.setString(i + 1, fields[i]);
            }
            StringBuilder sb = new StringBuilder();
            if (!timestamp) {
                // yyyy-mm-dd
                sb.append(fields[5] + "-" + fields[4] + "-" + fields[3] + " ");
                // hh-mi-ss
                sb.append(fields[6] + ":" + fields[7] + ":" + fields[8]);
            } else {
                // Oracle JDBC timestamp format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // ensure to parse the same timezone between user*.dat and user*-timestamps.dat
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                Date date = new Date(Long.parseLong(fields[3]));
                sb.append(sdf.format(date));
            }
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(sb.toString()));
            pstmt.executeUpdate();
        }
        // close file and statement
        fReader.close();
        pstmt.close();
        stmt.close();
    }
}

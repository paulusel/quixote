package quixote.core;

import java.io.File;
import java.sql.*;

final public class Database {
    private String filename = "quixote.db";
    private Connection conn;

    private String mkdir(){
        String data_home;
        if(System.getProperty("os.name").equalsIgnoreCase("Windows")){
            data_home = System.getenv("APPDATA") + "\\quixote";
        }
        else {
            data_home = System.getenv("XDG_DATA_HOME");
            if (data_home == null || data_home.isEmpty()) {
                data_home = System.getProperty("user.home") + "/.local/share/quixote";
            } else {
                data_home = data_home + "/quixote";
            }
        }

        new File(data_home).mkdir();

        return data_home;
    }

    public Database(){
        var db_path = "jdbc:sqlite:" + mkdir() + File.separator + filename;
        try{
            conn = DriverManager.getConnection(db_path);
            this.createTables();
        } catch(SQLException e){
            System.out.println("FATAL: failed to get database connection");
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    public void createTables() throws SQLException {
            PreparedStatement stmnt;

            String query = "CREATE TABLE IF NOT EXISTS noteitem ("
                            + "id INTEGER PRIMARY KEY,"
                            + "isNote INTEGER,"
                            + "title VARCHAR,"
                            + "note TEXT,"
                            + "modified DATE"
                            + ");";
            stmnt = conn.prepareStatement(query);
            stmnt.execute();

            query = "CREATE TABLE IF NOT EXISTS notebooks ("
                    + "id INTEGER PRIMARY KEY,"
                    + "name VARCHAR"
                    + ");";

            stmnt = conn.prepareStatement(query);
            stmnt.execute();
    }
}

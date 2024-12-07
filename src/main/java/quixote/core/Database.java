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
            data_home = data_home.isEmpty() ? System.getProperty("home") + "/.local/share/quixote" : data_home + "/quixote";
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

            String query = "CREATE TABLE IF NOT EXISTS notes ("
                            + "id INTEGER PRIMARY KEY,"
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

package quixote.core;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

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
            if (data_home == null) {
                data_home = System.getProperty("user.home") + "/.local/share/quixote";
            } else {
                data_home = data_home + "/quixote";
            }
        }

        new File(data_home).mkdir();

        return data_home;
    }

    public Database() throws SQLException {
        String db_path = "jdbc:sqlite:" + mkdir() + File.separator + filename;
        conn = DriverManager.getConnection(db_path);
        Statement stmnt = conn.createStatement();

        stmnt.execute("PRAGMA foreign_keys = ON");
        stmnt.execute(
            "CREATE TABLE IF NOT EXISTS notes ( "
            + "id INTEGER PRIMARY KEY, "
            + "title VARCHAR NOT NULL, "
            + "note TEXT, "
            + "modified DATE NOT NULL DEFAULT (unixepoch('now')), "
            + "parent INTEGER NOT NULL, "
            + "FOREIGN KEY (parent) REFERENCES notebooks (id) ON DELETE CASCADE "
            + ")"
        );

        stmnt.execute(
            "CREATE TABLE IF NOT EXISTS notebooks ( "
            + "id INTEGER PRIMARY KEY, "
            + "name VARCHAR NOT NULL, "
            + "parent INTEGER, "
            + "FOREIGN KEY (parent) REFERENCES notebooks (id) ON DELETE CASCADE "
            + ")"
        );

        stmnt.execute(
            "CREATE TRIGGER IF NOT EXISTS update_modified "
            + "AFTER UPDATE ON notes "
            + "BEGIN "
            + "UPDATE notes SET modified = (unixepoch('now')) WHERE id = OLD.id; "
            + "END"
        );

        if(stmnt.executeQuery("SELECT * FROM notebooks").next())
            return;

        stmnt.execute("INSERT INTO notebooks (name) VALUES ('root')");
    }

    public void shutdown(){
        try{
            conn.close();
        }
        catch(SQLException e){
            System.out.println("ERROR: Failed to properly shutdown database connection: " + e.getMessage());
        }
    }

    public ArrayList<NoteItem> getItems(Notebook parent){
        ArrayList<NoteItem> list = new ArrayList<>();
        try{
            var stmnt = conn.prepareStatement("SELECT id, title, note, modified FROM notes WHERE parent = ?");
            stmnt.setInt(1, parent.id());
            var result = stmnt.executeQuery();
            while (result.next()) {
                //System.out.println("Extracting notes");
                Note note = new Note(parent, "", "", 0);
                note.id(result.getInt(1));
                note.title(result.getString(2));
                note.document().setPlainText(result.getString(3));
                note.modified().setTime(result.getLong(4));
                list.add(note);
            }

            stmnt = conn.prepareStatement("SELECT id, name FROM notebooks WHERE parent = ?");
            stmnt.setInt(1, parent.id());
            result = stmnt.executeQuery();
            while(result.next()){
                //System.out.println("Extracting notebooks");
                Notebook nb = new Notebook(parent, "", 0);
                nb.id(result.getInt(1));
                nb.title(result.getString(2));
                list.add(nb);
            }
        }
        catch(SQLException e){
            System.out.println("ERROR: Failed to fetch notes: " + e.getMessage());
        }

        return list;
    }

    public void saveNote(Note note){
        try{
            var stmnt = conn.prepareStatement("UPDATE notes SET title = ?, note = ?, parent = ? WHERE id = ?");
            stmnt.setString(1, note.title());
            stmnt.setString(2, note.document().toPlainText());
            stmnt.setInt(3, note.parent().id());
            stmnt.setInt(4, note.id());
            stmnt.execute();
        }
        catch(SQLException e){
            System.out.println("ERROR: Failed to save notebook: " + e.getMessage());
        }
    }

    public void saveNotebook(Notebook nb){
        try{
            var stmnt = conn.prepareStatement("UPDATE notebooks set name = ?, parent = ? WHERE id = ?");
            stmnt.setString(1, nb.title());
            stmnt.setInt(2, nb.parent().id());
            stmnt.setInt(3, nb.id());
            stmnt.execute();
        }
        catch(SQLException e){
            System.out.println("ERROR: Failed to save note: " + e.getMessage());
        }
    }

    public void save(NoteItem item){
        if(item instanceof Note){
            saveNote((Note) item);
        }
        else{
            saveNotebook((Notebook) item);
        }
    }
}

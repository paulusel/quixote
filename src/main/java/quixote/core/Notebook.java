package quixote.core;

import java.util.ArrayList;

public class Notebook extends NoteItem {
    private ArrayList<NoteItem> notes = new ArrayList<>();

    public Notebook(Notebook notebook, String title, int id){
        super(notebook, title, id);
    }

    public int itemCount(){
        return notes.size();
    }

    public void addItem(NoteItem item){
        notes.add(item);
    }

    public NoteItem itemAt(int row){
        return row < 0 || row >= notes.size()
            ? null
            : notes.get(row);
    }

    int positionOf(NoteItem item){
        return notes.indexOf(item);
    }
}

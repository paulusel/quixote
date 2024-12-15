package quixote.core;

import java.util.ArrayList;

public class Notebook extends NoteItem {
    private ArrayList<NoteItem> items = new ArrayList<>();

    public Notebook(Notebook notebook, String title, int id){
        super(notebook, title, id);
    }

    public int itemCount(){
        return items.size();
    }

    public void addItem(NoteItem item){
        items.add(item);
    }

    public void addItem(NoteItem item, int pos){
        if(pos < 0 || pos > items.size()){
            System.err.println("Notebook: Out-of-bound error while inserting at " + pos);
            return; // Throw exception, maybe?
        }

        items.add(pos, item);
    }

    public NoteItem itemAt(int row){
        return row < 0 || row >= items.size()
            ? null
            : items.get(row);
    }

    public void removeItemAt(int row){
        if(row < 0 || row >= items.size())
            return;

        items.remove(row);
    }

    public ArrayList<NoteItem> children(){
        return items;
    }

    int positionOf(NoteItem item){
        return items.indexOf(item);
    }
}

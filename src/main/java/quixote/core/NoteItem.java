package quixote.core;

import java.util.Date;

public abstract class NoteItem {
    protected Notebook parent;
    protected String title;
    protected int id;

    protected NoteItem(Notebook parent, String title, int id){
        this.parent = parent;
        this.title = (title == null || title.isEmpty())
            ? new Date().toString()
            : title;
        this.id = id;
    }

    public String title(){
        return title;
    }

    public void title(String title){
        this.title = title;
    }

    public Notebook parent(){
        return parent;
    }

    public int id(){
        return id;
    }

    public void id(int id){
        this.id = id;
    }

    public int pos(){
        if(parent == null)
            return -1;
        return parent.positionOf(this);
    }
}

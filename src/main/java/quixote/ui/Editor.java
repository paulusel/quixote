package quixote.ui;

import quixote.core.Note;
import quixote.core.App;
import quixote.core.Buffer;

import java.util.ArrayList;

import io.qt.widgets.*;

public class Editor {
    private Tabline tabline;
    private QStackedLayout bufferLayout;
    private QWidget editor;
    private App app;

    private ArrayList<Buffer> buffers = new ArrayList<>();

    public Editor(QWidget parent, App app){
        this.app = app;

        editor = new QWidget();
        editor.setLayout(new QVBoxLayout());
        parent.layout().addWidget(editor);

        tabline = new Tabline(editor);

        var buffers = new QWidget(editor);
        bufferLayout = new QStackedLayout();
        buffers.setLayout(bufferLayout);
        editor.layout().addWidget(buffers);

    }

    public void newBuffer(Note note){
        tabline.newTab(note.title);

        var edit = new Buffer(note);
        edit.installEventFilter(app);
        bufferLayout.addWidget(edit);
        bufferLayout.setCurrentWidget(edit);

        buffers.add(edit);
    }

    public boolean isEmpty(){
        return buffers.isEmpty();
    }

    public void showNext(){
        bufferLayout.setCurrentIndex((bufferLayout.currentIndex()+1)%bufferLayout.count());
    }

    public void showPrev(){

    }

    public void show(int offset){

    }

    public void closeCurrent(){

    }
}

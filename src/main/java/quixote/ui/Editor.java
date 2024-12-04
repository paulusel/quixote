package quixote.ui;

import quixote.core.Note;
import quixote.core.App;

import io.qt.widgets.*;

public class Editor {
    private Tabline tabline;
    private QWidget buffers;
    private QWidget editor;
    private App app;

    public Editor(QWidget parent, App app){
        editor = new QWidget();
        editor.setLayout(new QVBoxLayout());
        parent.layout().addWidget(editor);

        buffers = new QWidget(editor);
        buffers.setLayout(new QStackedLayout());

        tabline = new Tabline(editor);

        // one text edit
        var edit = new QTextEdit();
        edit.installEventFilter(app);

        buffers.layout().addWidget(edit);
        editor.layout().addWidget(buffers);
    }

    public void newBuffer(Note note){
        //tabline.newTab(note.title);
    }

    public void showNext(){

    }

    public void showPrev(){

    }

    public void show(int offset){

    }

    public void closeCurrent(){

    }
}

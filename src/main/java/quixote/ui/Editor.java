package quixote.ui;

import quixote.core.Note;

import io.qt.widgets.*;

public class Editor {
    private Statusline statusline;
    private Tabline tabline;
    private QWidget buffers;
    private QWidget editor;

    public Editor(QWidget parent){
        editor = new QWidget();
        editor.setLayout(new QVBoxLayout());
        parent.layout().addWidget(editor);

        buffers = new QWidget(editor);
        buffers.setLayout(new QStackedLayout());

        tabline = new Tabline(editor);

        // one text edit
        buffers.layout().addWidget(new QTextEdit());
        editor.layout().addWidget(buffers);

        statusline = new Statusline(editor);
    }

    public void newBuffer(Note note){
        tabline.newTab(note.title);
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

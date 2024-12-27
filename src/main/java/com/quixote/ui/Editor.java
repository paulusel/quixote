package com.quixote.ui;

import com.quixote.core.Note;
import com.quixote.core.Notebook;

import java.util.HashMap;
import java.util.List;

import io.qt.widgets.*;
import io.qt.core.QModelIndex;
import io.qt.core.QTimer;
import io.qt.core.Qt;

final public class Editor extends QWidget {

    final public Signal0 editorEmpty = new Signal0();

    private Tabline tabline;
    private QStackedLayout bufferLayout;
    private HashMap<Note, Buffer> buffers = new HashMap<>();

    public Editor(QWidget parent){
        super(parent);
        parent.layout().addWidget(this);

        this.setLayout(new QVBoxLayout());
        tabline = new Tabline(this);
        QWidget bufferContainer = new QWidget(this);
        bufferLayout = new QStackedLayout();
        bufferContainer.setLayout(bufferLayout);
        this.layout().addWidget(bufferContainer);

        QTimer timer = new QTimer(this);
        timer.timeout.connect(this::save);
        timer.start(10000);
    }

    public void newBuffer(Note note){
        Buffer buffer = buffers.get(note);

        if(buffer != null){
            bufferLayout.setCurrentWidget(buffer);
            return;
        }

        buffer = new Buffer(note);
        tabline.newTab(buffer.tab());
        App.app.modeChanged.connect(buffer::changeMode);
        buffer.bufferClosed.connect(this::reapBuffer);

        bufferLayout.addWidget(buffer);
        bufferLayout.setCurrentWidget(buffer);

        buffers.put(note, buffer);
    }

    public void reapBuffer(Buffer buffer) {
        buffers.remove(buffer.note());
        saveWork(buffer);

        if(buffers.isEmpty())
            editorEmpty.emit();
        buffer.dispose();
    }

    public void saveWork(Buffer buffer){
            if(!buffer.document().isModified())
                return;
            App.db.saveNote(buffer.note());
            buffer.document().setModified(false);
    }

    public void save(){
        for(Buffer buffer: buffers.values())
            saveWork(buffer);
    }

    public boolean isEmpty(){
        return buffers.isEmpty();
    }

    public void showNext(){
        bufferLayout.setCurrentIndex((bufferLayout.currentIndex()+1)%bufferLayout.count());
    }

    public void itemEdited(QModelIndex first, QModelIndex last, List<Integer> roles){
        Object item = first.data(Qt.ItemDataRole.UserRole);
        if(item instanceof Notebook) {
            return;
        }

        Buffer buffer = buffers.get((Note) item);
        if(buffer == null) {
            return;
        }

        buffer.refreshName();
    }

    public void showPrev(){

    }

    public void show(int offset){

    }
}
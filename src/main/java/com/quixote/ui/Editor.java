package com.quixote.ui;

import com.quixote.core.Note;
import com.quixote.core.Notebook;

import java.util.HashMap;
import java.util.List;

import io.qt.widgets.*;
import io.qt.core.QModelIndex;
import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;

final public class Editor extends QWidget {

    final public Signal0 editorEmpty = new Signal0();

    final public Tabline header;

    private QStackedLayout bufferLayout;
    private QWidget bufferContainer;
    private HashMap<Note, Buffer> buffers = new HashMap<>();

    public Editor(QWidget parent){
        super(parent);

        header = new Tabline(this);

        this.setLayout(new QVBoxLayout());
        bufferContainer = new QWidget(this);
        bufferLayout = new QStackedLayout();
        bufferContainer.setLayout(bufferLayout);
        this.layout().addWidget(bufferContainer);
        bufferLayout.widgetRemoved.connect(this::refocus);

        QTimer timer = new QTimer(this);
        timer.timeout.connect(this::save);
        timer.start(10000);
    }

    public void newBuffer(Note note){
        Buffer buffer = buffers.get(note);

        if(buffer != null){
            unfocusCurrent();
            bufferLayout.setCurrentWidget(buffer);
            return;
        }

        buffer = new Buffer(bufferContainer, note);
        header.newTab(buffer.tab());
        buffer.bufferClosed.connect(this::reapBuffer);

        bufferLayout.addWidget(buffer);
        unfocusCurrent();
        bufferLayout.setCurrentWidget(buffer);
        focusCurrent();

        buffers.put(note, buffer);
    }

    public void unfocusCurrent(){
        Buffer buffer = (Buffer) bufferLayout.currentWidget();
        if(buffer == null){
            return;
        }
        buffer.unfocus();
    }

    public void refocus(int indx){
        focusCurrent();
    }

    public void focusCurrent(){
        Buffer buffer = (Buffer) bufferLayout.currentWidget();
        if(buffer == null){
            return;
        }
        buffer.focus();
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
        unfocusCurrent();
        int nextIndx = (bufferLayout.currentIndex() + 1) % bufferLayout.count();
        bufferLayout.setCurrentIndex(nextIndx);
        focusCurrent();
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

    @Override
    public void keyPressEvent(QKeyEvent event){
        int key = event.key();
        if(key == Qt.Key.Key_N.value()){
            showNext();
            event.accept();
        }
        else {
            event.ignore();
        }
    }
}

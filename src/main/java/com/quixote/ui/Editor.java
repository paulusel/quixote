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

        // If buffer is not already opened, create new
        if(buffer == null){
            buffer = new Buffer(bufferContainer, note);
            header.newTab(buffer.tab());
            buffer.bufferClosed.connect(this::reapBuffer);

            buffers.put(note, buffer);

            bufferLayout.addWidget(buffer);
        }

        showBuffer(buffer);
    }

    public void reapBuffer(Buffer buffer) {
        buffers.remove(buffer.note());
        saveWork(buffer);
        showPrev();

        if(buffers.isEmpty())
            editorEmpty.emit();
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

    /**
     *  Refocus the currently diplayed widget.
     *
     *  This is meant to be called by signal after a an active is closed and focus is
     *  transfered to new one. Mainly to alert the new buffer that it is in focus now.
     */
    public void refocus(int indx){
        showBuffer(((Buffer) bufferLayout.currentWidget()));
    }

    public void showBufferAt(int indx){
        int bufferCount = bufferLayout.count();
        if(bufferCount < 2 || indx < 0 || indx >= bufferCount){
            return;
        }
        showBuffer((Buffer) bufferLayout.itemAt(indx).widget());
    }

    public void showBuffer(Buffer buffer){
        if(buffer == null){
            return;
        }

        Buffer oldBuffer = (Buffer) bufferLayout.currentWidget();
        if(oldBuffer != null){
            oldBuffer.unfocus();
        }
        bufferLayout.setCurrentWidget(buffer);
        buffer.focus();
    }

    public void showNext(){
        int nextIndx = (bufferLayout.currentIndex() + 1) % bufferLayout.count();
        showBufferAt(nextIndx);
    }

    public void showPrev(){
        int totalCount = bufferLayout.count();
        int index = bufferLayout.currentIndex();
        index = (index == 0) ? totalCount - 1 : index - 1;
        showBufferAt(index);
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

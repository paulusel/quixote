package quixote.ui;

import quixote.core.Note;

import java.util.HashMap;

import io.qt.widgets.*;

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

        if(buffers.isEmpty())
            editorEmpty.emit();
        buffer.dispose();
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
}

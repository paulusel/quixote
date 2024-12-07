package quixote.ui;

import quixote.core.Note;

import java.util.ArrayList;

import io.qt.widgets.*;

final public class Editor extends QWidget {
    private Tabline tabline;
    private QStackedLayout bufferLayout;

    private ArrayList<Buffer> buffers = new ArrayList<>();

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
        tabline.newTab(note.title);

        var edit = new Buffer(note);
        // FIXME: Every buffer listens to modeChange. Make only active buffer listen?
        App.app.modeChanged.connect(edit::changeMode);
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

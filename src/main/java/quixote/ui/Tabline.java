package quixote.ui;

import io.qt.widgets.*;

final public class Tabline extends QWidget {
    public Tabline(QWidget parent){
        super(parent);
        this.setLayout(new QHBoxLayout());
        parent.layout().addWidget(this);
    }

    public void newTab(String name){
        this.layout().addWidget(new QLabel(name));
    }

    public void closeActive(){

    }
}

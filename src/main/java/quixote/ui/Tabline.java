package quixote.ui;

import io.qt.widgets.*;

public class Tabline {
    private QWidget tabline;

    public Tabline(QWidget parent){
        tabline = new QWidget(parent);
        tabline.setLayout(new QHBoxLayout());
        parent.layout().addWidget(tabline);
    }

    public void newTab(String name){
        tabline.layout().addWidget(new QLabel(name));
    }

    public void closeActive(){

    }
}

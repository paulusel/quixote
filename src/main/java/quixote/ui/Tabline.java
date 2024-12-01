package quixote.ui;

import io.qt.widgets.*;

public class Tabline {
    private QWidget tabline;

    public Tabline(QWidget parent){
        tabline = new QWidget(parent);
        tabline.setLayout(new QHBoxLayout());
        tabline.layout().addWidget(new QLabel("Note"));
    }
}

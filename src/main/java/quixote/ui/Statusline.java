package quixote.ui;

import io.qt.widgets.*;

public class Statusline {
    private QStatusBar statusBar;

    public Statusline(QWidget parent){
        statusBar = new QStatusBar(parent);
        parent.layout().addWidget(statusBar);
    }
}

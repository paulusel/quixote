package quixote.ui;

import quixote.core.App;

import io.qt.widgets.*;

final public class Statusline {
    private QStatusBar statusBar;
    private QLabel mode;
    private App app;

    public Statusline(QWidget parent, App app){
        this.app = app;

        statusBar = new QStatusBar(parent);
        parent.layout().addWidget(statusBar);
        this.app.modeChanged.connect(this::setMode);

        mode = new QLabel();
        setMode("NORMAL");
        statusBar.addWidget(mode);
    }

    public void setMode(String mode){
        this.mode.setText(mode);
    }
}

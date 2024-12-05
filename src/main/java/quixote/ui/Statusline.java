package quixote.ui;

import quixote.core.App;

import io.qt.widgets.*;

final public class Statusline extends QStatusBar {
    private QLabel mode;
    private App app;

    public Statusline(QWidget parent, App app){
        super(parent);
        this.app = app;

        parent.layout().addWidget(this);
        this.app.modeChanged.connect(this::setMode);

        mode = new QLabel();
        setMode("NORMAL");
        this.addWidget(mode);
    }

    public void setMode(String mode){
        this.mode.setText(mode);
    }
}

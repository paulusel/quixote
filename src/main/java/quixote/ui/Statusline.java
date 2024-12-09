package quixote.ui;

import io.qt.widgets.*;

final public class Statusline extends QStatusBar {
    private QLabel mode;

    public Statusline(QWidget parent, App app){
        super(parent);

        parent.layout().addWidget(this);
        app.modeChanged.connect(this::setMode);

        mode = new QLabel();
        setMode("NORMAL");
        this.addWidget(mode);
    }

    public void setMode(String mode){
        this.mode.setText(mode);
    }
}

package quixote.ui;

import quixote.core.App;

import io.qt.widgets.*;

final public class Statusline extends QStatusBar {
    private QLabel mode;

    public Statusline(QWidget parent){
        super(parent);

        parent.layout().addWidget(this);
        App.app.modeChanged.connect(this::setMode);

        mode = new QLabel();
        setMode("NORMAL");
        this.addWidget(mode);
    }

    public void setMode(String mode){
        this.mode.setText(mode);
    }
}

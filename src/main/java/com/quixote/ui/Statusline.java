package com.quixote.ui;

import io.qt.widgets.*;

final public class Statusline extends QStatusBar {
    private QLabel modeLabel;

    public Statusline(QWidget parent, App app){
        super(parent);

        parent.layout().addWidget(this);
        app.modeChanged.connect(this::setMode);

        modeLabel = new QLabel();
        setMode(App.Mode.NORMAL);
        this.addPermanentWidget(modeLabel);
        this.setStyleSheet("max-height: 15px;min-height: 15px;");
    }

    public void setMode(App.Mode mode){
        this.modeLabel.setText(mode.toString());
    }
}

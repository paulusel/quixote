package com.quixote.ui;

import io.qt.widgets.*;

final public class Statusline extends QStatusBar {
    private QLabel modeLabel;
    private QLabel cmdline;
    public static Statusline line;

    public static void init(QWidget parent, App app){
        if(line != null){
            return;
        }

        line = new Statusline(parent, app);
    }

    private Statusline(QWidget parent, App app){
        super(parent);
        parent.layout().addWidget(this);

        modeLabel = new QLabel(this);
        setMode(App.Mode.NORMAL);
        this.addPermanentWidget(modeLabel);

        cmdline = new QLabel(this);
        this.addWidget(cmdline);
    }

    public void displayMsg(String str, int time){
        this.showMessage(str, time);
    }

    public void setMode(App.Mode mode){
        this.modeLabel.setText(mode.toString());
    }

    public void appendCommand(String str){
        cmdline.setText(cmdline.text()+str);
    }

    public void clearCommand(){
        cmdline.setText("");
    }
}

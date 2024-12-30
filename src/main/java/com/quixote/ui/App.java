package com.quixote.ui;

import com.quixote.core.*;

import io.qt.gui.QAction;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;
import io.qt.core.QEvent;
import io.qt.core.Qt;

import java.sql.SQLException;

public class App extends QWidget {

    public enum Mode {NORMAL, INSERT, VISUAL};

    public static App app;
    public static Database db;

    private QWidget viewArea;
    private QWidget header;
    private QStackedLayout hLayout;
    private QStackedLayout layout; // layout manager for viewArrea
    private Editor editor;
    private Selector selector;

    final private String stylesheet = """
            * {
                background: #3D3D3D;
            }
    """;

    private static final int key_tab = Qt.Key.Key_Tab.value();

    public static void initialize(String[] args){
        if(app != null)
            return;

        try{
            QApplication.initialize(args);
            QApplication.setCursorFlashTime(0);
            db = new Database(); // This must preceed new App()
            app = new App();
        }
        catch(SQLException e){
            System.out.println("FATAL: Unable to intialize database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        catch(Exception e){
            System.out.println("FATAL: Unknow error occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // constructors
    private App(){
        this.setWindowTitle("Quixote");
        this.setStyleSheet(stylesheet);

        var blayout = new QVBoxLayout(this);
        blayout.setSpacing(0);

        // header
        header = new QWidget(this);
        hLayout = new QStackedLayout(header);

        // View Area
        viewArea = new QWidget(this);
        layout = new QStackedLayout(viewArea);

        // Statusline
        Statusline.init(this);

        // view Area elements
        selector = new Selector(viewArea);
        editor = new Editor(viewArea);
        viewArea.layout().addWidget(selector);
        viewArea.layout().addWidget(editor);

        // header elements
        header.layout().addWidget(selector.header);
        header.layout().addWidget(editor.header);

        // Add it all to the app
        this.layout().addWidget(header);
        this.layout().addWidget(viewArea);
        this.layout().addWidget(Statusline.line);

        blayout.setStretch(0, 0); // header (no stretch)
        blayout.setStretch(1, 1); // ViewArea (takes remaining space)
        blayout.setStretch(2, 0); // statusline (no stretch)


        // connect slots
        editor.editorEmpty.connect(this::switchView);
        selector.model().dataChanged.connect(editor::itemEdited);

        // Quit shortcut
        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect(this::stop);
        this.addAction(quitAction);
    }

    public void start() {
        this.show();
        QApplication.exec();
    }

    public void stop() {
        this.hide();
        cleanup();
        this.dispose();
        QApplication.quit();
    }

    public void openNote(Note note){
        editor.newBuffer(note);
        switchView();
    }

    private void cleanup(){
        editor.save();
        db.shutdown();
    }

    private void switchView(){
        if(layout.currentWidget() == selector && ! editor.isEmpty()) {
            layout.setCurrentWidget(editor);
            hLayout.setCurrentWidget(editor.header);
        }
        else if(layout.currentWidget() == editor){
            layout.setCurrentWidget(selector);
            hLayout.setCurrentWidget(selector.header);
        }
    }

    @Override
    public boolean event(QEvent e){
         if(e.type() == QEvent.Type.KeyPress){
            int key = ((QKeyEvent)e).key();
            if(key == key_tab){
                // toggle selector/editor
               switchView();
            }
            else if(key == Qt.Key.Key_N.value()) {
                editor.showNext();
            }

            return true;
        }

        return super.event(e);
    }
}

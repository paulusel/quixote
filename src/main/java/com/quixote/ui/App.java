package com.quixote.ui;

import com.quixote.core.*;

import io.qt.gui.QAction;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;

import java.sql.SQLException;

import io.qt.QtPrimitiveType;
import io.qt.core.QEvent;
import io.qt.core.Qt;


public class App extends QWidget {

    public enum Mode {NORMAL, INSERT, VISUAL};

    public static App app;
    public static Database db;

    private QWidget mainWindow;
    private QWidget viewArea;
    private QStackedLayout layout; // layout manager for viewArrea
    private Editor editor;
    private Selector selector;

    // signals
    public final Signal1<@QtPrimitiveType Integer> viewChanged = new Signal1<>();

    private static final int key_tab = Qt.Key.Key_Tab.value();

    public static void initialize(String[] args){
        if(app != null)
            return;

        try{
            QApplication.initialize(args);
            QApplication.setCursorFlashTime(0);
            db = new Database(); // This must be preceed new App()
            app = new App();
        }
        catch(SQLException e){
            System.out.println("FATAL: Unable to intialize database: " + e.getMessage());
        }
        catch(Exception e){
            System.out.println("FATAL: Unknow error occurred: " + e.getMessage());
        }
    }

    // constructors
    private App(){
        mainWindow = new QWidget();
        mainWindow.setWindowTitle("Quixote");

        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect(this::stop);
        mainWindow.addAction(quitAction);

        viewArea = new QWidget();
        layout = new QStackedLayout();
        viewArea.setLayout(layout);

        selector = new Selector(viewArea);
        editor = new Editor(viewArea);
        mainWindow.setLayout(new QVBoxLayout());
        mainWindow.layout().addWidget(viewArea);
        Statusline.init(mainWindow, this);

        // connect slots
        viewChanged.connect(layout::setCurrentIndex);
        editor.editorEmpty.connect(this::switchView);
        selector.model().dataChanged.connect(editor::itemEdited);
    }

    public void start() {
        mainWindow.show();
        QApplication.exec();
    }

    public void stop() {
        mainWindow.hide();
        cleanup();
        mainWindow.dispose();
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
        }
        else if(layout.currentWidget() == editor){
            layout.setCurrentWidget(selector);
        }
    }

    @Override
    public boolean event(QEvent e){
         if(e.type() == QEvent.Type.KeyPress){
            int key = ((QKeyEvent)e).key();
            if(key == key_tab){
                // toggle selector/editor
               switchView();
               return true;
            }
            else {
                // HACK: better event propagation up the parent-child line is needed
                return QApplication.sendEvent(editor, e);
            }
        }

        return super.event(e);
    }
}

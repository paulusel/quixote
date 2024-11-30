package quixote.core;

import io.qt.gui.QAction;
import io.qt.widgets.*;

public class App {
    private static App app;
    private static String[] args;

    private BufferManager mgr;
    private QWidget mainWindow;

    private App(){}; //

    static public App init(String[] args){
        if(app == null) {
            App.args = args;
            app = new App();
            app.initialize();
        }

        return app;
    }

    private void initialize(){
        mgr = BufferManager.manager();

        QApplication.initialize(App.args);

        mainWindow = new QWidget();
        mainWindow.setWindowTitle("Quixote");

        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect( ()->{ stop(); } );
        mainWindow.addAction(quitAction);

        var container = new QVBoxLayout();
        container.addWidget(new QLabel("Tabline"));
        container.addWidget(new QTextEdit());
        container.addWidget(new QLineEdit());

        mainWindow.setLayout(container);
    }

    public void start() {
        mainWindow.show();
        QApplication.exec();
    }

    public void stop() {
        mainWindow.hide();
        cleanup();
        QApplication.quit();
    }

    private void cleanup(){

    }
}

package quixote.core;

import io.qt.gui.QAction;
import io.qt.widgets.*;

import quixote.ui.Selector;
import quixote.ui.Editor;
import quixote.ui.Statusline;

public class App {
    private static App app;
    private static String[] args;
    private Statusline statusline;

    private QWidget mainWindow;
    private Selector selector;
    private Editor editor;

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
        QApplication.initialize(App.args);

        mainWindow = new QWidget();
        mainWindow.setWindowTitle("Quixote");

        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect(this::stop);
        mainWindow.addAction(quitAction);

        var upperArea = new QWidget();
        upperArea.setLayout(new QStackedLayout());
        editor = new Editor(upperArea);

        mainWindow.setLayout(new QVBoxLayout());
        mainWindow.layout().addWidget(upperArea);
        statusline = new Statusline(mainWindow);
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
        mainWindow.dispose();
    }
}

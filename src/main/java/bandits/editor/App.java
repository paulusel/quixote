package bandits.editor;

import io.qt.gui.QAction;
import io.qt.widgets.*;

public class App {
    public static void main(String[] args) {
        QApplication.initialize(args);

        var window = new QWidget();
        window.setWindowTitle("Quixote");

        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect(QApplication::quit);
        window.addAction(quitAction);

        var container = new QVBoxLayout();
        container.addWidget(new QLabel("Tabline"));
        container.addWidget(new QTextEdit());
        container.addWidget(new QLineEdit());

        window.setLayout(container);
        window.show();
        QApplication.exec();
    }
}

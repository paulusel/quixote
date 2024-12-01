package quixote.ui;

import io.qt.core.QDir;
import io.qt.gui.QFileSystemModel;
import io.qt.widgets.*;

public class Selector {

    private QWidget selector;

    public Selector(QWidget parent){
        selector = new QWidget();
        selector.setLayout(new QHBoxLayout());
        parent.layout().addWidget(selector);

        var model = new QFileSystemModel();
        model.setRootPath(QDir.homePath());

        var tree = new QTreeView();
        tree.setModel(model);
        tree.setHeaderHidden(true);

        selector.layout().addWidget(tree);
        // other construction
    }
}

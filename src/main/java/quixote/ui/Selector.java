package quixote.ui;

import io.qt.gui.QFileSystemModel;
import io.qt.widgets.*;

public class Selector {

    private QWidget selector;

    public Selector(QWidget parent){
        selector = new QWidget(parent);
        selector.setLayout(new QHBoxLayout());

        var tree = new QTreeView();
        tree.setModel(new QFileSystemModel());

        selector.layout().addWidget(tree);
        // other construction
    }
}

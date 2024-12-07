package quixote.ui;

import quixote.core.App;
import quixote.core.Note;
import io.qt.core.QDir;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.gui.QFileSystemModel;
import io.qt.widgets.*;

final public class Selector extends QTreeView {

    public Selector(QWidget parent){
        super(parent);

        parent.layout().addWidget(this);

        var model = new QFileSystemModel();
        model.setRootPath(QDir.homePath());

        this.setModel(model);
        this.setHeaderHidden(true);
    }

    @Override
    public void keyPressEvent(QKeyEvent event){
        if(event.key() == Qt.Key.Key_Return.value()){
            App.app.openNote();
        }
        else{
            super.keyPressEvent(event);
        }
    }
}

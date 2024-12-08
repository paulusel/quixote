package quixote.ui;

import quixote.core.Note;
import quixote.core.TreeModel;


import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;

final public class Selector extends QTreeView {
        TreeModel model = new TreeModel();

    public Selector(QWidget parent){
        super(parent);

        parent.layout().addWidget(this);

        this.setModel(model);
        this.setHeaderHidden(true);
    }

    @Override
    public void keyPressEvent(QKeyEvent event){
        if(event.key() == Qt.Key.Key_Return.value()){
            var item = currentIndex().data(Qt.ItemDataRole.UserRole);
            if(item instanceof Note) {
                App.app.openNote((Note) item);
            }
            else{
                if(model.hasChildren(currentIndex())){
                    this.expand(currentIndex());
                }
            }
        }
        else{
            super.keyPressEvent(event);
        }
    }
}

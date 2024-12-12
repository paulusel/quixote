package quixote.ui;

import quixote.core.Note;
import quixote.core.NoteItem;
import quixote.core.TreeModel;

import java.util.HashMap;

import io.qt.core.QEvent;
import io.qt.core.QModelIndex;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;

final public class Selector extends QTreeView {
    TreeModel model = new TreeModel();
    HashMap<Integer, QKeyEvent> motionMap = new HashMap<>();

    public Selector(QWidget parent){
        super(parent);

        parent.layout().addWidget(this);

        this.setModel(model);
        this.setHeaderHidden(true);

        motionMap.put(Qt.Key.Key_J.value(),
            new QKeyEvent(QEvent.Type.KeyPress, Qt.Key.Key_Down.value(), new Qt.KeyboardModifiers()));
        motionMap.put(Qt.Key.Key_K.value(),
            new QKeyEvent(QEvent.Type.KeyPress, Qt.Key.Key_Up.value(), new Qt.KeyboardModifiers()));

        if(model.rowCount() > 0){
            setCurrentIndex(model.index(0, 0, new QModelIndex()));
        }
    }

    @Override
    public void keyPressEvent(QKeyEvent event){
        var indx = currentIndex();
        var e = motionMap.get(event.key());
        if(e != null){
            super.keyPressEvent(e);
            event.accept();
        }
        else if(event.key() == Qt.Key.Key_H.value()){
            var parent = indx.parent();
            if(isExpanded(indx)) {
                collapse(currentIndex());
            }
            else if(parent.isValid()) {
                collapse(parent);
                setCurrentIndex(parent);
            }
        }
        else if(event.key() == Qt.Key.Key_L.value()) {
            NoteItem item = (NoteItem) indx.data(Qt.ItemDataRole.UserRole);
            if(item instanceof Note){
                App.app.openNote((Note) item);
            }
            else{
                expand(indx);
            }
        }
        else if(event.key() == Qt.Key.Key_Return.value()){
            var index = currentIndex();
            var item = index.data(Qt.ItemDataRole.UserRole);
            if(item instanceof Note) {
                App.app.openNote((Note) item);
            }
            else{
                if(isExpanded(index)){
                    collapse(index);
                }
                else {
                    expand(index);
                }
            }
        }
        else{
            super.keyPressEvent(event);
        }
    }
}

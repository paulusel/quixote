package quixote.ui;

import quixote.core.Note;
import quixote.core.NoteItem;
import quixote.core.Notebook;
import quixote.core.TreeModel;

import java.util.HashMap;

import io.qt.core.QCoreApplication;
import io.qt.core.QEvent;
import io.qt.core.QModelIndex;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;

final public class Selector extends QTreeView {
    TreeModel model = new TreeModel();
    HashMap<Integer, QKeyEvent> motionMap = new HashMap<>();
    QModelIndex yankedIndex;

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
        if(state().value() == QAbstractItemView.State.EditingState.value()){
            super.keyPressEvent(event);
            return;
        }

        // FIXME: This if-else-fi chain too long.
        // FIXME: Shortucuts should be more vim-like
        var indx = currentIndex();
        var e = motionMap.get(event.key());
        if(e != null){
            super.keyPressEvent(e);
            return;
        }
        else if(event.key() == Qt.Key.Key_H.value()){
            var parent = indx.parent();
            if(isExpanded(indx)) {
                collapse(currentIndex());
            }
            else if(parent.isValid()) {
                setCurrentIndex(parent);
                collapse(parent);
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
        else if(event.key() == Qt.Key.Key_A.value()){
            NoteItem item = (NoteItem) indx.data(Qt.ItemDataRole.UserRole);
            QModelIndex parent = item instanceof Notebook ? indx : indx.parent();

            var newIndex = model.addNote(parent);

            setCurrentIndex(newIndex);
            edit(newIndex);
        }
        else if(event.key() == Qt.Key.Key_N.value()){
            NoteItem item = (NoteItem) indx.data(Qt.ItemDataRole.UserRole);
            QModelIndex parent = item instanceof Notebook ? indx : indx.parent();

            var newIndex= model.addNotebook(parent);

            setCurrentIndex(newIndex);
            edit(newIndex);
        }
        else if(event.key() == Qt.Key.Key_R.value()){
            // start renaming notebook or note
            edit(indx);
        }
        else if(event.key() == Qt.Key.Key_X.value()){
            model().removeRow(indx.row(), indx.parent());
        }
        else if(event.key() == Qt.Key.Key_D.value()){
            yankedIndex = indx;
            return;
        }
        else if(event.key() == Qt.Key.Key_P.value()){
            if(yankedIndex != null){
                QModelIndex destParent = indx.data(Qt.ItemDataRole.UserRole) instanceof Notebook
                    ? indx
                    : indx.parent();

                model().moveRows(yankedIndex.parent(), yankedIndex.row(), 1, destParent, 0);
                expand(destParent);
                setCurrentIndex(model().index(0, 0, destParent));
            }
        }

        yankedIndex = null;
        event.accept();
    }

    @Override
    public boolean event(QEvent event){
        if(event.type() == QEvent.Type.KeyPress){
            QKeyEvent e = (QKeyEvent) event;
            if(e.key() == Qt.Key.Key_Tab.value()) 
                return QCoreApplication.sendEvent(App.app, e);
        }

        return super.event(event);
    }
}

package com.quixote.ui;

import com.quixote.core.*;

import java.util.HashMap;

import io.qt.core.QCoreApplication;
import io.qt.core.QEvent;
import io.qt.core.QModelIndex;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;

final public class Selector extends QTreeView {
    private enum PasteAction {COPY, MOVE};
    private TreeModel model = new TreeModel();
    private HashMap<Integer, QKeyEvent> motionMap = new HashMap<>();
    private QModelIndex yankedIndex;
    private PasteAction action;

    public QWidget header;

    public Selector(QWidget parent){
        super(parent);

        header = new QWidget(this);
        new QHBoxLayout(header).addWidget(new QLabel("Notes", header));

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

        // We accept all key-strokes in normal mode
        event.accept();

        // FIXME: This if-else-fi chain too long.
        // FIXME: Shortucuts should be more vim-like

        var indx = currentIndex();
        var e = motionMap.get(event.key());
        if(e != null){
            super.keyPressEvent(e);
            return;
        }
        else if(event.key() == Qt.Key.Key_P.value()){
            if(yankedIndex != null){
                QModelIndex destParent = indx.data(Qt.ItemDataRole.UserRole) instanceof Notebook
                    ? indx
                    : indx.parent();

                if(action == PasteAction.MOVE){
                    model().moveRows(yankedIndex.parent(), yankedIndex.row(), 1, destParent, 0);
                    setCurrentIndex(model().index(0, 0, destParent));
                }
                else {
                    var destIndex = model.copyItem(destParent, (Note) yankedIndex.data(Qt.ItemDataRole.UserRole));
                    setCurrentIndex(destIndex);
                }
                expand(destParent);
                yankedIndex = null;
                action = null;
            }
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
            return;
        }
        else if(event.key() == Qt.Key.Key_L.value()) {
            NoteItem item = (NoteItem) indx.data(Qt.ItemDataRole.UserRole);
            if(item instanceof Note){
                App.app.openNote((Note) item);
            }
            else{
                expand(indx);
            }
            return;
        }

        // Keybindings after this point are not relevant for motion. If these are
        // pressed, it means abort any move operation
        if(yankedIndex != null){
            Statusline.line.displayMsg("Aborted operation");
            yankedIndex = null;
            action = null;
            return;
        }

        if(event.key() == Qt.Key.Key_Return.value()){
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
            // Add new Note
            // FIXME: BUG:- If addition is aborted, the new item stays. It should be removed
            NoteItem item = (NoteItem) indx.data(Qt.ItemDataRole.UserRole);
            QModelIndex parent = item instanceof Notebook ? indx : indx.parent();

            var newIndex = model.addNote(parent);

            setCurrentIndex(newIndex);
            edit(newIndex);
        }
        else if(event.key() == Qt.Key.Key_N.value()){
            // Add new Notebook
            // FIXME: BUG:- If addition is aborted, the new item stays. It should be removed
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
            action = PasteAction.MOVE;
            Statusline.line.displayMsg("Moving item ...");
        }
        else if(event.key() == Qt.Key.Key_Y.value()){
            var currentItem = indx.data(Qt.ItemDataRole.UserRole);
            if(!(currentItem instanceof Note)){
                Statusline.line.displayMsg("Copying notebooks is not supported");
            }
            else {
                yankedIndex = indx;
                action = PasteAction.COPY;
                Statusline.line.displayMsg("Copying item ...");
            }
        }
        else {
            QCoreApplication.sendEvent(App.app, event);
        }
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

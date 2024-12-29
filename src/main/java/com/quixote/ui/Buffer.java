package com.quixote.ui;

import com.quixote.core.*;

import java.util.HashMap;

import io.qt.core.QCoreApplication;
import io.qt.core.QEvent;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.gui.QTextCursor;
import io.qt.widgets.*;

final public class Buffer extends QPlainTextEdit {

    private enum Action {NONE, DELETE, YANK} // NONE is also MOVE Operation

    private Note note;
    private QLabel tab;
    private App.Mode mode = App.Mode.NORMAL;
    private QTextCursor cursor;
    private HashMap<Integer, QTextCursor.MoveOperation> keyMotionMap = new HashMap<>();
    private HashMap<Integer, Integer> keyIntMap = new HashMap<>();
    private Action action = Action.NONE;
    private Integer motion;

    public final Signal1<Buffer> bufferClosed = new Signal1<>();

    public Buffer(QWidget parent, Note note){
        super(parent);

        this.note = note;
        this.tab = new QLabel(note.title(), this);
        this.cursor = new QTextCursor(note.document());
        this.setDocument(note.document());
        this.setLineWrapMode(LineWrapMode.NoWrap);
        this.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        this.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        this.changeMode(mode);

        keyMotionMap.put(Qt.Key.Key_L.value(), QTextCursor.MoveOperation.Right);
        keyMotionMap.put(Qt.Key.Key_H.value(), QTextCursor.MoveOperation.Left);
        keyMotionMap.put(Qt.Key.Key_J.value(), QTextCursor.MoveOperation.Down);
        keyMotionMap.put(Qt.Key.Key_K.value(), QTextCursor.MoveOperation.Up);
        keyMotionMap.put(Qt.Key.Key_W.value(), QTextCursor.MoveOperation.NextWord);
        keyMotionMap.put(Qt.Key.Key_B.value(), QTextCursor.MoveOperation.PreviousWord);
        keyMotionMap.put(Qt.Key.Key_0.value(), QTextCursor.MoveOperation.StartOfLine);
        keyMotionMap.put(Qt.Key.Key_Dollar.value(), QTextCursor.MoveOperation.EndOfLine);
        keyMotionMap.put(Qt.Key.Key_G.value(), QTextCursor.MoveOperation.End);

        keyIntMap.put(Qt.Key.Key_0.value(), 0);
        keyIntMap.put(Qt.Key.Key_1.value(), 1);
        keyIntMap.put(Qt.Key.Key_2.value(), 2);
        keyIntMap.put(Qt.Key.Key_3.value(), 3);
        keyIntMap.put(Qt.Key.Key_4.value(), 4);
        keyIntMap.put(Qt.Key.Key_5.value(), 5);
        keyIntMap.put(Qt.Key.Key_6.value(), 6);
        keyIntMap.put(Qt.Key.Key_7.value(), 7);
        keyIntMap.put(Qt.Key.Key_8.value(), 8);
        keyIntMap.put(Qt.Key.Key_9.value(), 9);
    }

    public void closeBuffer(){
        tab.dispose();
        bufferClosed.emit(this);
    }

    public Note note() {
        return note;
    }

    public QWidget tab(){
        return tab;
    }

    public void refreshName(){
        tab.setText(note.title());
    }

    @Override
    public boolean event(QEvent e){
        if(e.type() != QEvent.Type.KeyPress){
            return super.event(e);
        }

        var event = (QKeyEvent) e;
        if (mode == App.Mode.INSERT) {
            if (event.key() != Qt.Key.Key_Escape.value()) {
                super.event(e);
            }
            else{
                changeMode(App.Mode.NORMAL);
            }
        } else {
            Integer val = keyIntMap.get(event.key());
            if(val != null && event.modifiers().testFlag(Qt.KeyboardModifier.NoModifier)){
                motion = (motion == null ? val : motion * 10 + val);
                Statusline.line.appendCommand(val.toString());
            }
            else if(event.key() == Qt.Key.Key_Escape.value()){
                cursor.clearSelection();
                setTextCursor(cursor);
                changeMode(App.Mode.NORMAL);
            }
            else {
                motion = (motion == null ? 1 : motion);
                Statusline.line.clearCommand();
                if(action == Action.NONE){
                    noneHandler(event);
                }
                else if(action == Action.YANK) {
                    yankHandler(event);
                }
                else if(action == Action.DELETE) {
                    deleteHandler(event);
                }
                motion = null;
            }
        }

        return true;
    }

    public void changeMode(App.Mode mod){
        this.mode = mod;

        action = Action.NONE;
        motion = null;
        Statusline.line.clearCommand();
        Statusline.line.setMode(mode);

        if(mode == App.Mode.INSERT){
            setCursorWidth(1);
        }
        else {
            setCursorWidth(8);
        }
    }

    private void deleteHandler(QKeyEvent event){
        int key = event.key();

        var op = (key == Qt.Key.Key_D.value())
                    ? QTextCursor.MoveOperation.Down
                    : keyMotionMap.get(key);

        if(op != null && ! cursor.hasSelection()){
            if(key == Qt.Key.Key_D.value()){
                cursor.movePosition(QTextCursor.MoveOperation.StartOfLine);
                setTextCursor(cursor);
            }
            cursor.movePosition(op, QTextCursor.MoveMode.KeepAnchor, motion);
        }

        if(cursor.hasSelection()){
            QApplication.clipboard().setText(cursor.selectedText());
            cursor.removeSelectedText();
            setTextCursor(cursor);
            Statusline.line.displayMsg("Deleted", 2000);
        }

        action = Action.NONE;
    }

    private void yankHandler(QKeyEvent event){
        int key = event.key();

        var op = (key == Qt.Key.Key_Y.value())
                    ? QTextCursor.MoveOperation.Down
                    : keyMotionMap.get(key);
        var ncursor = new QTextCursor(cursor);
        if(op != null && ! ncursor.hasSelection()){
            if(key == Qt.Key.Key_Y.value()){
                ncursor.movePosition(QTextCursor.MoveOperation.StartOfLine);
            }
            ncursor.movePosition(op, QTextCursor.MoveMode.KeepAnchor, motion);
        }

        if(ncursor.hasSelection()){
            QApplication.clipboard().setText(ncursor.selectedText());
            Statusline.line.displayMsg("Yanked", 2000);
            ncursor.clearSelection();
        }

        setTextCursor(cursor);// Set the original cursor
        action = Action.NONE;
    }

    private void noneHandler(QKeyEvent event){
        int key = event.key();
        if(mode == App.Mode.NORMAL){
            if(key == Qt.Key.Key_D.value()){
                action = Action.DELETE;
                Statusline.line.appendCommand("d");
                return;
            }
            else if(key == Qt.Key.Key_Y.value()){
                Statusline.line.appendCommand("y");
                action = Action.YANK;
                return;
            }
            var op = keyMotionMap.get(key);
            if(op != null){
                cursor.movePosition(op, QTextCursor.MoveMode.MoveAnchor, motion);
                setTextCursor(cursor);
                return;
            }

            var mods = event.modifiers().flags();
            if(key == Qt.Key.Key_X.value()) {
                cursor.deleteChar();
                setTextCursor(cursor);
            }
            else if(key == Qt.Key.Key_I.value()) {
                changeMode(App.Mode.INSERT);
            }
            else if(key == Qt.Key.Key_V.value()){
                changeMode(App.Mode.VISUAL);
            }
            else if(key == Qt.Key.Key_Q.value()) {
                // Call the closeBuffer method when 'Q' is pressed
                closeBuffer();
            }
            else if(key == Qt.Key.Key_O.value() && mods.length == 1) {
                if(mods[0] == Qt.KeyboardModifier.NoModifier){
                    cursor.movePosition(QTextCursor.MoveOperation.EndOfLine);
                    cursor.insertText("\n");
                    setTextCursor(cursor);
                    changeMode(App.Mode.INSERT);
                }
                else if(mods[0] == Qt.KeyboardModifier.ShiftModifier){
                    cursor.movePosition(QTextCursor.MoveOperation.StartOfLine);
                    cursor.insertText("\n");
                    cursor.movePosition(QTextCursor.MoveOperation.Up);
                    setTextCursor(cursor);
                    changeMode(App.Mode.INSERT);
                }
            }
            else if(key == Qt.Key.Key_A.value() && mods.length == 1) {
                if(mods[0] == Qt.KeyboardModifier.NoModifier){
                    cursor.movePosition(QTextCursor.MoveOperation.Right);
                    setTextCursor(cursor);
                }
                else if(mods[0] == Qt.KeyboardModifier.ShiftModifier){
                    cursor.movePosition(QTextCursor.MoveOperation.EndOfLine);
                    setTextCursor(cursor);
                }
                changeMode(App.Mode.INSERT);
            }
            else if(key == Qt.Key.Key_U.value()){
                if(mods[0] == Qt.KeyboardModifier.NoModifier){
                    if(document().isUndoAvailable()){
                        document().undo(cursor);
                        Statusline.line.displayMsg("Undone", 2000);
                    }
                    else {
                        Statusline.line.displayMsg("Already at the earliest change", 2000);
                    }
                }
            }
            else if(key == Qt.Key.Key_R.value()){
                if(mods.length == 1 && mods[0] == Qt.KeyboardModifier.ControlModifier){
                    if(document().isRedoAvailable()){
                        document().redo(cursor);
                        Statusline.line.displayMsg("Redone", 2000);
                    }
                    else {
                        Statusline.line.displayMsg("Already at the newest change", 2000);
                    }
                }
            }
            else if(key == Qt.Key.Key_P.value()){
                if(mods.length == 1 && mods[0] == Qt.KeyboardModifier.NoModifier){
                    cursor.insertText(QApplication.clipboard().text());
                    setTextCursor(cursor);
                }
                else if(mods.length == 1 && mods[0] == Qt.KeyboardModifier.ShiftModifier){
                    // TODO: Paste before
                }
            }
            else {
                QCoreApplication.sendEvent(App.app, event);
            }
        }
        else if(mode == App.Mode.VISUAL){
            var op = keyMotionMap.get(key);
            if(op != null){
                cursor.movePosition(op, QTextCursor.MoveMode.KeepAnchor, motion);
                setTextCursor(cursor);
                return;
            }

            if(key == Qt.Key.Key_Y.value()){
                yankHandler(event);
            }
            else if(key == Qt.Key.Key_D.value()){
                deleteHandler(event);
            }
            changeMode(App.Mode.NORMAL);
        }
    }
}

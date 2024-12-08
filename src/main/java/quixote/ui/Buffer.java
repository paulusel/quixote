package quixote.ui;

import quixote.core.*;

import io.qt.widgets.QPlainTextEdit;
import io.qt.core.QCoreApplication;
import io.qt.core.QEvent;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.gui.QTextCursor;

final public class Buffer extends QPlainTextEdit {
    private Note note;
    private boolean insertMode = true;
    private QTextCursor cursor;

    public Buffer(Note note){
        this.note = note;
        this.setDocument(note.document());
        this.cursor = new QTextCursor(note.document());
        this.setLineWrapMode(LineWrapMode.NoWrap);
    }

    public void closeBuffer(){

    }

    @Override
    public boolean event(QEvent e){
        //TODO: Use hash-map to speed-up key-press lookup
        if(e.type() == QEvent.Type.KeyPress){
            int key = ((QKeyEvent)e).key();
            if(insertMode){
                if(key != Qt.Key.Key_Escape.value()){
                    super.event(e);
                    return true;
                }
            }
            else{
                // Edit/move shotcuts
                if(key == Qt.Key.Key_L.value()){
                    cursor.movePosition(QTextCursor.MoveOperation.Right);
                    setTextCursor(cursor);
                }
                else if(key == Qt.Key.Key_H.value()){
                    cursor.movePosition(QTextCursor.MoveOperation.Left);
                    setTextCursor(cursor);
                }
                else if(key == Qt.Key.Key_J.value()){
                    cursor.movePosition(QTextCursor.MoveOperation.Down);
                    setTextCursor(cursor);
                }
                else if(key == Qt.Key.Key_K.value()){
                    cursor.movePosition(QTextCursor.MoveOperation.Up);
                    setTextCursor(cursor);
                }
                else if(key == Qt.Key.Key_X.value()){
                    cursor.deleteChar();
                    setTextCursor(cursor);
                }
            }

            QCoreApplication.sendEvent(App.app, e);
            return true;
        }

        return super.event(e);
    }

    public void changeMode(String mode){
        if(mode.equals("INSERT")){
            insertMode = true;
            setCursorWidth(1);
        }
        else {
            insertMode = false;
            setCursorWidth(8);
        }
    }
}

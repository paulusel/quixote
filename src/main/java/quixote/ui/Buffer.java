package quixote.ui;

import quixote.core.*;

import java.util.HashMap;
import java.util.function.Consumer;

import io.qt.core.QCoreApplication;
import io.qt.core.QEvent;
import io.qt.core.Qt;
import io.qt.gui.QKeyEvent;
import io.qt.gui.QTextCursor;
import io.qt.widgets.*;

final public class Buffer extends QPlainTextEdit {
    private Note note;
    private QLabel tab;
    private boolean insertMode = true;
    private QTextCursor cursor;
    private HashMap<Integer, QTextCursor.MoveOperation> keyMotionMap = new HashMap<>();
    private Consumer<QTextCursor.MoveOperation> moveCursor;

    public final Signal1<Buffer> bufferClosed = new Signal1<>();

    public Buffer(Note note){
        this.note = note;
        this.tab = new QLabel(note.title(), this);
        this.setDocument(note.document());
        this.cursor = new QTextCursor(note.document());
        this.setLineWrapMode(LineWrapMode.NoWrap);

        keyMotionMap.put(Qt.Key.Key_L.value(), QTextCursor.MoveOperation.Right);
        keyMotionMap.put(Qt.Key.Key_H.value(), QTextCursor.MoveOperation.Left);
        keyMotionMap.put(Qt.Key.Key_J.value(), QTextCursor.MoveOperation.Down);
        keyMotionMap.put(Qt.Key.Key_K.value(), QTextCursor.MoveOperation.Up);
        keyMotionMap.put(Qt.Key.Key_W.value(), QTextCursor.MoveOperation.NextWord);
        keyMotionMap.put(Qt.Key.Key_B.value(), QTextCursor.MoveOperation.PreviousWord);
        keyMotionMap.put(Qt.Key.Key_0.value(), QTextCursor.MoveOperation.StartOfLine);
        keyMotionMap.put(Qt.Key.Key_Dollar.value(), QTextCursor.MoveOperation.EndOfLine);
        //keyMotionMap.put(Qt.Key.Key_G.value(), QTextCursor.MoveOperation.StartOfLine);
        keyMotionMap.put(Qt.Key.Key_G.value(), QTextCursor.MoveOperation.End);

        moveCursor = (QTextCursor.MoveOperation op) -> {
            cursor.movePosition(op);
            setTextCursor(cursor);
        };
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

    @Override
    public boolean event(QEvent e){
         if (e.type() == QEvent.Type.KeyPress) {
            int key = ((QKeyEvent) e).key();
            if (insertMode) {
                if (key != Qt.Key.Key_Escape.value()) {
                    super.event(e);
                }
                else{
                    QCoreApplication.sendEvent(App.app, e);
                }
            } else {
                // Edit/move shotcuts in Normal Mode
                var moveOp = keyMotionMap.get(key);
                if(moveOp != null){
                    moveCursor(moveOp);
                }
                else if(key == Qt.Key.Key_X.value()) {
                    cursor.deleteChar();
                    setTextCursor(cursor);
                }
                else if(key == Qt.Key.Key_Q.value()) {
                    // Call the closeBuffer method when 'Q' is pressed
                    closeBuffer();
                }
                else {
                    QCoreApplication.sendEvent(App.app, e);
                }
            }

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

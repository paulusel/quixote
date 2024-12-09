package quixote.ui;

import quixote.core.*;

import java.util.HashMap;
import java.util.Map;

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

    public final Signal1<Buffer> bufferClosed = new Signal1<>();

    public Buffer(Note note){
        this.note = note;
        this.tab = new QLabel(note.title(), this);
        this.setDocument(note.document());
        this.cursor = new QTextCursor(note.document());
        this.setLineWrapMode(LineWrapMode.NoWrap);
    }

    public void closeBuffer(){
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
                    return true;
                }
            } else {
                // Edit/move shotcuts in Normal Mode
                Map<Integer, Runnable> keyActions = new HashMap<>();
                keyActions.put(Qt.Key.Key_L.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.Right);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_H.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.Left);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_J.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.Down);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_K.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.Up);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_W.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.NextWord);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_B.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.PreviousWord);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_0.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.StartOfLine);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_Dollar.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.EndOfLine);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_G.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.StartOfLine);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_G.value(), () -> { cursor.movePosition(QTextCursor.MoveOperation.End);
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_X.value(), () -> { cursor.deleteChar();
                    setTextCursor(cursor);
                });
                keyActions.put(Qt.Key.Key_Q.value(), () -> {
                    // Call the closeBuffer method when 'Q' is pressed
                    closeBuffer();
                });

                Runnable action = keyActions.get(key);
                if (action != null) {
                    action.run();
                    return true;
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

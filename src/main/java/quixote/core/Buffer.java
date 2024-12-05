package quixote.core;

import quixote.core.Note;

import io.qt.widgets.QPlainTextEdit;
import io.qt.gui.QTextCursor;

final public class Buffer extends QPlainTextEdit {
    private Note note;

    public Buffer(Note note){
        this.note = note;
        this.setDocument(note.document);
        this.setLineWrapMode(LineWrapMode.NoWrap);
    }

    public void closeBuffer(){

    }
}

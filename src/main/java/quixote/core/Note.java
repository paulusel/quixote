package quixote.core;

import io.qt.gui.QTextDocument;
import io.qt.widgets.QPlainTextDocumentLayout;

public class Note {
    public QTextDocument document;
    public String title;
    public String notebook;

    public Note(){
        document = new QTextDocument();
        document.setDocumentLayout(new QPlainTextDocumentLayout(document));
        title = new String();
        notebook = new String();
    }
}

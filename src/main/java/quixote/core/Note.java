package quixote.core;

import io.qt.gui.QTextDocument;
import io.qt.widgets.QPlainTextDocumentLayout;

public class Note extends NoteItem {
    private QTextDocument doc;

    public Note(Notebook notebook, String title, String txt, int id){
        super(notebook, title, id);
        doc= new QTextDocument(txt);
        doc.setDocumentLayout(new QPlainTextDocumentLayout(doc));
    }

    public QTextDocument document(){
        return doc;
    }
}

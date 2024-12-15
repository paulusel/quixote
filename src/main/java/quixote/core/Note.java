package quixote.core;

import io.qt.gui.QTextDocument;
import io.qt.widgets.QPlainTextDocumentLayout;

import java.util.Date;

public class Note extends NoteItem {
    private QTextDocument doc = new QTextDocument();
    private Date modified = new Date();

    public Note(Notebook notebook, String title, String txt, int id){
        super(notebook, title, id);
        doc.setPlainText(txt);
        doc.setDocumentLayout(new QPlainTextDocumentLayout(doc));
        if (title == null || title.isEmpty() ) {
            title(modified.toString());
        }
    }

    public QTextDocument document(){
        return doc;
    }

    public Date modified(){
        return modified;
    }
}

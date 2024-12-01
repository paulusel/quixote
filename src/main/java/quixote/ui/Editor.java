package quixote.ui;

import io.qt.widgets.*;

public class Editor {
    private Statusline statusline;
    private Tabline tabline;
    private QWidget buffers;
    private QWidget editor;

    public Editor(QWidget parent){
        editor = new QWidget(parent);
        editor.setLayout(new QVBoxLayout());

        buffers = new QWidget(editor);
        buffers.setLayout(new QStackedLayout());

        tabline = new Tabline(editor);

        // one text edit
        buffers.layout().addWidget(new QTextEdit());
        editor.layout().addWidget(buffers);

        statusline = new Statusline(editor);
        //statusline.register(container);
    }
}

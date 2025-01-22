package com.quixote.ui;

import com.quixote.core.*;

import io.qt.gui.QAction;
import io.qt.gui.QKeyEvent;
import io.qt.widgets.*;
import io.qt.core.QEvent;
import io.qt.core.Qt;

import java.sql.SQLException;

public class App extends QWidget {

    public enum Mode {NORMAL, INSERT, VISUAL};

    public static App app;
    public static Database db;

    private QWidget viewArea;
    private QWidget header;
    private QStackedLayout hLayout;
    private QStackedLayout layout; // layout manager for viewArrea
    private Editor editor;
    private Selector selector;

    private final String helpText = """

    There two sets of keybindings in effect depending current view.

    1. Editor View
    ```````````
    These commands handle note editing in active buffer, 
    navigation or buffer management(e.g closing buffer).

    The format of commands is this:

        [command] + [number] + [movement]

    Where

    - [command] can be Y for yank or copy, [d] for delete and none.
    - [number] is non negative number.
    - movement is one of Movement keys below. In addition to those,
      two more movements are available depending on the command.

        * Y with Y/Yank command
        * D with D/Delete command

    Examples:

        2w - move two words forward (command none)
        d2d - delete two full lines starting with current one (d command)
        yy - copy one full line to clipboard (y command)

    Each of them repesent a movement covering whole line from start to end,
    unlike K and J which start from current curson position.

    If no number is given, 1 is assumed. Otherwise, [command] and [movement]
    are repeated [number] times.

    The command D deletes text covered by [movement] repeated when [number]
    times whereas Y copies it to clipboard. When no command is given, only
    cursor is moved.


    Movement

    J - Down one line
    K - Up one line
    H - Left one character
    L - Right one character
    0 - Start of line
    $ - End of Line
    W - Next word
    B - Previous Word
    G - Start of document
    Shift+G - End of document

    INSERT Mode

    In insert mode all key-presses except [Esc] are directly
    put into text being edited. [Esc] puts you in NORMAL mode.

    NORMAL Mode

    In normal mode, all key bindings are interpreted as commands.

    P - Paste
    Y - Start yank operation
    D - Start delete operation
    X - Delete character under cursor
    I - Enter insert mode
    V - Enter visual mode
    Esc - Change to normal mode/Abort current operation
    O - Insert new line after current line and start editing it
    Shift + O - Insert new above current line and start editing it
    a - Start inserting after character under cursor
    A - Go to end of current line and enter insert mode
    U - undo
    Control + R - redo

    In addition to these, few control commands are also present.

    Q - close active buffer
    ? - Open this help in readonly buffer

    NOTE: Only Y, D and no command support command structure
        outlined at the beginning of this section. The rest
        are non-repeatable, single shot operations.

    VISUAL Mode

    In visual mode, text is selected interactively. When selection
    is complete, you can press either Y or D to copy or delete the
    sected text.

    The selection process supports only no command format of
    command structure. Meaning only

        [number] + [movement]

    is accepted. NOTE: only Y and D movements are not supported
    as they are only applicable to Y and D commands which are
    absent in visual mode.

    2. Selector View

    These set of commands deal with notes/notebooks: creating,
    deleting, moving, renaming etc.

    A - Add new Note
    N - Add new Notebook
    J - Move one item Down
    K - Move one item Up
    Y - Copy
    D - Cut
    P - Paste copied/cut item
    R - Rename
    X - Remove
    ? - Open this help in buffer in readonly mode.
    """;

    private final Note help = new Note(null, "Help", helpText, 0);

    final private String stylesheet = """
            * {
                background: #3D3D3D;
            }
    """;

    private static final int key_tab = Qt.Key.Key_Tab.value();

    public static void initialize(String[] args){
        if(app != null)
            return;

        try{
            QApplication.initialize(args);
            QApplication.setCursorFlashTime(0);
            db = new Database(); // This must preceed new App()
            app = new App();
        }
        catch(SQLException e){
            System.out.println("FATAL: Unable to intialize database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        catch(Exception e){
            System.out.println("FATAL: Unknow error occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // constructors
    private App(){
        this.setWindowTitle("Quixote");
        this.setStyleSheet(stylesheet);

        var blayout = new QVBoxLayout(this);
        blayout.setSpacing(0);

        // header
        header = new QWidget(this);
        hLayout = new QStackedLayout(header);

        // View Area
        viewArea = new QWidget(this);
        layout = new QStackedLayout(viewArea);

        // Statusline
        Statusline.init(this);

        // view Area elements
        selector = new Selector(viewArea);
        editor = new Editor(viewArea);
        viewArea.layout().addWidget(selector);
        viewArea.layout().addWidget(editor);

        // header elements
        header.layout().addWidget(selector.header);
        header.layout().addWidget(editor.header);

        // Add it all to the app
        this.layout().addWidget(header);
        this.layout().addWidget(viewArea);
        this.layout().addWidget(Statusline.line);

        blayout.setStretch(0, 0); // header (no stretch)
        blayout.setStretch(1, 1); // ViewArea (takes remaining space)
        blayout.setStretch(2, 0); // statusline (no stretch)


        // connect slots
        editor.editorEmpty.connect(this::switchView);
        selector.model().dataChanged.connect(editor::itemEdited);

        // Quit shortcut
        var quitAction = new QAction();
        quitAction.setShortcut("CTRL+Q");
        quitAction.triggered.connect(this::stop);
        this.addAction(quitAction);
    }

    public void start() {
        this.show();
        QApplication.exec();
    }

    public void stop() {
        this.hide();
        cleanup();
        this.dispose();
        QApplication.quit();
    }

    public void openNote(Note note){
        editor.newBuffer(note, false);
        switchView();
    }

    private void cleanup(){
        editor.save();
        db.shutdown();
    }

    private void switchView(){
        if(layout.currentWidget() == selector && ! editor.isEmpty()) {
            layout.setCurrentWidget(editor);
            hLayout.setCurrentWidget(editor.header);
        }
        else if(layout.currentWidget() == editor){
            layout.setCurrentWidget(selector);
            hLayout.setCurrentWidget(selector.header);
            selector.setFocus();
        }
    }

    @Override
    public boolean event(QEvent e){
         if(e.type() == QEvent.Type.KeyPress){
            var event = (QKeyEvent) e;
            var modifiers = event.modifiers();

            int key = event.key();
            if(key == key_tab){
                // toggle selector/editor
               switchView();
            }
            else if(key == Qt.Key.Key_Space.value()) {
                // HACK: this should be handled in editor
                if(modifiers.testFlag(Qt.KeyboardModifier.ShiftModifier)){
                    editor.showPrev();
                }
                else if(modifiers.testFlag(Qt.KeyboardModifier.NoModifier)){
                    editor.showNext();
                }
            }
            else if(key == Qt.Key.Key_Question.value()) {
                editor.newBuffer(help, true);
                layout.setCurrentWidget(editor);
                hLayout.setCurrentWidget(editor.header);
            }

            return true;
        }

        return super.event(e);
    }
}

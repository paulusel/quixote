package quixote.core;

import java.util.HashMap;

import io.qt.core.QAbstractItemModel;
import io.qt.core.QModelIndex;
import io.qt.core.Qt;

public class TreeModel extends QAbstractItemModel {
    private Notebook root = new Notebook(null, "", -1);
    private HashMap<Long, NoteItem> itemMap = new HashMap<>();

    public TreeModel(){
        // FIXME: this is for testing should be replaced with real code that populates from storage
        NoteItem item = new Note(root, "Test", "This is test note", 1);
        root.addItem(item);
        itemMap.put(Long.valueOf(item.hashCode()), item);

        item = new Notebook(root, "Testbook", 2);
        root.addItem(item);
        itemMap.put(Long.valueOf(item.hashCode()), item);

        Notebook book = (Notebook)root.itemAt(1);

        item = new Note(book, "TestNote", "Note 2", 3);
        book.addItem(item);
        itemMap.put(Long.valueOf(item.hashCode()), item);

        item = new Note(book, "Note", "Another note", 4);
        book.addItem(item);
        itemMap.put(Long.valueOf(item.hashCode()), item);
    }

    @Override
    public int rowCount(QModelIndex pItem){
        NoteItem item = pItem.isValid()
            ? itemMap.get(pItem.internalId())
            : root;

        if(!(item instanceof Notebook))
            return 0;

        return ((Notebook) item).itemCount();
    }

    @Override
    public QModelIndex parent(QModelIndex child){
        if(!child.isValid()) // child itself is root, so has no position
            return new QModelIndex();

        NoteItem pItem = itemMap.get(child.internalId()).parent();
        return pItem == root
            ? new QModelIndex() // parent is root, ergo has no position
            : createIndex(pItem.pos(), 0, pItem.hashCode());
    }

    @Override
    public QModelIndex index(int row, int column, QModelIndex parent){
        if(!hasIndex(row, column, parent))
            return new QModelIndex();

        NoteItem pItem = parent.isValid()
            ? (NoteItem) parent.data(Qt.ItemDataRole.UserRole)
            : root;

        NoteItem item = ((Notebook)pItem).itemAt(row);
        return item == null
            ? new QModelIndex() // row not found. FIXME: SHOULD never happen though, MAYBE assert?
            : createIndex(row, 0, item.hashCode());
    }

    @Override
    public Object data(QModelIndex index, int role){
        if(role == Qt.ItemDataRole.UserRole) {
            if(!index.isValid())
                return root;
            else{
                return itemMap.get(index.internalId());
            }
        }
        else if(role == Qt.ItemDataRole.DisplayRole){
            return itemMap.get(index.internalId()).title();
        }

        // For all other data roles
        return null;
    }

    @Override
    public int columnCount(QModelIndex index){
        return 1;
    }
}


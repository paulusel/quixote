package quixote.core;

import java.util.ArrayList;
import java.util.HashMap;

import io.qt.core.QAbstractItemModel;
import io.qt.core.QList;
import io.qt.core.QModelIndex;
import io.qt.core.Qt;

import quixote.ui.App;

public class TreeModel extends QAbstractItemModel {
    private Notebook root = new Notebook(null, "root", 1);
    private HashMap<Long, NoteItem> itemMap = new HashMap<>();

    public TreeModel(){
        ArrayList<Notebook> que = new ArrayList<>();
        que.add(root);
        while(!que.isEmpty()){
            var parnt = que.get(0);
            que.remove(0);

            var list = App.db.getItems(parnt);
            for(NoteItem item : list){
                parnt.addItem(item);
                itemMap.put(Long.valueOf(item.hashCode()), item);
                if(item instanceof Notebook){
                    que.add((Notebook) item);
                }
            }
        }
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
            ? itemMap.get(parent.internalId())
            : root;

        NoteItem item = ((Notebook)pItem).itemAt(row);
        return item == null
            ? new QModelIndex() // row not found. FIXME: SHOULD never happen though, MAYBE assert?
            : createIndex(row, 0, item.hashCode());
    }

    @Override
    public Object data(QModelIndex index, int role){
        if(role == Qt.ItemDataRole.DisplayRole){
            if(index.isValid()){
                return itemMap.get(index.internalId()).title();
            }
            else{
                return root;
            }
        }
        else if(role == Qt.ItemDataRole.UserRole) {
            return itemMap.get(index.internalId());
        }
        // For all other data roles
        return null;
    }

    @Override
    public int columnCount(QModelIndex index){
        return 1;
    }

    @Override
    public boolean setData(QModelIndex index, Object value, int role){
        NoteItem item = (NoteItem) index.data(Qt.ItemDataRole.UserRole);
        item.title(value.toString());
        App.db.save(item);
        dataChanged.emit(index, index, QList.of(1));

        return true;
    }

    @Override
    public Qt.ItemFlags flags(QModelIndex index){
        return super.flags(index).combined(Qt.ItemFlag.ItemIsEditable);
    }

    @Override
    public boolean removeRows(int row, int count, QModelIndex parent){
        Notebook nbook = parent.isValid()
            ? (Notebook) parent.data(Qt.ItemDataRole.UserRole)
            : root;

        beginRemoveRows(parent, row, row);
        // Begin removing

        NoteItem item = nbook.itemAt(row);
        itemMap.remove(Long.valueOf(item.hashCode()));
        App.db.removeItem(item);
        nbook.removeItemAt(row);

        ArrayList<Notebook> que = new ArrayList<>();
        if(item instanceof Notebook)
            que.add((Notebook) item);
        while(!que.isEmpty()){
            var list = que.get(0).children();
            que.remove(0);
            for (NoteItem i : list){
                //App.db.removeItem(i);
                itemMap.remove(Long.valueOf(i.hashCode()));
                if(i instanceof Notebook){
                    que.add((Notebook) i);
                }
            }
        }

        // End removing
        endRemoveRows();

        return true;
    }

    @Override
    public boolean moveRows(QModelIndex srcParent, int srcRow, int count, QModelIndex destParent, int destRow){
        beginMoveRows(srcParent, srcRow, srcRow+count-1, destParent, destRow);
        // Begin moving
        //
        // FIXME: For now count and destRow are ignored, assuming only one count and beginning/end
        Notebook srcBook = (Notebook) srcParent.data(Qt.ItemDataRole.UserRole);
        Notebook destBook = (Notebook) destParent.data(Qt.ItemDataRole.UserRole);

        NoteItem item = srcBook.itemAt(srcRow);
        srcBook.removeItemAt(srcRow);
        item.parent(destBook);
        App.db.save(item);

        if(item instanceof Note){
            destBook.children().add(0, item);
        }
        else{
            destBook.children().add(item);
        }

        // End moving
        endMoveRows();
        return true;
    }

    public QModelIndex addNote(QModelIndex parent){
        Notebook nbook = parent.isValid()
            ? (Notebook) parent.data(Qt.ItemDataRole.UserRole)
            : root;

        Note note = App.db.insertNote(nbook);

        beginInsertRows(parent, 0, 0);
        // Begin inserting

        nbook.addItem(note, 0);
        itemMap.put(Long.valueOf(note.hashCode()), note);

        // End inserting
        endInsertRows();

        return index(0, 0, parent);
    }

    public QModelIndex addNotebook(QModelIndex parent){
        Notebook nbook = parent.isValid()
            ? (Notebook) parent.data(Qt.ItemDataRole.UserRole)
            : root;

        Notebook nb = App.db.insertNotebook(nbook);
        int pos = rowCount(parent);

        beginInsertRows(parent, pos, pos);
        // Begin inserting

        nbook.addItem(nb);
        itemMap.put(Long.valueOf(nb.hashCode()), nb);

        // End inserting
        endInsertRows();

        return index(pos, 0, parent);
    }
}


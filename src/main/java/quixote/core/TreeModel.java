package quixote.core;

import java.util.ArrayList;
import java.util.HashMap;

import io.qt.core.QAbstractItemModel;
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
            return itemMap.get(index.internalId()).title();
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
}


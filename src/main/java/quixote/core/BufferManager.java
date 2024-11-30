package quixote.core;

import java.util.ArrayList;

public class BufferManager {
    //static fields
    private static BufferManager mngr;

    private ArrayList<Buffer> buffers;

    private BufferManager(){} // no construction

    public static BufferManager manager(){
        if(BufferManager.mngr == null){
            mngr = new BufferManager();
        }

        return BufferManager.mngr;
    }


    public Buffer newBuffer(){
        var newBffr = new Buffer();
        buffers.add(newBffr);
        return newBffr;
    }

    //public Buffer activeBuffer(){
    //    return buffers.getLast();
    //}

    public void showNext(){

    }

    public void showPrev(){

    }

    public void show(int offset){

    }

    public void closeCurrent(){

    }
}

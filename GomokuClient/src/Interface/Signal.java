package Interface;

/**
 * Created by nim_13512501 on 12/3/15.
 */
public class Signal {
    private boolean cont;
    public void stopAndWait() throws InterruptedException {
        synchronized(this){
            cont = false;
            while (!cont)
                wait();
        }
    }
    public void tellToGo(){
        synchronized(this){
            cont = true;
            notifyAll();
        }
    }
}

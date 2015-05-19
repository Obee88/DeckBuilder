package custom.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Obee
 * Date: 19/05/15
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
public class StopWatch {
    List<Long> checkpoints = new ArrayList<Long>();

    public StopWatch(){
    }

    public void start(){
        long now = System.currentTimeMillis();
        checkpoints.clear();
        checkpoints.add(now);
    }

    public void checkpoint(String name){
        int size = checkpoints.size();
        long last = checkpoints.get(size-1);
        long now = System.currentTimeMillis();
        checkpoints.add(now);
        long time = now-last;
        log(name, time);
    }

    private void log(String name, long time) {
        System.out.println(name+" --> "+time/1000.0);
    }
}

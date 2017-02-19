import java.util.*;
import java.util.concurrent.Callable;

public class Joiner implements Callable<Set<Long>> {
    private Collection<Long> s;
    Joiner( Collection<Long> s ) {
        this.s = s;
    }
    public Set<Long> call() throws Exception {
        Set<Long> res = new HashSet<>();
        DataHolder dh = null;
        try {
            dh = DataHolder.getInstance(null);
        } catch ( Exception io ) {
            System.out.println("[Joiner.call():] "+io.getMessage());
            io.printStackTrace();
        }
        for ( Long x: s )
            for ( Long y: s )
                if ( dh.compatible(x,y) ) {
                    res.add(x | y);
                    /*dh.addExtension(x,x|y);
                    dh.addExtension(y,x|y);*/
                }
        return res;
    }
}


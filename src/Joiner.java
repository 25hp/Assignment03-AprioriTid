import java.util.*;

public class Joiner extends Thread {
    private Collection<Long> s;
    public Set<Long> res = new HashSet<>();
    Joiner( Collection<Long> s ) {
        this.s = s;
    }
    public void run() {
        DataHolder dh = null;
        try {
            dh = DataHolder.getInstance(null);
        } catch ( Exception io ) {
            System.out.println("[Joiner.run():] "+io.getMessage());
            io.printStackTrace();
        }
        for ( Long x: s )
            for ( Long y: s )
                if ( dh.compatible(x,y) ) {
                    res.add(x | y);
                    dh.addExtension(x,x|y);
                    dh.addExtension(y,x|y);
                }
    }
}


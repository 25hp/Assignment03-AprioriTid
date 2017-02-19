import java.io.BufferedReader;
import java.util.*;

public class AprioriTid {

    private final double minsup;
    private DataHolder dataHolder;
    private Map<Integer,Set<Long>> L = new HashMap<>();
    private Map<Integer,Set<CandItemsetsTID>> CC = new HashMap<>();
    private Map<Integer,Set<Long>> C = new HashMap<>();

    public AprioriTid(double s, BufferedReader br ) {
        minsup = s;
        try {
            dataHolder = DataHolder.getInstance(br);
        } catch ( Exception io ) {
            System.out.println("[AprioriTid]: constructor "+io.getMessage());
            io.printStackTrace();
        }
    }

    private class CandItemsetsTID implements Comparable<CandItemsetsTID> {
        final long id;
        @Override
        public int compareTo( CandItemsetsTID other ) {
            return id == other.id?0:(id<other.id?-1:1);
        }
        private Set<Long> itemsets = new HashSet<>();
        CandItemsetsTID( long id ) {
            this.id = id;
        }
        CandItemsetsTID( long id, Set<Long> itemsets ) {
            this.id = id;
            this.itemsets = itemsets;
        }
        void addItemset( long t ) {
            itemsets.add(t);
        }
        boolean itemsetPresent( long t ) {
            return itemsets.contains(t);
        }
    }

    private void buildFirstItemset() {
        Map<Long,Integer> h = dataHolder.retrieveAll();
        Map<Long,Integer> singles = new HashMap<>();
        for ( Map.Entry<Long,Integer> entry: h.entrySet() )
            for (int j = 0; j < dataHolder.getN(); ++j) {
                long val = dataHolder.readAttribute(entry.getKey(), j);
                assert val != 0;
                if (singles.containsKey(val))
                    singles.put(val, singles.get(val) + entry.getValue());
                else singles.put(val, entry.getValue());
            }
        dataHolder.addAll(singles);
        L.put(1,new TreeSet<>());
        for ( Map.Entry<Long,Integer> entry: singles.entrySet() )
            if ( dataHolder.getSupport(entry.getKey()) >= minsup )
                L.get(1).add(entry.getKey());
        CC.put(1,new TreeSet<>());
        for ( Map.Entry<Long,Integer> entry: h.entrySet() ) {
            CandItemsetsTID cc = new CandItemsetsTID(entry.getKey());
            for ( int j = 0; j < dataHolder.getN(); ++j )
                cc.addItemset(dataHolder.readAttribute(entry.getKey(),j));
            CC.get(1).add(cc);
        }
    }

    private Set<Long> generateCandidates(Set<Long> l ) {
        Map<Long,Set<Long>> buckets = new HashMap<>();
        Set<Long> res = new HashSet<>();
        for ( Long t: l ) {
            Long pt = dataHolder.removeTopItem(t);
            if ( !buckets.containsKey(pt) )
                buckets.put(pt,new HashSet<>());
            buckets.get(pt).add(t);
        }
        List<Joiner> lst = new ArrayList<>();
        for ( Map.Entry<Long,Set<Long>> entry: buckets.entrySet() ) {
            Joiner t = new Joiner(entry.getValue());
            t.start();
            lst.add(t);
        }
        for ( Joiner j: lst )
            try {
                j.join();
                res.addAll(j.res);
            } catch ( InterruptedException ie ) {
                System.out.println("[generateCandidates]: "+ie.getMessage());
                ie.printStackTrace();
            }
        return res ;
    }

    public Map<Integer,Set<Long>> findAllLargeItemsets() {
        int k;
        buildFirstItemset();
        /**
         * generateCandidates()
         */
        for ( k = 1; !L.get(k).isEmpty(); ++k ) {
            C.put(k+1, generateCandidates(L.get(k)));
            CC.put(k+1,new TreeSet<>());
            for ( CandItemsetsTID cis: CC.get(k) ) {
                Set<Long> C_t = new HashSet<>();
                for ( Long x: C.get(k+1) ) {
                    assert dataHolder.cardinality(x) == k+1: "k = "+k;
                    assert dataHolder.cardinality(x) >= 2;
                    Long px = dataHolder.removeTopItem(x), ppx = dataHolder.removeTopItem(px)|dataHolder.getTopItem(x);
                    assert Long.bitCount(dataHolder.getSignature(px)&dataHolder.getSignature(ppx)) == k-1: px+" "+ppx;
                    assert Long.bitCount(dataHolder.getSignature(px)^dataHolder.getSignature(ppx)) == 2;
                    assert Long.bitCount(dataHolder.getSignature(px)) == k;
                    assert Long.bitCount(dataHolder.getSignature(ppx)) == k;
                    if ( cis.itemsetPresent(px) && cis.itemsetPresent(ppx) )
                        C_t.add(x);
                }
                for ( Long c: C_t )
                    dataHolder.addWeight(c,dataHolder.getWeight(cis.id));
                if ( !C_t.isEmpty() )
                    CC.get(k+1).add(new CandItemsetsTID(cis.id,C_t));
            }
            /**
             * pruneItemset()
             */
            L.put(k+1,new HashSet<>());
            for ( Long c: C.get(k+1) )
                if ( dataHolder.getSupport(c) >= minsup )
                    L.get(k+1).add(c);
        }
        return L;
    }
}


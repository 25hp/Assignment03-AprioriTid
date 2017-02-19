/**
 * Created by sj on 13/02/17.
 */

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class RulesMiner {
    private static int LIMIT = 0x40;
    private final double confidence;
    final Map<Integer,Set<Long>> L;
    private DataHolder dh;
    RulesMiner( Map<Integer,Set<Long>> L, double c ) throws Exception {
        this.L = L;
        dh = DataHolder.getInstance(null);
        confidence = c;
    }

    private class BruteForceMiner extends RecursiveTask<List<AssociationRule>> {
        final Long []arr;
        final int left, right;
        BruteForceMiner( Long []a, Integer l, Integer r ) {
            arr = a;
            left = l;
            right = r;
        }
        @Override
        protected List<AssociationRule> compute() {
            List<AssociationRule> res = new ArrayList<>();
            if ( right-left <= LIMIT ) {
                for ( int k = left; k <= right; ++k )
                    for ( long base = dh.getSignature(arr[k]), mask = base; mask > 0; mask = (mask-1)&base ) {
                        long lhs = dh.extractSubset(arr[k],mask), rhs = dh.extractComplement(arr[k],mask);
                        if ( lhs != 0 && rhs != 0 && dh.getConfidence(lhs,rhs) >= confidence )
                            res.add(new AssociationRule(lhs,rhs));
                    }
                return res ;
            }
            BruteForceMiner ll = new BruteForceMiner(arr,left,(left+right)/2), rr = new BruteForceMiner(arr,(left+right)/2,right);
            ll.fork();
            res.addAll(rr.compute());
            res.addAll(ll.join());
            return res;
        }
    }

    public List<AssociationRule> createRules() {
        List<AssociationRule> res = new ArrayList<>();
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for ( Map.Entry<Integer,Set<Long>> entry: L.entrySet() ) {
            Long []arr = new Long[entry.getValue().size()];
            entry.getValue().toArray(arr);
            BruteForceMiner bf = new BruteForceMiner(arr,0,arr.length-1);
            res.addAll(pool.invoke(bf));
        }
        return res;
    }
}


/**
 * Created by sj on 13/02/17.
 */

import java.io.*;
import java.util.*;

public class AssociationRule implements Comparable<AssociationRule> {
    final Long LHS, RHS;
    DataHolder dh;
    AssociationRule( Long lhs, Long rhs ) {
        LHS = lhs;
        RHS = rhs;
        try {
            dh = DataHolder.getInstance(null);
        } catch ( Exception e ) {
            System.out.println("[AssociationRuleConstructor]: "+e.getMessage());
            e.printStackTrace();
        }
    }
    public double getConfidenceLevel() {
        return dh.getConfidence(LHS,RHS);
    }
    public double getSupportLevel() {
        return dh.getSupport(LHS|RHS);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("(Support = %.2f, Confidence = %.2f)\n",getSupportLevel(),getConfidenceLevel()));
        sb.append(dh.toStr(LHS)).append("-->").append(dh.toStr(RHS));
        return sb.toString();
    }
    @Override
    public int compareTo( AssociationRule other ) {
        if ( LHS == other.LHS ) {
            if ( RHS != other.RHS )
                return RHS<other.RHS?-1:1;
            return 0;
        }
        return LHS<other.LHS?-1:1;
    }
}


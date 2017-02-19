/*
 * 
 */

/**
 * Created by sj on 11/02/17.
 */

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.System.*;

public class Main {

    private static BufferedReader br;
    private static Scanner inp;

    private static boolean probMeasure( double x ) {
        return 0.00 <= x && x <= 1.00;
    }

    public static void main( String ... args ) throws Exception {
        double support = 0.00, confidence = 0.00;
        File infile = null, outfile = null;
        String filename = null;
        Runtime rt = Runtime.getRuntime();
        boolean ok = false;

        //System.setIn(new FileInputStream("/home/sj/IdeaProjects/Assignment03/src/input.txt"));
        inp = new Scanner(System.in);

        do {
            out.printf("What is the name of the file containing your data?\n");
            filename = inp.next();
            infile = new File(Paths.get("").toAbsolutePath().toString() + "/" + filename);
            if ( !infile.exists() || infile.isDirectory() ) {
                out.printf(MyUtils.ANSI_RED+"[error] The supplied file is either non-existent, read-protected or is a directory."+MyUtils.ANSI_RESET+"\n");
                //rt.exec("cls");
                infile = null;
            }
        } while ( infile == null );
        out.printf(MyUtils.ANSI_GREEN+"[done]"+MyUtils.ANSI_RESET+" reading from %s\n\n",infile.toString());

        do {
            out.printf("Please select the minimum support rate(0.00-1.00): ");
            String t;
            Scanner scan = new Scanner(t = inp.next());
            if ( !scan.hasNextDouble() || !probMeasure(support = scan.nextDouble()) ) {
                out.printf(MyUtils.ANSI_RED+"[error] invalid support value: %s\n"+MyUtils.ANSI_RESET,t);
                //rt.exec("cls");
                ok = false;
            }
            else ok = true ;
        } while ( !ok );
        out.printf(MyUtils.ANSI_GREEN+"[done]"+MyUtils.ANSI_RESET+" support level set to %.2f\n\n",support);

        do {
            out.printf("Please select the minimum confidence rate(0.00-1.00): ");
            String t;
            Scanner scan = new Scanner(t = inp.next());
            if ( !scan.hasNextDouble() || !probMeasure(confidence = scan.nextDouble()) ) {
                out.printf(MyUtils.ANSI_RED+"[error] invalid confidence value: %s\n"+MyUtils.ANSI_RESET,t);
                //rt.exec("cls");
                ok = false;
            }
            else ok = true ;
        } while ( !ok );
        out.printf(MyUtils.ANSI_GREEN+"[done]"+MyUtils.ANSI_RESET+" confidence level set to %.2f\n\n",confidence);

        /**
         * outputRules() functionality
         */
        BufferedWriter bw = new BufferedWriter(new PrintWriter(outfile = new File("./Rules.txt")));

        System.setIn(new FileInputStream(infile));
        bw.write("Summary:\n");
        Map<Integer,Set<Long>> L = new AprioriTid(support,br=new BufferedReader(new InputStreamReader(System.in))).findAllLargeItemsets();
        bw.write(String.format("Total rows in the original set: %d\n",DataHolder.getInstance(null).getDBSize()));
        /**
         * createRules() functionality
         */
        List<AssociationRule> res = new RulesMiner(L,confidence).createRules();
        bw.write("Total rules discovered: "+res.size()+"\n");
        bw.write(String.format("The selected measures: Support = %.2f Confidence = %.2f\n",support,confidence));
        bw.write("---------------------------------------------------------\n");
        int rl = 0;
        for ( AssociationRule rule: res ) bw.write("Rule #"+(++rl)+" "+rule+"\n");
        bw.flush();
        bw.close();
        Path p = outfile.toPath();
        out.printf(MyUtils.ANSI_YELLOW_BACKGROUND+MyUtils.ANSI_BLUE+"The result is in the file %s\n"+MyUtils.ANSI_RESET,p.normalize().toAbsolutePath().toString());
        out.println(MyUtils.ANSI_GREEN+"*** Algorithm Finished ***"+ MyUtils.ANSI_RESET);
    }
}


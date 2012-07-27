package xmldumptools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A data container to collect intermediate results.
 *
 * @author kamir
 */
public class Results {

    
    // We want to define a threshold for tests, so not all
    // records are used, just a part of it lets say 1000.
    public static boolean doLimitForTests = false;
    public static int nrOfTestRecords = 1000;
    
    // We want to define a threshold for output splits, so not all
    // records are stored in RAM, this goes in line with the MapReduce approach
    public static boolean doSplitHashTables = false;
    public static int splitSize = 10000;
    
    /**
     * all temporary files will be concatenated to one
     * final result file.
     */
    private static void combineAllFiles() {

        // at the end all splits have to be combined again to one
        // large file with all results ...
        
        System.out.println(" >>> combine results: " );
        
        System.out.println(" --> done! " );
        
    }

    /**
     * to reduce the storage cosumption we have to flush after
     * a certain number of words which occured in our articles.
     */
    static int splitCounter;
    private static void triggerIntermediateFlush() {
        
        splitCounter++;
        String splitFN = "./split_" + splitCounter + ".dat";
        
        // the number of records in RAM has been checked and now
        // we can write a split to disc
        
        // and we can cleanup the RAM and create a new buffer
        
        System.out.println(" >>> create a new split(" + splitCounter + ") " + splitFN );
    }

    

    // a base for calculated filenames
    public static String fnBase = "results.dat";

    // a reused writer for result files
    static BufferedWriter bw = null;

    // set up the initial result writer
    public static void init() throws IOException {
        bw = new BufferedWriter( new FileWriter( fnBase ) );
    };

    // collect the number of articles in which a word occurs
    public static Hashtable<String,Integer> articleWordCount = new Hashtable<String,Integer>();
    
    // count all words, no matter to what article they belong
    public static Hashtable<String,Integer> wordCount = new Hashtable<String,Integer>();
  
    public static void storeResultLine( String articleName , Hashtable<String, Integer> data, String META ) throws IOException {
        if ( run == 2 ) {
            bw.write( articleName + "\t" + META + "\n" );
            bw.flush();
        }
        
        mapToGlobalResult( data );
        mapToWordPerarticleCount( articleName , data );
    
        if ( mustSplitNow() ) triggerIntermediateFlush();
    }



    private static void mapToGlobalResult(Hashtable<String, Integer> data) {
        int z = 0;
        for( String word : data.keySet() ) {
            int dz = data.get(word);
            if ( wordCount.containsKey(word) ) {
                z = wordCount.get(word);
                z=z+dz;
            }
            else {
                z=dz;
            }
            wordCount.put(word, z);
        }
    }

    public static void close() throws IOException {
        bw.flush();
        bw.close();

        bw = new BufferedWriter( new FileWriter( "all_word_counts_"+fnBase ) );
        for( String word : wordCount.keySet() ) {
            bw.write( word + "\t" + wordCount.get(word) + "\n" );
        }
        bw.flush();
        bw.close();

        bw = new BufferedWriter( new FileWriter( "word_in_articles_"+fnBase ) );
        for( String word : articleWordCount.keySet() ) {
            bw.write( word + "\t" + articleWordCount.get(word) + "\n" );
        }
        bw.flush();
        bw.close();

        combineAllFiles();
    };

    public static void nextRun() {
        run = 2;
        articleWordCount = new Hashtable<String,Integer>();
        wordCount = new Hashtable<String,Integer>();
    }

    public static int run = 1;

    private static void mapToWordPerarticleCount(String articleName, Hashtable<String, Integer> data) {
        switch ( run ) {
            // im ersten Durchlauf all Words ever ermitteln ...all
            case 1: {
                for( String word : data.keySet() ) {
                    if ( !articleWordCount.containsKey( word )) {
                        articleWordCount.put( word , 0 );
                    }
                }
                break;
            }
            // im zweiten RUN die aktuell gez�hlten WORTE jeweils um 1 hochz�hlen.
            case 2: {
                for( String word : data.keySet() ) {

                    // in this article the word occurs, but it is not important how often ...
                    int dz = 1;

                    int z = articleWordCount.get(word);
                    z=z+dz;

                    articleWordCount.put(word, z);
                }
                break;
            }
        }
    }

    /** checks it we have to create a new split **/ 
    private static boolean mustSplitNow() {
        return false;
    }


}

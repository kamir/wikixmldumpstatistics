package xmldumptools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class Results {



    // a base for filenames
    static String fnBase = "results_222.dat";

    // a reused writer for result files
    static BufferedWriter bw = null;

    // set up the initial result writer
    public static void init() throws IOException {
        bw = new BufferedWriter( new FileWriter( fnBase ) );
    };

    // collect the number of articles in which a word occurs
    public static Hashtable<String,Integer> articleWordCount = new Hashtable<String,Integer>();
    
    // count all words
    public static Hashtable<String,Integer> wordCount = new Hashtable<String,Integer>();

    public static void storeResultLine( String articleName , Hashtable<String, Integer> data, String META ) throws IOException {

        if ( run == 2 ) {
            bw.write( articleName + "\t" + META + "\n" );
            bw.flush();
        }

        mapToGlobalResult( data );

        mapToWordPerarticleCount( articleName , data );

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

}

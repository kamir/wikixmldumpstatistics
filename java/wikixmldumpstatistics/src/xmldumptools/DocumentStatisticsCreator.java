package xmldumptools;

import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.IWikiModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Counts some word statistics for Wikipedia articles.
 */
public class DocumentStatisticsCreator {

	private final IWikiModel fModel;

	private String fHeader;

	private String fFooter;

	private WikiArticle fArticle;

        static boolean debug = true;

	public DocumentStatisticsCreator(IWikiModel model, WikiArticle article) {
		fArticle = article;
		fModel = model;
		fHeader = null;
		fFooter = null;
	}

	/**
	 * Render the given Wikipedia texts into a string for a given converter
	 *
	 * @param converter
	 *          a text converter. <b>Note</b> the converter may be
	 *          <code>null</code>, if you only would like to analyze the raw wiki
	 *          text and don't need to convert. This speeds up the parsing
	 *          process.
	 * @return <code>null</code> if an IOException occurs or
	 *         <code>converter==null</code>
	 * @return
	 */
	public void render(ITextConverter converter, Appendable appendable) throws IOException {

		// print page information
		String rawWikiText = fArticle.getText();

                String filteredText = _filterText( rawWikiText );

                if ( debug ) System.out.println( "***********************************************************************************\n<Filtered :>\n==========\n" + filteredText +"\n***********************************************************************************\n");

		fModel.setPageName(fArticle.getTitle());
		// System.out.println(rawWikiText);

                Hashtable<String, Integer> wc = countWordsInArticle( filteredText );

                String META = "[id:"+fArticle.getId() + "]\t[namespace:" + fArticle.getNamespace() + "]\t" + fArticle.isCategory()  + "\t" + fArticle.isFile()  + "\t" + fArticle.isMain()  + "\t" + fArticle.isProject()  + "\t" + fArticle.isTemplate();

                Results.storeResultLine(fArticle.getTitle() , wc, META );

        }


    /**
     * 
     * Here we have to eliminate data, which is not usefull for text statistics.
     * 
     * - remove numbers
     * - remove symbol
     * 
     * - work with language dependent filters => modularisierung 
     * 
     * @param rawWikiText
     * @return 
     */    
    private String _filterText(String rawWikiText) {
        
        String filter = rawWikiText.replaceAll(". ", " ");
        
        filter = filter.replaceAll("\n", " ");
        filter = filter.replaceAll(", ", " ");
        filter = filter.replaceAll(":", " ");
        filter = filter.replaceAll("[\\[\\[]", " ");
        filter = filter.replaceAll("[\\]\\]]", " ");
        filter = filter.replaceAll("!", " ");
        filter = filter.replaceAll("=", " ");
        
        return filter;
    }

    
    /**
     * Here we split the filtered text and do count the words ...
     * 
     * later on we give the value n for n-Gram
     * 
     * @param filteredText
     * @return 
     */
    private Hashtable<String, Integer> countWordsInArticle(String filteredText) {


            StringTokenizer st = new StringTokenizer( filteredText );
            Hashtable<String,Integer> temp = new Hashtable<String, Integer>();
            int z = 0;
            while( st.hasMoreTokens() ) {
                String token = st.nextToken();
                if ( temp.containsKey(token) ) {
                    z = temp.get(token);
                    z=z+1;
                }
                else z=1;
                temp.put(token, z);
            }
            return temp;
        }

	/**
	 * Render the given Wikipedia texts into an HTML string and use the default
	 * HTMLConverter.
	 *
	 */
	public void render(Appendable appendable) throws IOException {
		render(new PlainTextConverter(), appendable);
	}

	/**
	 * Render the given Wikipedia texts into an HTML string and use the default
	 * PDFConverter. The resulting XHTML could be used as input for the Flying
	 * Saucer PDF renderer
	 *
	 */
	public void renderPDF(Appendable appendable) throws IOException {
		render(new PlainTextConverter(), appendable);
	}

	/**
	 * Render the given Wikipedia texts into an HTML file for the given converter.
	 *
	 */
	public void renderToFile(ITextConverter converter, String filename) throws IOException {
		File file = new File(filename);
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		try {
                        render(converter, fw);
		} finally {
			fw.close();
		}
	}

	/**
	 * Render the given Wikipedia texts into an HTML file.
	 *
	 */
	public void renderToFile(String filename) throws IOException {
		renderToFile(new PlainTextConverter(), filename);
	}

	/**
	 * Render the given Wikipedia texts into a PDF file.
	 *
	 * @param baseDirectoryName
	 *          the base directory, where all files should be stored
	 * @param filename
	 *          the filename relative to the baseDirectory
	 * @param cssStyle
	 *          CSS styles which should be used for rendering the PDF file
	 * @throws IOException
	 */
	public void renderPDFToFile(String baseDirectoryName, String filename, String cssStyle) throws IOException {
//		StringBuffer buffer = new StringBuffer();
//		renderPDF(buffer);
//		String renderedXHTML = buffer.toString();
//		// System.out.println(renderedXHTML);
//		File baseDirectory = new File(baseDirectoryName);
//		try {
//			URL url = baseDirectory.toURI().toURL();
//			PDFGenerator gen = new PDFGenerator(url);
//			gen.create(baseDirectoryName + '/' + filename, renderedXHTML, PDFGenerator.HEADER_TEMPLATE, PDFGenerator.FOOTER, "Big Test",
//					cssStyle);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Get the HTML header of this creator.
	 *
	 * @return <code>null</code> if no HTML header is set
	 */
	public String getHeader() {
		return fHeader;
	}

	/**
	 * Set the HTML header set of this creator.
	 *
	 */
	public void setHeader(String header) {
		this.fHeader = header;
	}

	/**
	 * Get the HTML footer of this creator.
	 *
	 * @return <code>null</code> if no HTML footer is set
	 */
	public String getFooter() {
		return fFooter;
	}

	/**
	 * Set the HTML footer of this creator.
	 *
	 */
	public void setFooter(String footer) {
		this.fFooter = footer;
	}

}

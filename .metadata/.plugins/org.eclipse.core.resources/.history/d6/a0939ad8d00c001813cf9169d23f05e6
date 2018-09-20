package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.tc.api.io.TCReaderSequence;
import org.dkpro.tc.api.io.TCReaderSingleLabel;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations.DocumentIdAnnotation;

/**
 * DummyReader.
 * 
 * @author Daniel Wehner
 *
 */
public class TweetReader extends JCasCollectionReader_ImplBase implements TCReaderSingleLabel {
	public final static String PARAM_TEXT_FOLDER = "TextFolder";

	// path to folder to read
	@ConfigurationParameter(name = PARAM_TEXT_FOLDER, description = "Path to folder which contains the texts to be read", mandatory = true)
	private String pathToFolder;
	
	private int fileOffset = 0;

	// list of documents
	List<String> docs = new ArrayList<>();
	List<String> golds = new ArrayList<>();

	// index of current document
	int current = 0;

	/**
	 * Read the files from the folder specified.
	 * 
	 * @param context
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		//super.initialize(context);
//			Thanks Clemens
		// iterate over all files in the folder
		for (File d : new File(pathToFolder).listFiles()) {
			// check if f is a file
			if (d.isDirectory()) {
				for(File d2 : d.listFiles()) {
					if(d2.isDirectory()) {
						for(File f : d2.listFiles()) {
							// create a buffered reader for each file
							try (BufferedReader inputReader = new BufferedReader(new FileReader(f))) {
								// read each file
								String line;
								while ((line = inputReader.readLine()) != null) {
									try {
										String text = parse(line, "full_text");
										docs.add(text);
										golds.add(d.getName() + "-" + d2.getName());
										
									}catch(Exception e) {
//										e.printStackTrace();
									}
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public String parse(String jsonLine, String property) throws Exception {
	    JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    String result = jobject.get(property).getAsString();
	    return result;
	}
	
	/**
	 * Indicate progress while reading.
	 * 
	 * @return
	 */
	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(current + 1, docs.size(), Progress.ENTITIES) };
	}

	/**
	 * Indicate if there are more documents to read.
	 * 
	 * @return
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return current < docs.size();
	}

	@Override
	public String getTextClassificationOutcome(JCas jcas) throws CollectionException {
//		try {
//            String uriString = DocumentMetaData.get(jcas).getDocumentUri();
//            return new File(new URI(uriString).getPath()).getParentFile().getName() + "-" + new File(new URI(uriString).getPath()).getParentFile().getParentFile().getName();
//        }
//        catch (URISyntaxException e) {
//            throw new CollectionException(e);
//        }
		return null;
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
//		JCas jCas = null;
		
//		try {
//			jCas = aCas.getJCas();
//		} catch (CASException e) {
//			e.printStackTrace();
//		}
		
		jCas.setDocumentText(docs.get(current));
		
//		Sentence sentenceAnno = new Sentence(jCas);
//        sentenceAnno.setBegin(0);
//        sentenceAnno.setEnd(jCas.getDocumentText().length());
//        sentenceAnno.addToIndexes();
        
//        TextClassificationTarget unit = new TextClassificationTarget(jCas, sentenceAnno.getBegin(), sentenceAnno.getEnd());
//		unit.addToIndexes();
        TextClassificationTarget fullDoc = new TextClassificationTarget(jCas, 0, jCas.getDocumentText().length());
        fullDoc.addToIndexes();

		
		DocumentMetaData dmd = DocumentMetaData.create(jCas); 
		//DocumentMetaData.get(jCas);
		dmd.setDocumentId(String.valueOf(current));
		dmd.setDocumentTitle("RuegenwalderSchinkenspicker - " + fileOffset);
		dmd.setDocumentUri("RuegenwalderSchinkenspicker - " + fileOffset);
		
		TextClassificationOutcome outcome = new TextClassificationOutcome(jCas, 0, jCas.getDocumentText().length());
		outcome.setOutcome(golds.get(current));
//		outcome.setBegin(sentenceAnno.getBegin());
//		outcome.setEnd(sentenceAnno.getEnd());
		outcome.addToIndexes();
		
		fileOffset++;
		current++;		
	}

}

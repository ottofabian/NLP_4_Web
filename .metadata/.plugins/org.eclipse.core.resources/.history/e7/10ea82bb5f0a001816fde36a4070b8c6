package pipeline;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
//import org.dkpro.tc.core.DeepLearningConstants;
import org.dkpro.tc.features.length.NrOfChars;
import org.dkpro.tc.features.length.NrOfTokens;
import org.dkpro.tc.features.length.NrOfTokensPerSentence;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.LucenePOSNGram;
import org.dkpro.tc.features.spelling.SpellingErrorRatioExtractor;
import org.dkpro.tc.features.style.ContextualityMeasureFeatureExtractor;
import org.dkpro.tc.features.style.ExclamationFeatureExtractor;
import org.dkpro.tc.features.style.TypeTokenRatioFeatureExtractor;
import org.dkpro.tc.features.syntax.PastVsFutureFeatureExtractor;
import org.dkpro.tc.features.syntax.SuperlativeRatioFeatureExtractor;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
//import org.dkpro.tc.ml.DeepLearningExperimentTrainTest;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentTrainTest;
//import org.dkpro.tc.ml.deeplearning4j.Deeplearning4jAdapter;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.BatchTrainTestReport;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;

import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
//import deeplearning.Dl4jDocumentUserCode;
import featureExtractor.CharacterFeatureExtractor;
import featureExtractor.VocabularyRichnessFeatureExtractor;
import reader.TweetReader;
import weka.classifiers.bayes.NaiveBayes;

// TODO: classifiers
enum Classifier {
	WekaNaiveBayes, Deeplearning4j
}

public class Pipeline implements Constants {

	private static final String PATH_TO_TWEETS_TRAIN = new File(Pipeline.class.getResource("/tweets_train/").getFile())
			.getAbsolutePath().replace("target\\classes", "src\\main\\resources").replaceAll("%20", " ");

	private static final String PATH_TO_TWEETS_TEST = new File(Pipeline.class.getResource("/tweets_test/").getFile())
			.getAbsolutePath().replace("target\\classes", "src\\main\\resources").replaceAll("%20", " ");

	public static void main(String[] args) {
		System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");

		// ensure DKPRO_HOME environment variable is set
		DemoUtils.setDkproHome(Pipeline.class.getSimpleName());

		Pipeline pipeline = new Pipeline();

		try {
			System.out.println("Launching TrainTestRun");
			pipeline.runTrainTest(getParameterSpace(Classifier.Deeplearning4j), Classifier.Deeplearning4j);
			System.out.println("DONE with TrainTestRun");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	// Cross validation
	protected void runCrossValidation(ParameterSpace pSpace, int num_folds, Classifier clf) throws Exception {
		if (clf == Classifier.WekaNaiveBayes) {
			// TODO
			ExperimentCrossValidation batch = new ExperimentCrossValidation("TwitterSherlockWekaNB", WekaClassificationAdapter.class, num_folds);
			batch.setPreprocessing(getPreprocessing(clf));
			batch.setParameterSpace(pSpace);
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
			batch.addReport(BatchCrossValidationReport.class);

			Lab.getInstance().run(batch);
			
		}else if (clf == Classifier.Deeplearning4j) {
			//TODO
		}

	}

	// Train/Test evaluation
	protected void runTrainTest(ParameterSpace pSpace, Classifier clf) throws Exception {
		if (clf == Classifier.WekaNaiveBayes) {
			// TODO
			ExperimentTrainTest batch = new ExperimentTrainTest("TwitterSherlockWekaNB", WekaClassificationAdapter.class);
			batch.setPreprocessing(getPreprocessing(clf));
			batch.setParameterSpace(pSpace);
			batch.addReport(BatchTrainTestReport.class);
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);

			Lab.getInstance().run(batch);			
		}else if (clf == Classifier.Deeplearning4j) {
//			DeepLearningExperimentTrainTest batch = new DeepLearningExperimentTrainTest("TwitterSherlockDeeplearning4j", Deeplearning4jAdapter.class);
//	        batch.setPreprocessing(getPreprocessing(clf));
//	        batch.setParameterSpace(pSpace);
//	        batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
//
//	        // Run
//	        Lab.getInstance().run(batch);
		}

	}
	
	public static ParameterSpace getParameterSpace(Classifier clf) throws ResourceInitializationException {
		
		CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
				TweetReader.class, TweetReader.PARAM_TEXT_FOLDER, PATH_TO_TWEETS_TRAIN);
		
		CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
				TweetReader.class, TweetReader.PARAM_TEXT_FOLDER, PATH_TO_TWEETS_TEST);

		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);
		
		ParameterSpace pSpace = null;
		
		if(clf == Classifier.WekaNaiveBayes) {
			Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET,
				new TcFeatureSet(TcFeatureFactory.create(NrOfTokensPerSentence.class),
						TcFeatureFactory.create(TypeTokenRatioFeatureExtractor.class),
						TcFeatureFactory.create(ContextualityMeasureFeatureExtractor.class),
						// TcFeatureFactory.create(ModalVerbsFeatureExtractor.class),
						TcFeatureFactory.create(ExclamationFeatureExtractor.class),
						TcFeatureFactory.create(SuperlativeRatioFeatureExtractor.class),
						// TcFeatureFactory.create(PastVsFutureFeatureExtractor.class), //Penn Treebank
						// Tagset only for this one!!!
						TcFeatureFactory.create(EmoticonRatio.class), TcFeatureFactory.create(NumberOfHashTags.class),
						// TcFeatureFactory.create(AvgNrOfCharsPerSentence.class),
						// TcFeatureFactory.create(AvgNrOfCharsPerToken.class),
						TcFeatureFactory.create(NrOfChars.class),
						// TcFeatureFactory.create(NrOfSentences.class),
						TcFeatureFactory.create(NrOfTokens.class),
						TcFeatureFactory.create(CharacterFeatureExtractor.class),
						// TcFeatureFactory.create(VocabularyRichnessFeatureExtractor.class),
						TcFeatureFactory.create(LuceneNGram.class, LuceneNGram.PARAM_NGRAM_USE_TOP_K, 1000,
								LuceneNGram.PARAM_NGRAM_MIN_N, 1, LuceneNGram.PARAM_NGRAM_MAX_N, 3,
								LuceneNGram.PARAM_TF_IDF_CALCULATION, true),
						TcFeatureFactory.create(LucenePOSNGram.class, LuceneNGram.PARAM_NGRAM_USE_TOP_K, 1000,
								LuceneNGram.PARAM_NGRAM_MIN_N, 1, LuceneNGram.PARAM_NGRAM_MAX_N, 3,
								LuceneNGram.PARAM_TF_IDF_CALCULATION, true),
						TcFeatureFactory.create(SpellingErrorRatioExtractor.class)

				));
			
			@SuppressWarnings("unchecked")
			Dimension<List<String>> dimClassificationArgs = Dimension.create(DIM_CLASSIFICATION_ARGS,
	                Arrays.asList(new String[] { NaiveBayes.class.getName() }));
			
			pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
					Dimension.create(DIM_LEARNING_MODE, Constants.LM_SINGLE_LABEL),
					Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT), dimFeatureSets, dimClassificationArgs);
		
		} else if(clf == Classifier.Deeplearning4j) {
			
//			pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
//	                Dimension.create(DIM_FEATURE_MODE, Constants.FM_DOCUMENT),
//	                Dimension.create(DIM_LEARNING_MODE, Constants.LM_SINGLE_LABEL),
//	                Dimension.create(DeepLearningConstants.DIM_USER_CODE,
//	                        new Dl4jDocumentUserCode()),
//	                Dimension.create(DeepLearningConstants.DIM_MAXIMUM_LENGTH, 280),
//	                Dimension.create(DeepLearningConstants.DIM_VECTORIZE_TO_INTEGER, true),
//	                Dimension.create(DeepLearningConstants.DIM_PRETRAINED_EMBEDDINGS,
//	                        "src/main/resources/wordvector/glove.6B.50d_250.txt")); //glove.twitter.27B.100d.txt
//			
		}	
		return pSpace;
	}

	protected AnalysisEngineDescription getPreprocessing(Classifier clf) throws ResourceInitializationException {
		
		if(clf == Classifier.WekaNaiveBayes) {
			return createEngineDescription(
					createEngineDescription(ArktweetTokenizer.class),
	                createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_LANGUAGE,
	                        "en", ArktweetPosTagger.PARAM_VARIANT, "default"),
	                createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_LANGUAGE, "en"));
		
		} else if(clf == Classifier.Deeplearning4j) {
			return createEngineDescription(BreakIteratorSegmenter.class);
		} else {
//			return createEngineDescription(NoOpAnnotator.class);
			return null;
		}
	}

}

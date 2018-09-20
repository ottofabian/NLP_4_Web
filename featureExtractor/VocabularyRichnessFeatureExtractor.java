package featureExtractor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class VocabularyRichnessFeatureExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	private final double C = Math.pow(10, 4);

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {

		FrequencyDistribution<String> fd = new FrequencyDistribution<String>();
        Set<Feature> featSet = new HashSet<Feature>();

		for (Token token : JCasUtil.selectCovered(jcas, Token.class, target)) {
			fd.inc(token.getCoveredText().toLowerCase());
		}

		Map<Long, Integer> map = new HashMap<Long, Integer>();

		for (String key : fd.getKeys()) {
			long count = fd.getCount(key);
			if (map.containsKey(count))
				map.put(count, map.get(count) + 1);
			else
				map.put(count, 1);

		}

		long N = fd.getN(); // length of text
		long V = fd.getB(); // length of vocab
		int hapaxLegomena = map.get(1L);
		int hapaxDislegomena = 0;
		if (map.containsKey(2L))
			hapaxDislegomena = map.get(2L);

		// Yule's K
//		final long[] s2 = { 0 };
//		map.forEach((k, v) -> s2[0] += Math.pow(k, 2) * v);
//		long yulesK = (long) (C * (s2[0] - N) / Math.pow(N, 2));

		// Simpson's D
		final long[] simpsonD = { 0 };
//		if(N > 1) {
//			map.forEach((k, v) -> simpsonD[0] += v * (k / N) * ((k - 1) / (N - 1)));
//		}


		// Honore's R
		double honoreR = 100 * Math.log(N) / (1 - hapaxLegomena / V);

		// Brunet's W
		double brunetW = Math.pow(N, V - 0.17);

		// Sichel's S
		double sichelS = hapaxDislegomena / V;

		// Uber Index
		double uberIndex = Math.pow(Math.log(N), 2) / (Math.log(N) - Math.log(V));

//		featSet.add(new Feature("JulesK", yulesK));
//		featSet.add(new Feature("SimpsonsD", simpsonD));
		featSet.add(new Feature("HonoresR", honoreR));
		featSet.add(new Feature("BrunetsW", brunetW));
		featSet.add(new Feature("SichelsS", sichelS));
		featSet.add(new Feature("UberIndex", uberIndex));
		featSet.add(new Feature("HapaxLegonmena", hapaxLegomena));
		featSet.add(new Feature("HapaxDislegomena", hapaxDislegomena));
		
		return featSet;
	}

}

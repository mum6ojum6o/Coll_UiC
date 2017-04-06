package AI_II;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



/**
 * @author Ravi Mohan
 * @author Mike Stampone
 */
public class DecisionTreeLearner implements Learner {
	private DecisionTree tree;

	private String defaultValue;

	public DecisionTreeLearner() {
		this.defaultValue = "Unable To Classify";

	}

	// used when you have to test a non induced tree (eg: for testing)
	public DecisionTreeLearner(DecisionTree tree, String defaultValue) {
		this.tree = tree;
		this.defaultValue = defaultValue;
	}

	//
	// START-Learner

	/**
	 * Induces the decision tree from the specified set of examples
	 * 
	 * @param ds
	 *            a set of examples for constructing the decision tree
	 */
	@Override
	public void train(DataSet ds) {
		List<String> attributes = ds.getNonTargetAttributes();
		this.tree = decisionTreeLearning(ds, attributes,
				new ConstantDecisonTree(defaultValue));
	}
	public void trainWithGR(DataSet ds) {
		List<String> attributes = ds.getNonTargetAttributes();
		this.tree = decisionTreeLearningWithGainRatio(ds, attributes,
				new ConstantDecisonTree(defaultValue));
	}

	@Override
	public String predict(Example e) {
		return (String) tree.predict(e);
	}

	@Override
	public int[] test(DataSet ds) {
		int[] results = new int[] { 0, 0 };

		for (Example e : ds.examples) {
			if (e.targetValue().equals(tree.predict(e))) {
				results[0] = results[0] + 1;
			} else {
				results[1] = results[1] + 1;
			}
		}
		return results;
	}

	// END-Learner
	//

	/**
	 * Returns the decision tree of this decision tree learner
	 * 
	 * @return the decision tree of this decision tree learner
	 */
	public DecisionTree getDecisionTree() {
		return tree;
	}

	//
	// PRIVATE METHODS
	//

	private DecisionTree decisionTreeLearning(DataSet ds,
			List<String> attributeNames, ConstantDecisonTree defaultTree) {
		//System.out.println("in decisionTree Learning:"+Arrays.asList(attributeNames));
		if (ds.size() == 0) {
			return defaultTree;
		}
		if (allExamplesHaveSameClassification(ds)) {
			return new ConstantDecisonTree(ds.getExample(0).targetValue());
		}
		if (attributeNames.size() == 0) {
			return majorityValue(ds);
		}
		String chosenAttribute = chooseAttribute(ds, attributeNames);
		//System.out.println("the chosen attribute is:"+chosenAttribute);

		DecisionTree tree = new DecisionTree(chosenAttribute);
		ConstantDecisonTree m = majorityValue(ds);

		List<String> values = ds.getPossibleAttributeValues(chosenAttribute);
		for (String v : values) {
			//System.out.println("learning about:"+v);
			DataSet filtered = ds.matchingDataSet(chosenAttribute, v);
			List<String> newAttribs = Util.removeFrom(attributeNames,
					chosenAttribute);
			DecisionTree subTree = decisionTreeLearning(filtered, newAttribs, m);
			
			tree.addNode(v, subTree);

		}

		return tree;
	}
	private DecisionTree decisionTreeLearningWithGainRatio(DataSet ds,
			List<String> attributeNames, ConstantDecisonTree defaultTree) {
		//System.out.println("in decisionTree Learning:"+Arrays.asList(attributeNames));
		if (ds.size() == 0) {
			return defaultTree;
		}
		if (allExamplesHaveSameClassification(ds)) {
			return new ConstantDecisonTree(ds.getExample(0).targetValue());
		}
		if (attributeNames.size() == 0) {
			return majorityValue(ds);
		}
		String chosenAttribute = chooseAttributeWithGainRatio(ds, attributeNames);
		//System.out.println("the chosen attribute is:"+chosenAttribute);

		DecisionTree tree = new DecisionTree(chosenAttribute);
		ConstantDecisonTree m = majorityValue(ds);

		List<String> values = ds.getPossibleAttributeValues(chosenAttribute);
		for (String v : values) {
			//System.out.println("learning about:"+v);
			DataSet filtered = ds.matchingDataSet(chosenAttribute, v);
			List<String> newAttribs = Util.removeFrom(attributeNames,
					chosenAttribute);
			DecisionTree subTree = decisionTreeLearning(filtered, newAttribs, m);
			
			tree.addNode(v, subTree);

		}

		return tree;
	}

	
/* What does this function do??*/
	private ConstantDecisonTree majorityValue(DataSet ds) {
		Learner learner = new MajorityLearner();
		learner.train(ds);
		return new ConstantDecisonTree(learner.predict(ds.getExample(0)));
	}

	private String chooseAttribute(DataSet ds, List<String> attributeNames) {
		double greatestGain = 0.0;
		String attributeWithGreatestGain = attributeNames.get(0);
		for (String attr : attributeNames) {
			double gain = ds.calculateGainFor(attr);
			System.out.println(attr+"'s gain:"+gain);
			if (gain > greatestGain) {
				greatestGain = gain;
				attributeWithGreatestGain = attr;
			}
		}

		return attributeWithGreatestGain;
	}

	private String chooseAttributeWithGainRatio(DataSet ds, List<String> attributeNames) {
		double greatestGain = 0.0;
		double greatestGainRatio = 0.0;
		String attributeWithGreatestGain = attributeNames.get(0);
		for (String attr : attributeNames) {
			double gain = ds.getInformationFor(attr);
			double split = ds.getSplitInfo(attr);
			double gr=gain/split;
			System.out.println(attr+"'s gain ratio:"+gr);
			if (gr > greatestGainRatio) {
				greatestGainRatio = gr;
				attributeWithGreatestGain = attr;
			}
		}
//System.out.println("greatest gain ration:"+attributeWithGreatestGain);
		return attributeWithGreatestGain;
	}
	private boolean allExamplesHaveSameClassification(DataSet ds) {
		String classification = ds.getExample(0).targetValue();
		Iterator<Example> iter = ds.iterator();
		while (iter.hasNext()) {
			Example element = iter.next();
			if (!(element.targetValue().equals(classification))) {
				return false;
			}

		}
		return true;
	}
}
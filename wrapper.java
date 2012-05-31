/*
 * This is a wrapper program to build and evaluate a Weka Classifer.
 * It relies on the python programs frequency_counter_two.py and bla.py.
 * Execution is "java wrapper <number>" where <number> is the upper bound 
 * on the number of features that you wish to try.
 * frequency_counter_two.py produces the csv input files to weka.
 * bla.py produces the graph with matplotlib.
 * The classifier currently being built (hardcoded), is NaiveBayesMultinomial 
 * with ten-fold cross validation.
 * Results are outputed to a graph, and are also outputted to stdout.
 *
 */


import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.lang.Integer;
import java.lang.String;
import java.util.Random;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;


public class wrapper
{
    public static void main(String[] args)
    {
	// determining upper bound for number of features desired
	int firstArg = Integer.parseInt(args[0]);
        int i;

	// exception handling
        try{
              // Create file 
              FileWriter fstream = new FileWriter("graph_ten_topics.txt");
	      BufferedWriter out = new BufferedWriter(fstream);
	      out.write("features,accuracy\n");
  
	      // looping through different numbers of unigrams
	      for (i=1500; i < firstArg; i = i + 200 )
	      {

		  try
		  {
		      // running python program to produce csv input files
		      Runtime r = Runtime.getRuntime();
		      Process p = r.exec("python frequency_counter_two.py nono " + Integer.toString(i) + " 0");
		      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		      p.waitFor();
		      String line = "";
		      while (br.ready())
			  System.out.println(br.readLine());

		  }
		  catch (Exception e)
		  {
			  String cause = e.getMessage();
			  if (cause.equals("python: not found"))
			      System.out.println("No python interpreter found.");
		  }

		  // file names are always in the same format
		  String file_name = "features_ten_topics_" + Integer.toString(i) + "_unigrams_0_bigrams.csv"; 
		  
		  // print which iteration to stdout
		  System.out.println(i);

		  // run weka 
		  run_weka(file_name, i, out);

	      }

	      //Close the output stream
	      out.close();
	}
	catch (Exception e)
	{
	    //Catch exception if any
	    System.err.println("Error: " + e.getMessage());
	}
	
	try
	{
	    //produce graph using matplotlib
	    Runtime r = Runtime.getRuntime();
	    Process p = r.exec("python bla.py");
	    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    p.waitFor();
	    String line = "";
	    while (br.ready())
		System.out.println(br.readLine());
	}
	catch (Exception e)
	{
	    String cause = e.getMessage();
	    if (cause.equals("python: not found"))
		System.out.println("No python interpreter found.");
	}
}



public static int run_weka(String file_name, int iteration, BufferedWriter input)
{

    // passing in the results (graph input) file
    BufferedWriter out = input;

    int i = iteration;

    // passing in correct csv input file
    String file = file_name;

    // instantiating for weka
    Instances data;

    try
    {
	// get data for weka
        DataSource source = new DataSource(file);
        data = source.getDataSet();
    }
    catch (Exception e)
    {
        System.out.println("An error occurred: " + e);
        return -1;
    }

    // setting class attribute if the data format does not provide this information
    // For example, the XRFF format saves the class attribute information as well
    if (data.classIndex() == -1)
         data.setClassIndex(data.numAttributes() - 1);
   
    // still need to work out train set/test set split

    /*
      double percent = 66.0; 
      Instances inst = data; // your full training set 
      int trainSize = (int) Math.round(inst.numInstances() * percent / 100); 
      int testSize = inst.numInstances() - trainSize; 
      Instances train = new Instances(inst, 0, trainSize); 
      Instances test = new Instances(inst, trainSize, testSize);
    */

    try
    {
    /*    
	// train classifier
	Classifier cls = new NaiveBayesMultinomial();
	cls.buildClassifier(train);
	// evaluate classifier and print some statistics
	Evaluation eval = new Evaluation(train);
	eval.evaluateModel(cls, test);
	System.out.println(eval.toSummaryString("\nResults\n======\n", false)); 	
    */
	// built classifier
	Classifier cls = new NaiveBayesMultinomial();

	// evaluate on data
	Evaluation eval = new Evaluation(data);
	eval.crossValidateModel(cls, data, 10, new Random(1));

	// print percentage correct to stdout and to the graph input file
	System.out.println(eval.pctCorrect());
	out.write(Integer.toString(i) + "," + eval.pctCorrect() + "\n");

	// System.out.println(eval.toSummaryString("\nResults\n======\n", false));

    }
    catch (Exception e)
    {
        System.out.println("An error occurred: " + e);
        return -1;
    }



    return 0;
}

}

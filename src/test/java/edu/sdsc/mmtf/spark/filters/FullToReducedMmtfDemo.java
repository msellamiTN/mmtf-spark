/**
 * 
 */
package edu.sdsc.mmtf.spark.filters;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.api.StructureDataInterface;

import edu.sdsc.mmtf.spark.incubator.IsDProteinChain;
import edu.sdsc.mmtf.spark.incubator.IsDSaccharide;
import edu.sdsc.mmtf.spark.incubator.IsLDnaChain;
import edu.sdsc.mmtf.spark.incubator.IsLProteinChain;
import edu.sdsc.mmtf.spark.incubator.IsLProteinChainNonStrict;
import edu.sdsc.mmtf.spark.incubator.IsLRnaChain;
import edu.sdsc.mmtf.spark.incubator.IsMonomerChain;
import edu.sdsc.mmtf.spark.incubator.IsPolymerChain;
import edu.sdsc.mmtf.spark.io.MmtfSequenceFileReader;
import edu.sdsc.mmtf.spark.mappers.ReducedEncoderNew;
import edu.sdsc.mmtf.spark.mappers.StructureToPolymerChains;
import scala.Tuple2;

/**
 * @author peter
 *
 */
public class FullToReducedMmtfDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	    if (args.length != 1) {
	        System.err.println("Usage: " + FullToReducedMmtfDemo.class.getSimpleName() + " <hadoop sequence file>");
	        System.exit(1);
	    }
	    
	    long start = System.nanoTime();
	    
	    SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(FullToReducedMmtfDemo.class.getSimpleName());
	    JavaSparkContext sc = new JavaSparkContext(conf);
		 
	    JavaPairRDD<String, StructureDataInterface> full = MmtfSequenceFileReader.read(args[0],  sc);
    
//	    full = full.filter(new ExperimentalMethodsFilter("SOLUTION NMR"));

	    JavaPairRDD<String, StructureDataInterface> fullChains = full.flatMapToPair(new StructureToPolymerChains()).cache();

	    long lProtein = fullChains.filter(new IsLProteinChain()).count();
//	    long lProteinNS = fullChains.filter(new IsLProteinChainNonStrict()).count();
	    long dProtein = fullChains.filter(new IsDProteinChain()).count();
	    long lDna = fullChains.filter(new IsLDnaChain()).count();
	    long lRna =  fullChains.filter(new IsLRnaChain()).count();
//	    long dSaccharide = fullChains.filter(new IsDSaccharide()).count();
//	    long monomer = fullChains.filter(new IsMonomerChain()).count();
//	    long polymer = fullChains.filter(new IsPolymerChain()).count();
	    long sum = lProtein + dProtein + lDna + lRna;
	    
	    System.out.println("L-Peptide: " + lProtein);
//	    System.out.println("L-PeptideNS: " + lProteinNS);
	    System.out.println("D-Peptide: " + dProtein);
	    System.out.println("L-RNA: " + lDna);
	    System.out.println("L-DNA: " + lRna);
//	    System.out.println("dSaccaride: " + dSaccharide);
//	    System.out.println("Non-Polymer: " + monomer);
//	    System.out.println("Polymer: " + polymer);
	    System.out.println("Sum: " + sum);

//	    JavaPairRDD<String, StructureDataInterface> reduced = full.mapToPair(t -> new Tuple2<String,StructureDataInterface>(t._1, ReducedEncoderNew.getReduced(t._2)));

//	    JavaPairRDD<String, StructureDataInterface>reducedChains = reduced.flatMapToPair(new StructureToPolymerChains());
//	    long lProteinR = reducedChains.filter(new IsLProteinChain()).count();
//	    long lProteinNSR = reducedChains.filter(new IsLProteinChainNonStrict()).count();
//	    long dProteinR = reducedChains.filter(new IsDProteinChain()).count();
//	    long lDnaR = reducedChains.filter(new IsLDnaChain()).count();
//	    long lRnaR =  reducedChains.filter(new IsLRnaChain()).count();
//	    long dSaccharideR = reducedChains.filter(new IsDSaccharide()).count();
//	    long monomerR = reducedChains.filter(new IsMonomerChain()).count();
//	    long polymerR = reducedChains.filter(new IsPolymerChain()).count();
	    long sumR = lProtein + dProtein + lDna + lRna;
	    
//	    System.out.println("L-Peptide: " + lProteinR);
//	    System.out.println("L-PeptideNS: " + lProteinNSR);
//	    System.out.println("D-Peptide: " + dProteinR);
//	    System.out.println("L-RNA: " + lDnaR);
//	    System.out.println("L-DNA: " + lRnaR);
//	    System.out.println("dSaccaride: " + dSaccharideR);
//	    System.out.println("Non-Polymer: " + monomerR);
//	    System.out.println("Polymer: " + polymerR);
//	    System.out.println("Sum: " + sumR);

	    
	    long end = System.nanoTime();
	    
	    System.out.println("Time: " + (end-start)/1E9 + "sec.");
	    
	    sc.close();
	}

}
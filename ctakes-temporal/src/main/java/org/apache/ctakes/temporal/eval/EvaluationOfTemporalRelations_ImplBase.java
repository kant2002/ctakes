package org.apache.ctakes.temporal.eval;

import java.io.File;
import java.util.Collection;

import org.apache.ctakes.temporal.eval.EvaluationOfEventTimeRelations.ParameterSettings;
import org.apache.ctakes.typesystem.type.relation.BinaryTextRelation;
import org.apache.ctakes.typesystem.type.relation.RelationArgument;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.tksvmlight.model.CompositeKernel.ComboOperator;
import org.cleartk.eval.AnnotationStatistics;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;
import com.lexicalscope.jewel.cli.Option;

public abstract class EvaluationOfTemporalRelations_ImplBase extends
		Evaluation_ImplBase<AnnotationStatistics<String>> {

	  static interface TempRelOptions extends Evaluation_ImplBase.Options{
		  @Option
		  public boolean getTest();
		  
		  @Option
		  public boolean getPrintFormattedRelations();
		  
		  @Option
		  public boolean getBaseline();
	    
	    @Option
	    public boolean getClosure();
	  }
	  
	  protected static boolean DEFAULT_BOTH_DIRECTIONS = false;
	  protected static float DEFAULT_DOWNSAMPLE = 1.0f;
	  protected static double DEFAULT_SVM_C = 1.0;
	  protected static double DEFAULT_SVM_G = 1.0;
	  protected static double DEFAULT_TK = 0.5;
	  protected static double DEFAULT_LAMBDA = 0.5;
	  
	  protected static ParameterSettings defaultParams = new ParameterSettings(DEFAULT_BOTH_DIRECTIONS, DEFAULT_DOWNSAMPLE, "linear",
	  		  DEFAULT_SVM_C, DEFAULT_SVM_G, "polynomial", ComboOperator.SUM, DEFAULT_TK, DEFAULT_LAMBDA);

	  
	  protected ParameterSettings params = null;
	  protected boolean printRelations = false;

	public EvaluationOfTemporalRelations_ImplBase(File baseDirectory,
			File rawTextDirectory, File xmlDirectory, XMLFormat xmlFormat,
			File xmiDirectory, File treebankDirectory, boolean printErrors, boolean printRelations, ParameterSettings params) {
		super(baseDirectory, rawTextDirectory, xmlDirectory, xmlFormat, xmiDirectory,
				treebankDirectory);
		this.params = params;
		this.printRelations = printRelations;
		this.printErrors =  printErrors;
	}

  public static class PreserveEventEventRelations extends JCasAnnotator_ImplBase {
    public static final String PARAM_GOLD_VIEW = "GoldView";

    @ConfigurationParameter(name = PARAM_GOLD_VIEW)
    private String goldViewName = CAS.NAME_DEFAULT_SOFA;

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
      for(BinaryTextRelation relation : Lists.newArrayList(JCasUtil.select(jCas, BinaryTextRelation.class))){
          RelationArgument arg1 = relation.getArg1();
          RelationArgument arg2 = relation.getArg2();
          if(arg1.getArgument() instanceof EventMention && arg2.getArgument() instanceof EventMention){
            // these are the kind we keep.
            continue;
          }
          arg1.removeFromIndexes();
          arg2.removeFromIndexes();
          relation.removeFromIndexes();
      }
    }   
  }

  public static class RemoveNonContainsRelations extends JCasAnnotator_ImplBase {
    public static final String PARAM_RELATION_VIEW = "RelationView";

    @ConfigurationParameter(name = PARAM_RELATION_VIEW)
    private String relationViewName = CAS.NAME_DEFAULT_SOFA;
  @Override
  public void process(JCas jCas) throws AnalysisEngineProcessException {
    JCas relationView = null;
    
    try {
      relationView = jCas.getView(relationViewName);
    } catch (CASException e) {
      e.printStackTrace();
    }
    for (BinaryTextRelation relation : Lists.newArrayList(JCasUtil.select(
        relationView,
        BinaryTextRelation.class))) {
    	String relationType = relation.getCategory();
      if (relationType.startsWith("BEFORE")|| relationType.startsWith("BEGINS-ON")|| relationType.startsWith("ENDS-ON")) {
//        relation.getArg1().removeFromIndexes();
//        relation.getArg2().removeFromIndexes();
//        relation.removeFromIndexes();
    	  relation.setCategory("RARE");
      }else if(relationType.startsWith("CONTAINS")|| relationType.startsWith("OVERLAP")){
    	  relation.setCategory("COMMON");
      }
    }
  }
  
  public static class RemoveGoldAttributes extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
      for(EventMention event : JCasUtil.select(jCas, EventMention.class)){
        if(event.getEvent() != null && event.getEvent().getProperties() != null){
          event.getEvent().getProperties().setContextualAspect("UNK");
          event.getEvent().getProperties().setContextualModality("UNK");
        }
      }
      for(TimeMention timex : JCasUtil.select(jCas, TimeMention.class)){
        timex.setTimeClass("UNK");
      }
    }
  }
}

	  protected static void printRelationAnnotations(String fileName, Collection<BinaryTextRelation> relations) {

		  for(BinaryTextRelation binaryTextRelation : relations) {

			  Annotation arg1 = binaryTextRelation.getArg1().getArgument();
			  Annotation arg2 = binaryTextRelation.getArg2().getArgument();

			  String arg1Type = arg1.getClass().getSimpleName();
			  String arg2Type = arg2.getClass().getSimpleName();

			  int arg1Begin = arg1.getBegin();
			  int arg1End = arg1.getEnd();
			  int arg2Begin = arg2.getBegin();
			  int arg2End = arg2.getEnd();

			  String category = binaryTextRelation.getCategory();

			  System.out.format("%s\t%s\t%s\t%d\t%d\t%s\t%d\t%d\n", 
					  fileName, category, arg1Type, arg1Begin, arg1End, arg2Type, arg2Begin, arg2End);
		  }
	  }
	  
	  protected static String formatRelation(BinaryTextRelation relation) {
		  IdentifiedAnnotation arg1 = (IdentifiedAnnotation)relation.getArg1().getArgument();
		  IdentifiedAnnotation arg2 = (IdentifiedAnnotation)relation.getArg2().getArgument();
		  String arg1Type ="E";
		  String arg2Type ="T";
		  if(arg1 instanceof TimeMention) arg1Type = "T";
		  if(arg2 instanceof EventMention) arg2Type = "E";
		  String text = arg1.getCAS().getDocumentText();
		  int begin = Math.min(arg1.getBegin(), arg2.getBegin());
		  int end = Math.max(arg1.getBegin(), arg2.getBegin());
		  begin = Math.max(0, begin - 50);
		  end = Math.min(text.length(), end + 50);
		  return String.format(
				  "%s(%s(type=%d!%d-%d!%s), %s(type=%d!%d-%d!%s)) in ...%s...",
				  relation.getCategory(),
				  arg1.getCoveredText(),
				  arg1.getTypeID(),
				  //add extra
				  arg1.getBegin(),
				  arg1.getEnd(),
				  arg1Type,
				  
				  arg2.getCoveredText(),
				  arg2.getTypeID(),
				  //add extra
				  arg2.getBegin(),
				  arg2.getEnd(),
				  arg2Type,
				  text.substring(begin, end).replaceAll("[\r\n]", " "));
	  }

}

package org.apache.ctakes.fhir.cc;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.fhir.resource.PractitionerCtakes;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hl7.fhir.dstu3.model.Bundle;

/**
 * Prototype writer for fhir json.
 *
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/20/2017
 */
@PipeBitInfo(
      name = "FhirJsonWriter",
      description = "For ctakes_401.", role = PipeBitInfo.Role.WRITER
)
final public class FhirJsonWriter extends JCasAnnotator_ImplBase {

   static private final Logger LOGGER = Logger.getLogger( "FhirJsonWriter" );

   /**
    * {@inheritDoc}
    */
   @Override
   public void initialize( final UimaContext context ) throws ResourceInitializationException {
      // Always call the super first
      super.initialize( context );

      // place AE initialization code here

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final JCas jCas ) throws AnalysisEngineProcessException {
      LOGGER.info( "Processing ..." );

      final Bundle bundle = FhirDocComposer.composeDocFhir( jCas, PractitionerCtakes.getInstance() );

      final FhirContext fhirContext = FhirContext.forDstu3();
      final IParser jsonParser = fhirContext.newJsonParser();
      jsonParser.setPrettyPrint( true );
      final String json = jsonParser.encodeResourceToString( bundle );
      System.out.println( json );
      System.out.println();
      System.out.println();

      LOGGER.info( "Finished." );
   }


}

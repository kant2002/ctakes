

/* First created by JCasGen Wed Jan 11 14:37:38 EST 2012 */
package edu.mayo.bmi.uima.core.type.syntax;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Equivalent to cTAKES: edu.mayo.bmi.uima.chunker.type.NP
 * Updated by JCasGen Wed Jan 11 14:37:38 EST 2012
 * XML source: C:/Users/CH150124/workspace_SHARPn/common-type-system/desc/common_type_system.xml
 * @generated */
public class NP extends Chunk {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NP.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NP() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NP(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NP(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NP(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    
package AI_II;


/**
 * @author Ravi Mohan
 * 
 */
public interface AttributeSpecification {

	boolean isValid(String string);

	String getAttributeName();

	Attribute createAttribute(String rawValue);
}
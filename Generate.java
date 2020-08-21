import java.util.HashMap;

/**
 * Generate class for Compiler
 *
 * @author Yi Xin Huang
 */

public class Generate extends AbstractGenerate {
    HashMap<String, Variable> variables = new HashMap<String, Variable>();

    /**
     * Prints out explanatory message of the error
     *
     * @param token
     * @param explanatoryMessage
     * @throws CompilationException
     */
    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        System.out.println("rggERROR "+explanatoryMessage);
        throw new CompilationException(explanatoryMessage, token.lineNumber);
    }

    /**
     * Add variable objects to the hashmap
     *
     * @param v The variable to add
     */
    @Override
    public void addVariable( Variable v ) {
        if (getVariable(v.identifier) != null) {
            //variable exists so don't add another variable
        } else {
            variables.put(v.identifier, v);
            System.out.println( "rggDECL " + v );
        }
    }

    /**
     * Get variable object from the hashmap
     *
     * @param identifier The identifier to match
     * @return
     */
    public Variable getVariable(String identifier) {
        return variables.get(identifier);
    }
}

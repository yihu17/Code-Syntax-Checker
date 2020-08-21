import java.io.IOException;
/**
 * Syntax Analyser for Compiler
 *
 * @author Yi Xin Huang
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {
    String fileName;

    /**
     *Class Constructor
     *Takes in the file name and initialises a new LexicalAnalyser for the declared file
     *
     * @param fileName
     */
    public SyntaxAnalyser(String fileName) {
        this.fileName = fileName;

        try {
            lex = new LexicalAnalyser(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Grammar for statement part:
     *    Starts with begin symbol terminal (terminal)
     *    Goes into statement list function (Non-terminal)
     *    Ends with end symbol terminal (terminal)
     *
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("StatementPart");
        acceptTerminal(Token.beginSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("StatementPart");
    }

    /**
     * Grammar for the statement list:
     *    Goes into statement function (Non-terminal)
     *    IF the next symbol is a ';',
     *       Accepts ';' (terminal)
     *       then recur itself (Non-terminal)
     *    otherwise continue
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _statementList_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("StatementList");
        _statement_();
        if(nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);
            _statementList_();
        }
        myGenerate.finishNonterminal("StatementList");
    }

    /**
     * Grammar for statement:
     *    check if next symbol is any of the following symbol and if so, move into the respective functions:
     *       symbols: IDENTIFIER, IF, WHILE, CALL, DO, FOR
     *    if not raised error for incorrect token
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _statement_() throws IOException, CompilationException {
        switch (nextToken.symbol){
            case Token.identifier:
                myGenerate.commenceNonterminal("Statement");
                _assignmentStatement_();
                break;

            case Token.ifSymbol:
                myGenerate.commenceNonterminal("Statement");
                _ifStatement_();
                break;

            case Token.whileSymbol:
                myGenerate.commenceNonterminal("Statement");
                _whileStatement_();
                break;

            case Token.callSymbol:
                myGenerate.commenceNonterminal("Statement");
                _procedureStatement_();
                break;

            case Token.doSymbol:
                myGenerate.commenceNonterminal("Statement");
                _untilStatement_();
                break;

            case Token.forSymbol:
                myGenerate.commenceNonterminal("Statement");
                _forStatement_();
                break;
            default:
                reportError(nextToken, errorMessage(nextToken, Token.getName(Token.identifier) + "/" + Token.getName(Token.ifSymbol) + "/" + Token.getName(Token.whileSymbol) + "/" + Token.getName(Token.callSymbol) + "/" + Token.getName(Token.doSymbol) + "/" + Token.getName(Token.forSymbol)));
        }
        myGenerate.finishNonterminal("Statement");
    }

    /**
     * Grammar for assignment statement:
     *     Accepts identifier and becomes symbol
     *     If the next symbol is a string constant
     *         accept string constant
     *     else goes into the expression function
     *     Then creates a variable object to be added to the variables Hashmap in generate class
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _assignmentStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("AssignmentStatement");
        String variableName = nextToken.text; //get the name of the variable
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);

        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
            Variable s = new Variable(variableName, Variable.Type.STRING); //declare new variable
            myGenerate.addVariable(s); //add variable to hashmap
        } else {
            _expression_();
            Variable n = new Variable(variableName, Variable.Type.NUMBER);
            myGenerate.addVariable(n);
        }

        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     *Grammar for if statement:
     *     Accepts IF symbol
     *     Goes into condition function
     *     Accept then symbol
     *     Goes into statement list function
     *     if there's an else symbol then accept ELSE symbol and calls statement list function
     *     Accepts END symbol
     *     Accepts IF symbol
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _ifStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("IfStatement");
        acceptTerminal(Token.ifSymbol);
        _condition_();
        acceptTerminal(Token.thenSymbol);
        _statementList_();
        if(nextToken.symbol == Token.elseSymbol) { //check for else symbol
            acceptTerminal(Token.elseSymbol);
            _statementList_();
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("IfStatement");
    }

    /**
     * Grammar for while statement:
     *     accept WHILE symbol
     *     goes into condition function
     *     accept LOOP symbol
     *     goes into statement list function
     *     accept END symbol
     *     accept LOOP symbol
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _whileStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("WhileStatement");
        acceptTerminal(Token.whileSymbol);
        _condition_();
        acceptTerminal(Token.loopSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * Grammar for procedure statement:
     *     accepts CALL symbol
     *     accepts IDENTIFIER
     *     accepts LEFT PARENTHESIS
     *     goes into argument list function
     *     accepts RIGHT PARENTHESIS
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _procedureStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("ProcedureStatement");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        _argumentList_();
        acceptTerminal(Token.rightParenthesis);
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * Grammar for until statement:
     *     accepts DO symbol
     *     goes into statement list
     *     accepts UNTIL symbol
     *     goes into condition
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _untilStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("UntilStatement");
        acceptTerminal(Token.doSymbol);
        _statementList_();
        acceptTerminal(Token.untilSymbol);
        _condition_();
        myGenerate.finishNonterminal("UntilStatement");
    }

    /**
     * Grammar for for statement:
     *     accepts FOR symbol
     *     accepts LEFT PARENTHESIS symbol
     *     goes into assignment statement function
     *     accepts SEMICOLON symbol
     *     goes into condition function
     *     accepts SEMICOLON symbol
     *     goes into assignment statement function
     *     accepts RIGHT PARENTHESIS symbol
     *     accepts DO symbol
     *     goes into statement list function
     *     accepts END symbol
     *     accepts LOOP symbol
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _forStatement_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("ForStatement");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        String vname = nextToken.text; //get variable name
        Variable tempV = myGenerate.getVariable(vname); //get variable
        _assignmentStatement_();
        acceptTerminal(Token.semicolonSymbol);
        _condition_();
        acceptTerminal(Token.semicolonSymbol);
        _assignmentStatement_();
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        _statementList_();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        //if variable is null, it means that the variable is not in hashmap so its a temp variable
        //thus remove the name
        if (tempV == null) {
            myGenerate.removeVariable(myGenerate.getVariable(vname));
        }
        myGenerate.finishNonterminal("ForStatement");
    }

    /**
     * Grammar for argument list statement:
     *     accepts IDENTIFIER
     *     if its a COMMA symbol then accepts COMMA symbol and go into argument list funciton
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _argumentList_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        if (nextToken.symbol == Token.commaSymbol){
            acceptTerminal(Token.commaSymbol);
            _argumentList_();
        }
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
     * Grammar for condition:
     *     accepts IDENTIFIER
     *     goes into conditional operator function
     *     accept either IDENTIFIER, NUMBER CONSTANT or STRING CONSTANT
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _condition_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        _conditionalOperator_();
        switch (nextToken.symbol){
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                reportError(nextToken, errorMessage(nextToken, Token.getName(Token.identifier)+ "/" + Token.getName(Token.numberConstant) + "/" + Token.getName(Token.stringConstant)));
        }
        myGenerate.finishNonterminal("Condition");
    }

    /**
     * Conditional Operator function:
     *     accepts either '>', '>=', '=', '!=', '<', '<='
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _conditionalOperator_() throws  IOException, CompilationException {
        switch (nextToken.symbol){
            case Token.greaterThanSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                myGenerate.commenceNonterminal("ConditionalOperator");
                acceptTerminal(Token.lessEqualSymbol);
                break;
            default:
                reportError(nextToken, errorMessage(nextToken, Token.getName(Token.greaterThanSymbol) + "/" + Token.getName(Token.greaterEqualSymbol) + "/" + Token.getName(Token.equalSymbol) + "/" + Token.getName(Token.notEqualSymbol) + "/" + Token.getName(Token.lessThanSymbol) + "/" + Token.getName(Token.lessEqualSymbol)));
        }
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * Grammar for expression statement:
     *     goes into term function
     *     if next symbol is a '+' or '-' then recur itself
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void  _expression_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Expression");
        Variable tempv = myGenerate.getVariable(nextToken.text); //get variable
        _term_();

        if(nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
            switch(nextToken.symbol) {
                case Token.plusSymbol:
                    acceptTerminal(Token.plusSymbol);
                    _expression_();
                    break;
                case Token.minusSymbol:
                    if (tempv.type != Variable.Type.STRING) { //check if the variable is a string, if it is throw an error
                        acceptTerminal(Token.minusSymbol);
                        _expression_();
                    } else {
                        reportError(nextToken, "line " + nextToken.lineNumber + " in " + this.fileName+": Invalid operation rules on variable: "+tempv.identifier);
                    }
                    break;
                default:
                    reportError(nextToken, errorMessage(nextToken, Token.getName(Token.plusSymbol)+ "/" + Token.getName(Token.minusSymbol)));
            }
        }
        myGenerate.finishNonterminal("Expression");
    }

    /**
     * Grammar for term statement
     *     goes into factor function
     *     if next symbol is '*' or '/' and variable is not a String then recur itself
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _term_() throws  IOException, CompilationException {
        myGenerate.commenceNonterminal("Term");
        Variable tempv = myGenerate.getVariable(nextToken.text);
        _factor_();
        if (nextToken.symbol == Token.timesSymbol || nextToken.symbol == Token.divideSymbol) {
            if (tempv.type != Variable.Type.STRING) {
                switch(nextToken.symbol) {
                    case Token.divideSymbol:
                        acceptTerminal(Token.divideSymbol);
                        _term_();
                        break;
                    case Token.timesSymbol:
                        acceptTerminal(Token.timesSymbol);
                        _term_();
                        break;
                    default:
                        reportError(nextToken, errorMessage(nextToken, Token.getName(Token.divideSymbol)+ "/" + Token.getName(Token.timesSymbol)));
                }
            } else {
                reportError(nextToken, "line " + nextToken.lineNumber + " in " + this.fileName+": Invalid operation rules on variable: "+tempv.identifier);
            }
        }
        myGenerate.finishNonterminal("Term");
    }

    /**
     * Grammar for factor statement:
     *     accepts next symbol if its IDENTIFIER, NUMBER CONSTANT or LEFT PARENTHESIS
     *     if its left parenthesis, then expression function will be called and end with RIGHT PARENTHESIS
     *
     * @throws IOException
     * @throws CompilationException
     */
    public void _factor_() throws  IOException, CompilationException {
        switch(nextToken.symbol){
            case Token.identifier:
                myGenerate.commenceNonterminal("Factor");
                if (myGenerate.getVariable(nextToken.text) != null) { //check if variable exists
                    acceptTerminal(Token.identifier);
                } else {
                    reportError(nextToken, "line " + nextToken.lineNumber + " in " + this.fileName+": Variable "+nextToken.text+" not defined");
                }
                break;
            case Token.numberConstant:
                myGenerate.commenceNonterminal("Factor");
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                myGenerate.commenceNonterminal("Factor");
                acceptTerminal(Token.leftParenthesis);
                _expression_();
                acceptTerminal(Token.rightParenthesis);
                break;
            default:
                reportError(nextToken, errorMessage(nextToken, Token.getName(Token.identifier)+ "/" + Token.getName(Token.numberConstant)));
        }
        myGenerate.finishNonterminal("Factor");
    }

    /**
     * Constructs the error message taking in the token and expected tokens as parameters
     *
     * @param token
     * @param expected
     * @return explanatoryMessage
     */
    public String errorMessage(Token token, String expected) {
        return "line " + nextToken.lineNumber + " in " + this.fileName + ": Expected token(s) " + expected + " but found " + Token.getName(token.symbol) + ".";
    }

    /**
     * Accept terminal checks if the token symbol is the correct symbol
     *
     * @param symbol
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if(symbol == nextToken.symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        } else {
            reportError(nextToken, errorMessage(nextToken, Token.getName(symbol)));
        }
    }

    /**
     * Function that reports the error and throws compilation exception
     *
     * @param token
     * @param explanatoryMessage
     * @throws CompilationException
     */
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        myGenerate.reportError(token, explanatoryMessage);
    }
}

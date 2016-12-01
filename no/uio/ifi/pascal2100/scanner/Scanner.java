package no.uio.ifi.pascal2100.scanner;

import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

import java.io.*;

public class Scanner {
    public Token curToken = null, nextToken = null; //readNextToken is supposed to use these
    
    public boolean first = true; //readNextToken is supposed to use these

    private LineNumberReader sourceFile = null;
    private String sourceFileName, sourceLine = "";
    private int sourcePos = 0;

    public Scanner(String fileName) {
    	sourceFileName = fileName;
    	try {
    		sourceFile = new LineNumberReader(new FileReader(fileName));
    	} catch (FileNotFoundException e) {
    		Main.error("Cannot read " + fileName + "!");
    	}

    	readNextLine();
    	readNextToken();  readNextToken();
    }
    
    public String identify() {//can be used for logging and testing
    	return "Scanner reading " + sourceFileName;
    }


    public int curLineNum() {
    	return curToken.lineNum;
    }

    
    private void error(String message) {
    	Main.error("Scanner error on line " + curLineNum() + ": " + message);
    }
    
    /**
     * Reads from the sourceLine variable one character at a time until it has read a complete token.
     * When it discovers that the end of a token has been reached, the token that has been read is identified
     * and a new Token-object of the correct kind is created and placed in the nextToken pointer.
     */

    public void readNextToken() {
    	// Del 1 her
    	String nextSymbol = "";
    	int commentLineStart = 0;
	
    	//below are 5 switches used to continue the creation of tokens that contain more than 1 character or maintain ignoring comments
    	boolean quotationOn = false;
    	boolean commentOn = false; //handles slash star
    	boolean commentOn2 = false; //handles curly brackets
    	boolean nameOn = false;
    	boolean numberOn = false;
    	boolean symbolOn = false;
    
    	
    
    	/* else{//not end of file */
	
    		if(first){
    			first = false;
    		}
    		else{
    			curToken = nextToken;
    			nextToken = null;
    		}
    		
    		while(nextToken == null){		
    			
    			if(sourceFile == null){//if the end of the file is reached, this is triggered. 		
    				if(commentOn == true){
    					error("\nERROR: The scanner has stopped working due to detecting an endless comment. Missing a */ . comment starts at line : " + commentLineStart);
						System.exit(0);
    				}
    				else if(commentOn2 == true){
    					error("\nERROR: The scanner has stopped working due to detecting an endless comment. Missing a } . comment starts at line : " + commentLineStart);
						System.exit(0);
    				}
    				
    				else{
    					nextToken = new Token(eofToken, curLineNum());
    					nextSymbol = ""; //just to be safe
    				}	
    	    	}
    			
    			if(sourceLine.length() == sourcePos){//new line detected
    				if(quotationOn == true){//handles cases where a string literal fails to end at the line
    					error("\nERROR: The scanner has stopped working due to detecting an endless string literal. Missing a ' . string literal starts at line : " + curLineNum());
						System.exit(0);
    				}
    				readNextLine();
    				sourcePos = -1;//this is set to -1 to compensate for the ++ at the end
    			}
    			
    			else if(commentOn == true){//the char is part of a comment
    				if(nextSymbol.contains("*")){//trying to find the end
    					if(sourceLine.charAt(sourcePos) == '/'){
    						commentOn = false;//the end of the comment has been reached
    						nextSymbol = "";
    					}
    					else{
    						nextSymbol = "";//the end was not found afterall, the nextSymbol is reset
    					}
    				}
    				if(sourceLine.charAt(sourcePos) == '*'){//the end may have been found, next char will test it (see above)
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    			}	
    			
    			else if(commentOn2 == true){//the char is part of a curly bracket comment
    				if(sourceLine.charAt(sourcePos) == '}'){
    					commentOn2 = false;//the end of the curly bracket commment has been reached
    					nextSymbol = "";
    				}
    			}
    			
    			else if(quotationOn == true){//the char is part of a quotation for read, write, etc...
    				if(sourceLine.charAt(sourcePos) == '\''){//supposed to check for apostrophe, this could be wrong
    					
    					sourcePos++;//goes temporarily one step forward to check for a double apostrophe.
    					
    					if(sourcePos == sourceLine.length()){//due to the above, this condition is required in cases where the last char in the line was a apostrophe. this is always an error, but it is handled elsewhere
    						sourcePos--;//rewind
        					quotationOn = false;
        					nextToken = new Token("Any string", nextSymbol, sourceFile.getLineNumber());
        					nextSymbol = "";//Whenever a token is made, this needs to be done immediately afterwards.
    					}
    										
    					else if(sourceLine.charAt(sourcePos) == '\''){//double quotation found
    						nextSymbol += sourceLine.charAt(sourcePos);
    					}
    					
    					else{
    						sourcePos--;//rewind
    						quotationOn = false;
    						nextToken = new Token("Any string", nextSymbol, sourceFile.getLineNumber());
    						nextSymbol = "";//Whenever a token is made, this needs to be done immediately afterwards.
    					}
    				
    				}
    				else{
    					nextSymbol += sourceLine.charAt(sourcePos);//continues adding onto the quotation
    				}
    			}
		
    			else if(nameOn == true){
    				if(isLetterAZ(sourceLine.charAt(sourcePos)) == true || isDigit(sourceLine.charAt(sourcePos)) == true ){//this assumes method names can contain numbers
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else{
    					nextToken = new Token(nextSymbol, sourceFile.getLineNumber());
    					nextSymbol = "";
    					nameOn = false;
    					sourcePos--;//this is done to allow the new char to be analysed after the new token for the previous stuff is made
    				}	
    			}
		
    			else if(numberOn == true){
    				if(isDigit(sourceLine.charAt(sourcePos)) == true){
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else{
    					nextToken = new Token(Integer.parseInt(nextSymbol), sourceFile.getLineNumber());
    					nextSymbol = "";
    					numberOn = false;
    					sourcePos--;//this is done to allow the new char to be analysed after the new token for the previous stuff is made
    				}	
    			}
		
    			else if(symbolOn == true){//this handles cases where symbols have more than 1 characters such as ><, it also handles comments
    				if(nextSymbol.equals("/")){//new comment found
    					if(sourceLine.charAt(sourcePos) == '*'){
    						commentLineStart = sourceFile.getLineNumber();
    						symbolOn = false;
    						commentOn = true;
    					}
    					else{
    					
    						error("\nERROR: The scanner has stopped working due to detecting / which is an invalid symbol unless it comes with * right after\n");
    						System.exit(0);
    					}
				
    				}
			
    				else if(nextSymbol.equals(":")){
    					if(sourceLine.charAt(sourcePos) == '='){
    						nextToken = new Token(assignToken, sourceFile.getLineNumber());
    					}
    					else{
    						nextToken = new Token(colonToken, sourceFile.getLineNumber());
    						sourcePos--; //reanalyse the currently read char as part of a new token on next run of method
    					}
    					nextSymbol = "";
    					symbolOn = false;
    				}
    				else if(nextSymbol.equals(">")){
    					if(sourceLine.charAt(sourcePos) == '='){
    						nextToken = new Token(greaterEqualToken, sourceFile.getLineNumber());
    					}
    					else{
    						nextToken = new Token(greaterToken, sourceFile.getLineNumber());
    						sourcePos--;
    					}
    					nextSymbol = "";
    					symbolOn = false;
    				}
    				else if(nextSymbol.equals(".") ){
    					if(sourceLine.charAt(sourcePos) == '.'){
    						nextToken = new Token(rangeToken, sourceFile.getLineNumber());
    					}
    					else{
    						nextToken = new Token(dotToken, sourceFile.getLineNumber());
    						sourcePos--;
    					}
    					nextSymbol = "";
    					symbolOn = false;
    				}
    				else if(nextSymbol.equals("<")){
    					if(sourceLine.charAt(sourcePos) == '='){
    						nextToken = new Token(lessEqualToken, sourceFile.getLineNumber());
    					}
    					else if(sourceLine.charAt(sourcePos) == '>'){
    						nextToken = new Token(notEqualToken, sourceFile.getLineNumber());
    					}
    					else{
    						nextToken = new Token(lessToken, sourceFile.getLineNumber());
    						sourcePos--;
    					}
    					nextSymbol = "";
    					symbolOn = false;
    				}
    				else{//a new token unrelated to the current one is found, the old one is created, the new one is ignored until the method is called next time
    				
    					error("\nERROR: the student fucked up the scanner somehow. This condition should never be reached\n");
    					System.exit(1);
    				}
			
    			}
			
		
    			//below is what happens if all switches are off
		
    			else if(sourceLine.charAt(sourcePos) == '\''){//start of quotation detected
    				quotationOn = true;
    				commentLineStart = sourceFile.getLineNumber();			
    			}		
    			
    			else if(isLetterAZ(sourceLine.charAt(sourcePos)) == true){//start of function, name, etc detected
    				nameOn = true;
    				nextSymbol += sourceLine.charAt(sourcePos);	
    			}
    			else if(isDigit(sourceLine.charAt(sourcePos)) == true){//start of a number
    				numberOn = true;
    				nextSymbol += sourceLine.charAt(sourcePos);			
    			}
    			else if(sourceLine.charAt(sourcePos) == ' ' || sourceLine.charAt(sourcePos) == '\t' ){//space outside quotation or comment detected
    				//do absolutely nothing
    			}
		
    			else{//symbol is found token(tokenkind,lnum)
		
    				//this handles cases where a symbol might consist of more than 1 char
    				if(sourceLine.charAt(sourcePos) == ':'){
    					symbolOn = true;
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else if(sourceLine.charAt(sourcePos) == '/'){
    					symbolOn = true;
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else if(sourceLine.charAt(sourcePos) == '>'){
    					symbolOn = true;
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else if(sourceLine.charAt(sourcePos) == '<'){
    					symbolOn = true;
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
    				else if(sourceLine.charAt(sourcePos) == '.'){
    					symbolOn = true;
    					nextSymbol += sourceLine.charAt(sourcePos);
    				}
			
    				//this handles singular symbol cases
    				else if(sourceLine.charAt(sourcePos) == '+'){
    					nextToken = new Token(addToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == ','){
    					nextToken = new Token(commaToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '='){
    					nextToken = new Token(equalToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '['){
    					nextToken = new Token(leftBracketToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '('){
    					nextToken = new Token(leftParToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '*'){
    					nextToken = new Token(multiplyToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == ']'){
    					nextToken = new Token(rightBracketToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == ')'){
    					nextToken = new Token(rightParToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == ';'){
    					nextToken = new Token(semicolonToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '-'){
    					nextToken = new Token(subtractToken, sourceFile.getLineNumber());
    				}
    				else if(sourceLine.charAt(sourcePos) == '{'){//curly bracket comment
    					nextSymbol = "" ;
    					commentLineStart = sourceFile.getLineNumber();
						symbolOn = false;
						commentOn2 = true;
    				}
    				else if(sourceLine.charAt(sourcePos) == '}'){
    					error("\nERROR: The scanner has stopped working due to detecting the end of a comment without a beginning\n");
    					System.exit(0);
    				}
			
    				else{
    				
    					error("\nERROR: The scanner has stopped working due to detecting an invalid symbol on line: " + sourceFile.getLineNumber() + "\n");
    					System.exit(0);
    				}
			
    			}
		
		
    			sourcePos++;//moves the tracker one char ahead. in cases where this is not desired, sourcePos-- is used.
    			
    			
    		}
    	//}
    
        Main.log.noteToken(nextToken);
    }


    private void readNextLine() {
    	if (sourceFile != null) {
    		try {
    			sourceLine = sourceFile.readLine();
    			if (sourceLine == null) {
    				sourceFile.close();  sourceFile = null;
    				sourceLine = "";  
    			} 
    			else {
    				sourceLine += " ";
    			}
    			sourcePos = 0;
    		} 
    		catch (IOException e) {
    			Main.error("Scanner error: unspecified I/O error!");
    		}
    	}
    	if (sourceFile != null){
    		Main.log.noteSourceLine(getFileLineNum(), sourceLine);
    	}
	    
    }


    private int getFileLineNum() {
    	return (sourceFile!=null ? sourceFile.getLineNumber() : 0);
    }


    // Character test utilities:

    private boolean isLetterAZ(char c) {
    	return 'A'<=c && c<='Z' || 'a'<=c && c<='z';
    }


    private boolean isDigit(char c) {
    	return '0'<=c && c<='9';
    }


    // Parser tests:

    public void test(TokenKind t) {
    	if (curToken.kind != t)
    		testError(t.toString());
    }

    public void testError(String message) {
    Main.error(curLineNum(), 
		   "Expected a " + message +
		   " but found a " + curToken.kind + "!");
    }

    public void skip(TokenKind t) {
    	test(t);  
    	readNextToken();
    }
}

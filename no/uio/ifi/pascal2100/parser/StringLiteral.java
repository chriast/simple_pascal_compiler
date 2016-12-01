package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class StringLiteral extends Constant {
    String value;
    
    public StringLiteral(int lNum, String value) {
    	super(lNum);
    	this.value = value;
    }
    
    @Override public String identify() {
    	return "<StringLiteral> "+ value+ " on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint("\"" + value + "\"");
    }

    public static StringLiteral parse(Scanner s) {
        enterParser("StringLiteral");
    	StringLiteral SL = new StringLiteral(s.curLineNum(), s.curToken.strVal);
        s.readNextToken();
        leaveParser("StringLiteral");
        return SL;
    }
    @Override
    public int getVal(Block curScope) {
    	return 0;
    }
    @Override
    public int getVal2() {
    	return 0;
    }
    
    
    @Override
    public void check(Block curScope, Library lib) {
	
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

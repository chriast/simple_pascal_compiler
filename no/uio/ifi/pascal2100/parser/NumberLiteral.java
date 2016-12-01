package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class NumberLiteral extends Constant {
    
    public NumberLiteral(int lNum, int value) {
    	super(lNum);
    	this.value = value;
    }
    
    @Override public String identify() {
    	return "<NumberLiteral> "+ value + " on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint("" + value);
    }

    public static NumberLiteral parse(Scanner s) {
        enterParser("NumberLiteral");
    	NumberLiteral NL = new NumberLiteral(s.curLineNum(), s.curToken.intVal);
        s.readNextToken();
        leaveParser("NumberLiteral");
        return NL;
    }

    @Override
    public int getVal(Block curScope) {
    	return value;
    }
    @Override
    public int getVal2() {
    	return value;
    }
    @Override
    public void check(Block curScope, Library lib) {
	
    }

    @Override public void genCode(CodeFile f) {
	f.genInstr("", "movl", "$"+value+",%eax", "");
    } 
}

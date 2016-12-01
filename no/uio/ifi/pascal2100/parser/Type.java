package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.main.*;
import no.uio.ifi.pascal2100.scanner.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public abstract class Type extends PascalSyntax {
    
    public Type(int lineNum) {
    	super(lineNum);
    }
    
    @Override public String identify() {
    	return "<Type> on line " + lineNum;
    }
    
    public static Type parse(Scanner s){
    	enterParser("Type");
    	
    	
    	if(s.nextToken.kind == rangeToken) {//range token on token after current one is checked
	    Type st = RangeType.parse(s);
	    leaveParser("Type");
	    return st;
    	}
    	
    	else if(s.curToken.kind == nameToken) {//checks for nameToken in the event there is no range
	    Type st = TypeName.parse(s);
	    leaveParser("Type");
	    
	    return st; 
     	}
    	else if(s.curToken.kind == leftParToken) {//checks for enum paranthesis
	    Type st = EnumType.parse(s);
	    leaveParser("Type");
	    return st; 
    	}
    	else if(s.curToken.kind == arrayToken) {//checks for array bracket
	    Type st = ArrayType.parse(s);
	    leaveParser("Type");
	    return st;
    	}
    	else {
	    Main.error(s.curLineNum(),"invalid type. found:" + s.curToken.kind);
	}
	
    	return null;
    }
}

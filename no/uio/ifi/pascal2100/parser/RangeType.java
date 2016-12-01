package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class RangeType extends Type {
    Constant c1;
    Constant c2;

    public RangeType(int lNum, Constant c1, Constant c2){
    	super(lNum);
		this.c1 = c1;
		this.c2 = c2;
    }

    @Override public String identify() {
	return "<RangeType> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	c1.prettyPrint(); 
    	Main.log.prettyPrint(".."); 
    	c2.prettyPrint();
    }

    public static RangeType parse(Scanner s) {
    	enterParser("RangeType");
	int num = s.curLineNum();

    	Constant c1 = Constant.parse(s);
    	s.skip(rangeToken);
    	Constant c2 = Constant.parse(s);

    	RangeType RT = new RangeType(num, c1, c2);
    	
    	leaveParser("RangeType");
    	return RT;
    }

    @Override
    public void check(Block curScope, Library lib) {
    	if(c1 instanceof NamedConst){
    		c1.check(curScope, lib);
    	}
    	if(c2 instanceof NamedConst){
    		c2.check(curScope, lib);	    
    	}
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

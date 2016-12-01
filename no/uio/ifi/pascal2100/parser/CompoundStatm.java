package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class CompoundStatm extends Statement {
	
	StatmList SL;
	
	public CompoundStatm(int n) {
		super(n);
	}
	
	
	public static CompoundStatm parse(Scanner S){
		enterParser("CompoundStatm");
		S.skip(beginToken);
		CompoundStatm CS = new CompoundStatm(S.curLineNum());
		CS.SL = StatmList.parse(S);
		S.skip(endToken);
		leaveParser("CompoundStatm");
		return CS;
	}
	
	
	@Override
	public String identify() {
		return "<CompoundStatm> on line " + lineNum;
	}

    @Override void prettyPrint() {
    	Main.log.prettyPrintLn("begin"); 
    	Main.log.prettyIndent();
    	SL.prettyPrint(); 
    	Main.log.prettyOutdent();
    	Main.log.prettyPrint("end");
    }


    @Override
    public void check(Block curScope, Library lib) {
	SL.check(curScope, lib);
	
    }
    
    @Override public void genCode(CodeFile f) {
	SL.genCode(f);
    }
}

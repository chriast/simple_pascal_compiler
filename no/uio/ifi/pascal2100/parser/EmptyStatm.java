package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class EmptyStatm extends Statement {

	public EmptyStatm(int n) {
		super(n);
	}
	
	@Override
	public String identify() {
		return "<EmptyStatm> on line " + lineNum;
	}

    @Override void prettyPrint() {
    	Main.log.prettyPrint("");
    }
	
	public static EmptyStatm parse(Scanner S){
		enterParser("empty-statm");
		EmptyStatm ES = new EmptyStatm(S.curLineNum());
		leaveParser("empty-statm");
		return ES;
	}

    @Override
    public void check(Block curScope, Library lib) {
	
    }

    @Override public void genCode(CodeFile f) {

    } 
}

package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;


public class InnerExp extends Factor {

	Expression E;
	
	public InnerExp(int n) {
		super(n);
	}
	
	@Override
	public String identify() {
		return "<InnerExp> on line " + lineNum;
	}

    @Override void prettyPrint() {
    	Main.log.prettyPrint("(");
    	E.prettyPrint();
    	Main.log.prettyPrint(")");
    }
	
	public static InnerExp parse(Scanner S){
		enterParser("InnerExp");
		S.skip(leftParToken);
		InnerExp IE = new InnerExp(S.curLineNum());
		IE.E = Expression.parse(S);
		S.skip(rightParToken);
		leaveParser("innerExp");
		return IE;
	}

    @Override
    public void check(Block curScope, Library lib) {
    	E.check(curScope, lib);	
    }

    @Override void genCode(CodeFile f) {
    	E.genCode(f);
    }
}

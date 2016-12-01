package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;
import no.uio.ifi.pascal2100.main.*;


public class Expression extends PascalSyntax {

	public SimpleExpr SE1;
	public SimpleExpr SE2;
	public RelOpr RO;
	
	public Expression(int n) {
		super(n);
	}

	@Override
	public String identify() {
		return "<Expression> on line " + lineNum;
	}
    
    @Override void prettyPrint() {
    	SE1.prettyPrint();
	if(RO != null) {
	    RO.prettyPrint();
	    SE2.prettyPrint();
	}
    }
    //A failed attempt to make the compiler stop printing the operators when they aren't used. The change in SimpleExpr causes the compiler to crash on line 27 when attempting to compile easter.pac	
    /*
	public static Expression parse(Scanner S){
		enterParser("Expression");
		Expression E = new Expression(S.curLineNum());
		E.SE1 = SimpleExpr.parse(S);
		
		if(S.curToken.kind == equalToken){
			E.RO = RelOpr.parse(S);
		}
		if(S.curToken.kind == notEqualToken){
			E.RO = RelOpr.parse(S);
		}
		if(S.curToken.kind == lessToken){
			E.RO = RelOpr.parse(S);
		}
		if(S.curToken.kind == lessEqualToken){
			E.RO = RelOpr.parse(S);
		}
		if(S.curToken.kind == greaterToken){
			E.RO = RelOpr.parse(S);
		}
		if(S.curToken.kind == greaterEqualToken){
			E.RO = RelOpr.parse(S);
		}
		
		if(E.RO != null){
			E.SE2 = SimpleExpr.parse(S);
		}	
		
		leaveParser("Expression");
		return E;
	}*/
    
    public static Expression parse(Scanner S){
    	enterParser("Expression");
    	Expression E = new Expression(S.curLineNum());
    	E.SE1 = SimpleExpr.parse(S);
    	E.RO = RelOpr.parse(S);
    	if(E.RO != null){
    		E.SE2 = SimpleExpr.parse(S);
    	}	
    	leaveParser("Expression");
    	return E;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
    	SE1.check(curScope, lib);
    	if(SE2 != null){
    		SE2.check(curScope, lib);
    	}	
    }

    @Override public void genCode(CodeFile f) {
    	SE1.genCode(f);
    	if(RO != null) {
    		f.genInstr("", "pushl", "%eax", "");//Puts the value on the stack
    		SE2.genCode(f);//Gets the result from the next simple expression
    		f.genInstr("", "movl", "%eax,%ecx", "");//Moves the second result to ecx
    		f.genInstr("", "popl", "%ecx", "");//Gets the previous result back from the stack	   
    		RO.genCode(f);
    	}
    }
}

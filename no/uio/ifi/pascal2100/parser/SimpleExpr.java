package no.uio.ifi.pascal2100.parser;

import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

import java.util.ArrayList;
import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class SimpleExpr extends PascalSyntax {
	
    public PrefixOpr PO;
    public ArrayList<TermOpr> TOL = new ArrayList<TermOpr>();
    public ArrayList<Term> TL = new ArrayList<Term>();
    
    public SimpleExpr(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<SimpleExpr> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	if(PO != null) {
    	    PO.prettyPrint();
    	}
    	
    	TL.get(0).prettyPrint();
    	int i = 1;
    	while(i < TOL.size()) {
    	    TOL.get(i-1).prettyPrint();
    	    TL.get(i).prettyPrint();
    	    i++;
    	}
    }
    //A failed attempt to make the compiler stop printing the operators when they aren't used. It causes the compiler to crash on line 27 when attempting to compile easter.pac
	/* 
	public static SimpleExpr parse(Scanner S){
		enterParser("SimpleExpr");	
		SimpleExpr simpleexpr = new SimpleExpr(S.curLineNum());
		
		if(S.curToken.kind == addToken){
			simpleexpr.PO = PrefixOpr.parse(S);
		}
		if(S.curToken.kind == subtractToken){
			simpleexpr.PO = PrefixOpr.parse(S);
		}
		
		
		
		Term tmpTerm = Term.parse(S); //parse term, and send this to term so that term can analyse term operators and send them back here
		simpleexpr.TL.add(tmpTerm);
		
		TermOpr tmpOpr = null;
		if(S.curToken.kind == addToken){
			tmpOpr = TermOpr.parse(S);
		}
		if(S.curToken.kind == subtractToken){
			tmpOpr = TermOpr.parse(S);
		}
		if(S.curToken.kind == orToken){
			tmpOpr = TermOpr.parse(S);
		}
		
		
		 
		while(tmpOpr != null){
			
			simpleexpr.TOL.add(tmpOpr);//this adds the previous one
			tmpOpr = null;
			tmpTerm = Term.parse(S);
			simpleexpr.TL.add(tmpTerm);
			
			if(S.curToken.kind == addToken){
				tmpOpr = TermOpr.parse(S);
			}
			if(S.curToken.kind == subtractToken){
				tmpOpr = TermOpr.parse(S);
			}
			if(S.curToken.kind == orToken){
				tmpOpr = TermOpr.parse(S);
			}
			else{
				
				tmpOpr = null;
			}
		}
		
		leaveParser("SimpleExpr");
		return simpleexpr;
	}*/
    
    public static SimpleExpr parse(Scanner S){
    	enterParser("SimpleExpr");	
    	SimpleExpr simpleexpr = new SimpleExpr(S.curLineNum());
    	simpleexpr.PO = PrefixOpr.parse(S);
    	Term tmpTerm = Term.parse(S); //parse term, and send this to term so that term can analyse term operators and send them back here
    	simpleexpr.TL.add(tmpTerm);
    	TermOpr tmpOpr = TermOpr.parse(S); 
    	while(tmpOpr != null){
    		simpleexpr.TOL.add(tmpOpr);
		    tmpTerm = Term.parse(S);
		    simpleexpr.TL.add(tmpTerm);
		    tmpOpr = TermOpr.parse(S);
    	}
    	leaveParser("SimpleExpr");
    	return simpleexpr;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
    	for(int i = 0;i<TL.size();i++){
    		TL.get(i).check(curScope, lib);
    	}
	
    }
    
    @Override public void genCode(CodeFile f) {
    	TL.get(0).genCode(f);//Gets the value from the first term into eax
    	int i = 1;
    	while(i < TL.size()) {
    		f.genInstr("", "pushl", "%eax", "");//Puts the value on the stack
    		TL.get(i).genCode(f);//Gets the next term
    		f.genInstr("", "movl", "%eax,%ecx", "");//Moves the second term to ecx
    		f.genInstr("", "popl", "%eax", "");//Gets the previous value back from the stack
    		TOL.get(i-1).genCode(f);
    		i++;
    	}

    	if(PO != null) {
    		PO.genCode(f);
    	}
    }
}

package no.uio.ifi.pascal2100.parser;

import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;

public class Term extends PascalSyntax {
	
    ArrayList<FactorOpr> FOL = new ArrayList<FactorOpr>();
    ArrayList<Factor> FL = new ArrayList<Factor>();
    
    
    public Term(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<Term> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	FL.get(0).prettyPrint();
    	int i = 1;
    	while(i < FOL.size()) {
	    FOL.get(i-1).prettyPrint();
	    FL.get(i).prettyPrint();
	    i++;
    	}
    }
    //A failed attempt to make the compiler stop printing the operators when they aren't used. The change in SimpleExpr causes the compiler to crash on line 27 when attempting to compile easter.pac
    /*
      public static Term parse(Scanner S){
      enterParser("Term");
		Term term = new Term(S.curLineNum());
		Factor tmpFactor = Factor.parse(S);
		term.FL.add(tmpFactor);
		
		FactorOpr tmpOpr = null;
		
		if(S.curToken.kind == multiplyToken){
			tmpOpr = FactorOpr.parse(S);
		}
		if(S.curToken.kind == divToken){
			tmpOpr = FactorOpr.parse(S);
		}
		if(S.curToken.kind == modToken){
			tmpOpr = FactorOpr.parse(S);
		}
		if(S.curToken.kind == andToken){
			tmpOpr = FactorOpr.parse(S);
		}
		
		while(tmpOpr != null){
			term.FOL.add(tmpOpr);//adds the previous one.
			tmpOpr = null;
			tmpFactor = Factor.parse(S);
			term.FL.add(tmpFactor);
			
			if(S.curToken.kind == multiplyToken){
				tmpOpr = FactorOpr.parse(S);
			}
			if(S.curToken.kind == divToken){
				tmpOpr = FactorOpr.parse(S);
			}
			if(S.curToken.kind == modToken){
				tmpOpr = FactorOpr.parse(S);
			}
			if(S.curToken.kind == andToken){
				tmpOpr = FactorOpr.parse(S);
			}
			else{
				tmpOpr = null;
			}
			
		}
		
		leaveParser("Term");
		return term;
			
	}
	*/
    
    public static Term parse(Scanner S){
	enterParser("Term");
	Term term = new Term(S.curLineNum());
	Factor tmpFactor = Factor.parse(S);
	term.FL.add(tmpFactor);
	FactorOpr tmpOpr = FactorOpr.parse(S);
	while(tmpOpr != null){
	    term.FOL.add(tmpOpr);
	    tmpFactor = Factor.parse(S);
	    term.FL.add(tmpFactor);
	    tmpOpr = FactorOpr.parse(S);
	}
	leaveParser("Term");
	return term;
	
    }
    
    @Override
    public void check(Block curScope, Library lib) {
	for(int i = 0;i<FL.size();i++){
	    if(FL.get(i) instanceof Variable){//this is where we determine whether its a variable or a namedConstant that factor is supposed to make for the parser
		Variable tmp = (Variable) FL.get(i);
		String lowerCase = tmp.name;
		lowerCase.toLowerCase();//I am not taking any chances!
		PascalDecl pas = curScope.findDecl(lowerCase.toLowerCase(), tmp,true);//this is used for checking whether the variable should be a namedConstant
		if(pas instanceof ConstDecl){//create a NamedConstant
		    int linenum = tmp.lineNum;
		    String name2 = pas.name;
		    NamedConst nc = new NamedConst(name2,linenum);
		    
		    ArrayList<Factor> tmpList = new ArrayList<Factor>();//create new arraylist and put the new element in it in place of the false variable					
		    int j = 0;
		    while(j<i){
			tmpList.add(FL.get(j));
			j++;
		    }
		    tmpList.add(nc);
		    j++;
		    while(j<FL.size()){
			tmpList.add(FL.get(j));
			j++;
		    }					
		    FL = tmpList;
		    
		    nc.check(curScope, lib);
		}
		else{
		    tmp.check(curScope, lib);
		}
	    }
	    else{
		FL.get(i).check(curScope, lib);
	    }
	}	
    }

    @Override public void genCode(CodeFile f) {
	FL.get(0).genCode(f);//Gets the value from the first factor into eax
	int i = 1;
	while(i < FL.size()) {
	    f.genInstr("", "pushl", "%eax", "");//Puts the value on the stack
	    FL.get(i).genCode(f);//Gets the next factor
	    f.genInstr("", "movl", "%eax,%ecx", "");//Moves the second factor to ecx
	    f.genInstr("", "popl", "%eax", "");//Gets the previous value back from the stack
	    FOL.get(i-1).genCode(f);//Generates code for the operation
	    i++;
	}
    }	
}

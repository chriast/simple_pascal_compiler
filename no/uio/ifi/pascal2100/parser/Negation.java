package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;


public class Negation extends Factor {
    Factor F;
    
    public Negation(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<Negation> on line " + lineNum;
    }
    
    @Override void prettyPrint(){
    	Main.log.prettyPrint(" not ");
    	F.prettyPrint();
    }
    
    public static Negation parse(Scanner S){
	enterParser("Negation");
	S.skip(notToken);
	Negation Neg = new Negation(S.curLineNum());
	Neg.F = Factor.parse(S);
	leaveParser("Negation");
	return Neg;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
	if(F instanceof Variable){//this is where we determine whether its a variable or a namedConstant that factor is supposed to make for the parser
	    Variable tmp = (Variable) F;
	    String lowerCase = tmp.name;
	    lowerCase.toLowerCase();//I am not taking any chances!
	    PascalDecl pas = curScope.findDecl(lowerCase.toLowerCase(), tmp,true);//this is used for checking whether the variable should be a namedConstant
	    if(pas instanceof ConstDecl){
		int linenum = tmp.lineNum;
		String name2 = pas.name;
		NamedConst nc = new NamedConst(name2,linenum);
		F = nc;
		nc.check(curScope, lib);
	    }
	    else{
		tmp.check(curScope, lib);
	    }
	}
	else{
	    F.check(curScope, lib);
	}
    }
    
    @Override public void genCode(CodeFile f) {
	F.genCode(f);
	f.genInstr("", "xorl", "$0x1,%eax", "");
	
    }
}

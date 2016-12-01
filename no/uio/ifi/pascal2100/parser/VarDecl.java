package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class VarDecl extends PascalDecl {	
	
    public Type T;
	
    public VarDecl(String id,int lNum) {
    	super(id,lNum);
    }

    @Override public String identify() {	    
    	if(lineNum == -1){
	    return "<var decl> in the library";
    	}
    	return "<var decl> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	Main.log.prettyPrint(name + " : "); 
    	T.prettyPrint();
    	Main.log.prettyPrintLn(";");
    }
    
    public static VarDecl parse(Scanner s){
    	enterParser("VarDecl");
    	
    	s.test(nameToken);
    	VarDecl VD = new VarDecl(s.curToken.id, s.curLineNum());
    	s.readNextToken();//go to next token which should be equalToken
    	s.skip(colonToken);
    	
    	
	VD.T = Type.parse(s);
    	
    	
	s.skip(semicolonToken);
	
    	
    	
    	leaveParser("VarDecl");
    	return VD;
    }
    
    @Override
	public void check(Block curScope, Library lib) {
    	if(T instanceof RangeType || T instanceof ArrayType || T instanceof TypeName){
	    T.check(curScope, lib);//should work for both range and Array
    	}
    	declLevel = curScope.blockLevel;
    }
    
    @Override void checkWhetherAssignable(PascalSyntax where) {
    	//Is assignable
    }

    @Override void checkWhetherFunction(PascalSyntax where) {
    	where.error("Not a function.");
    }
    @Override void checkWhetherProcedure(PascalSyntax where) {
    	where.error("Not a procedure.");
    }
    @Override void checkWhetherValue(PascalSyntax where) {
    	//Is value
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

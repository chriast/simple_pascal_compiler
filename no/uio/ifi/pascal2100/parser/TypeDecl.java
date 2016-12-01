package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class TypeDecl extends PascalDecl {	
    public TypeName TN;
    public Type T;
	
    public TypeDecl(String id,int lNum, TypeName TN) {
    	super(id,lNum);
    	this.TN = TN;
    	this.name = TN.name;
    }

    @Override public String identify() {	    
    	if(lineNum == -1){
    		return "<type decl> in the library";
    	}
    	return "<type decl> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	TN.prettyPrint(); 
    	Main.log.prettyPrint(" = ");
    	T.prettyPrint(); 
    	Main.log.prettyPrintLn(";");
    }
    
    public static TypeDecl parse(Scanner s){
    	enterParser("TypeDecl");
    	TypeName tmpTN = TypeName.parse(s);
    	TypeDecl TD = new TypeDecl(tmpTN.name, s.curLineNum(), tmpTN);//name is almost immediately overwritten with that of typename
    	//s.readNextToken();//not needed, TypeName does the skipping
    	s.skip(equalToken);
    	TD.T = Type.parse(s);
	//tmpTN.namedRef = TD.T; mikkel, WTF is this?
	s.skip(semicolonToken);
	
    	
    		
    	leaveParser("TypeDecl");
    	return TD;
    }

    @Override
    public void check(Block curScope, Library lib) {
    	if(T instanceof RangeType || T instanceof ArrayType || T instanceof TypeName){
    		T.check(curScope, lib);//should work for both range and Array
    	}
    	declLevel = curScope.blockLevel;
    }

    @Override void checkWhetherAssignable(PascalSyntax where) {
	where.error("You cannot assign to a type.");
    }

    @Override void checkWhetherFunction(PascalSyntax where) {
    	where.error("Not a function.");
    }
    @Override void checkWhetherProcedure(PascalSyntax where) {
    	where.error("Not a procedure.");
    }
    @Override void checkWhetherValue(PascalSyntax where) {
    	where.error("Not a value.");
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

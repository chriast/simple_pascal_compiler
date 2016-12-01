package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ParamDecl extends PascalDecl {
    TypeName ptype;

    public ParamDecl(String id, int lineNum, TypeName ptype) {
    	super(id, lineNum);
    	this.ptype = ptype;
    }

    @Override public String identify() {
    	if(lineNum == -1){
	    return "<param decl> in the library";
    	}
    	return "<param decl> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint(name + ":"); 
    	ptype.prettyPrint();
    }

    public static ParamDecl parse(Scanner s) {
	enterParser("ParamDecl");
    	s.test(nameToken);
    	String name = s.curToken.id;
    	s.readNextToken();//correction
    	s.skip(colonToken);

    	int num = s.curLineNum();
    	TypeName typeName = TypeName.parse(s);
    	ParamDecl PD = new ParamDecl(name, num, typeName);

    	//it is already at the token after typeName at this point
    	

    	leaveParser("ParamDecl");
    	return PD;
    }

    @Override
    public void check(Block curScope, Library lib) {
    	ptype.check(curScope, lib);
    	declLevel = curScope.blockLevel + 1;
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

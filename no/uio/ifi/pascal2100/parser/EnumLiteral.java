package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class EnumLiteral extends PascalDecl {
    //false = 0, true = 1, all other enums are => 2
    static int enumCounter = 2;
    int enumNumber;

    public EnumLiteral(String id,int lNum) {
    	super(id,lNum);
    }

    @Override public String identify() {
    	if(lineNum == -1){
    		return "<enum literal> in the Library";
    	}
    	return "<enum literal> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint(name);

    }

    public static EnumLiteral parse(Scanner s) {
    	enterParser("EnumLiteral");
    	
    	s.test(nameToken);
    	
    	EnumLiteral EL = new EnumLiteral(s.curToken.id,s.curLineNum());
	EL.enumNumber = -1;

    	s.readNextToken();   	
    	
    	leaveParser("EnumLiteral");
    	return EL;
    }

    @Override
    public void check(Block curScope, Library lib) {
	if(enumNumber < 0) {
	    enumNumber = enumCounter;
	    enumCounter++;
	}
	declLevel = curScope.blockLevel;
    }

    @Override void checkWhetherAssignable(PascalSyntax where) {
	where.error("You cannot assign to an enum literal.");
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

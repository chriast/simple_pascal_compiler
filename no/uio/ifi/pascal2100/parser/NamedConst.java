package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

class NamedConst extends Constant {
    String name;
    PascalDecl declPointer;//This is for calls, not declarations
    //Constant nameValue;
    
    NamedConst(String id, int lNum) {
    	super(lNum);
    	this.name = id;
    }
    
    @Override public String identify() {
	return "<NamedConst> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint(name);
    }

    public static NamedConst parse(Scanner s) {
    	enterParser("NamedConst");
    	s.test(nameToken);
    	NamedConst NC = new NamedConst(s.curToken.id, s.curToken.lineNum);
    	s.readNextToken();
    	leaveParser("NamedConst");
    	return NC;
    }
    @Override
    public int getVal(Block curScope) {
    	ConstDecl cd = (ConstDecl) curScope.findDecl(name.toLowerCase(),this,true);
    	return cd.c.getVal(curScope);
    }
    @Override
    public int getVal2() {
    	ConstDecl cd = (ConstDecl) declPointer;
    	return cd.c.getVal2();
    }
    
    @Override
    public void check(Block curScope, Library lib) {
    	declPointer = curScope.findDecl(name.toLowerCase(),this,false);
    	declPointer.checkWhetherValue(this);
    }

    @Override public void genCode(CodeFile f) {
    	declPointer.genCode(f);
    } 
}

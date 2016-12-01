package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ParamDeclList extends PascalSyntax {
	
    ArrayList<ParamDecl> paramList = new ArrayList<ParamDecl>();
    int curOffset;
    

    public ParamDeclList(int lineNum) {
    	super(lineNum);
    	curOffset = 8;
    }
    
    @Override public String identify() {
    	return "<ParamDeclList> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint("(");
    	paramList.get(0).prettyPrint();
    	for(int i = 1; i < paramList.size(); i++) {
    	    Main.log.prettyPrint(", ");
    	    paramList.get(i).prettyPrint();
    	}
        Main.log.prettyPrint(")");
    }

    public static ParamDeclList parse(Scanner s) {
    	enterParser("ParamDeclList");
    	
    	s.skip(leftParToken);
    	ParamDeclList PDL = new ParamDeclList(s.curLineNum());
    	
    	ParamDecl firstItem = ParamDecl.parse(s);
    	PDL.paramList.add(firstItem);
    	
    	while(s.curToken.kind == semicolonToken) {
	    s.readNextToken();
	    ParamDecl otherItem = ParamDecl.parse(s);
	    PDL.paramList.add(otherItem);
    	}
    	
    	s.skip(rightParToken);
    	
    	leaveParser("ParamDeclList");
    	return PDL;
    }

    @Override
    public void check(Block curScope, Library lib) {
    	for(int i = 0; i < paramList.size(); i++) {
    		paramList.get(i).check(curScope, lib);
    		paramList.get(i).offset = curOffset;	  
    		curOffset += 4;
    	}
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

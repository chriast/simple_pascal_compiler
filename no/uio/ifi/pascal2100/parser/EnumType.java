package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class EnumType extends Type {
    ArrayList<EnumLiteral> enumList = new ArrayList<EnumLiteral>();

    public EnumType(int lNum){
    	super(lNum);
    }

    @Override public String identify() {
    	return "<EnumType> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrint("(");
    	enumList.get(0).prettyPrint();
    	for(int i = 1; i < enumList.size(); i++) {
    	    Main.log.prettyPrint(", ");
    	    enumList.get(i).prettyPrint();
    	}
        Main.log.prettyPrint(")");
    }

    public static EnumType parse(Scanner s) {
    	enterParser("EnumType");
    	
    	s.skip(leftParToken);
    	EnumType ET = new EnumType(s.curLineNum());//goes through all enums
    	EnumLiteral first = EnumLiteral.parse(s);//temporary pointer
    	ET.enumList.add(first);
    	
    	while(s.curToken.kind == commaToken) {
    		s.readNextToken();
    		EnumLiteral others = EnumLiteral.parse(s);
    		ET.enumList.add(others);
    	}
    	
    	s.skip(rightParToken);
    	
    	
    	
    	leaveParser("EnumType");
    	return ET;
    }

    @Override
    public void check(Block curScope, Library lib) {
	
	
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

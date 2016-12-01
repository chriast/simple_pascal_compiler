package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class TypeDeclPart extends PascalSyntax {

    ArrayList<TypeDecl> typeList = new ArrayList<TypeDecl>();

    public TypeDeclPart(int lineNum) {
    	super(lineNum);
    }

    @Override public String identify() {
    	return "<TypeDeclPart> on line " + lineNum;
    }

    @Override void prettyPrint() {
    	Main.log.prettyPrintLn("type"); 
    	Main.log.prettyIndent();
    	for(int i = 0; i < typeList.size(); i++) {
    		typeList.get(i).prettyPrint();
    	}
    	Main.log.prettyOutdent();
    }

    public static TypeDeclPart parse(Scanner s) {
    	enterParser("TypeDeclPart");
    	
    	TypeDeclPart TDP = new TypeDeclPart(s.curLineNum());
    	s.skip(typeToken);
    	
    	
    	
    	while(s.curToken.kind == nameToken && s.nextToken.kind == equalToken) {
    		TypeDecl otherItem = TypeDecl.parse(s);
        	TDP.typeList.add(otherItem);
    	}
    	leaveParser("TypeDeclPart");
    	return TDP;
    }

    @Override
    public void check(Block curScope, Library lib) {
    	for(int i = 0;i<typeList.size();i++){
    		TypeDecl tmp = typeList.get(i);
    		tmp.check(curScope, lib);
    	}
    }

    @Override public void genCode(CodeFile f) {
	
    }
}

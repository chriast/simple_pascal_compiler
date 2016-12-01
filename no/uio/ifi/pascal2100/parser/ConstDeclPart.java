package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class ConstDeclPart extends PascalSyntax {	

    public ArrayList<ConstDecl> constList = null;
	
    public ConstDeclPart(int lineNum) {
    	super(lineNum);
    }
    
    
    @Override public String identify() {
    	return "<ConstDeclPart> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	Main.log.prettyPrintLn("const"); 
    	Main.log.prettyIndent();
    	for(int i = 0; i < constList.size(); i++) {
    		constList.get(i).prettyPrint();
    	}
    	Main.log.prettyOutdent();
    }
    /*
    public static ConstDeclPart parse(Scanner s){//starts a chains of ConstDecks linked to each other
        enterParser("ConstDeclPart");
        ConstDeclPart CDP = new ConstDeclPart(s.curLineNum());
        s.skip(constToken);
        ConstDecl firstItem = ConstDecl.parse(s);
        CDP.first = firstItem;
        leaveParser("ConstDeclPart");
        return CDP;
    }
    */
    public static ConstDeclPart parse(Scanner s){
    	enterParser("ConstDeclPart");
    	ConstDeclPart CDP = new ConstDeclPart(s.curLineNum());
    	CDP.constList = new ArrayList<ConstDecl>();
    	s.skip(constToken);
    	//beginning of loop
    	
    	while(s.curToken.kind == nameToken && s.nextToken.kind == equalToken) {//check for more
    		ConstDecl tmp = ConstDecl.parse(s);
        	CDP.constList.add(tmp);
    	}
    	return CDP;
    	
    }


    @Override
    public void check(Block curScope, Library lib) {
	for(int i = 0;i<constList.size();i++){
	    ConstDecl tmp = constList.get(i);
	    tmp.check(curScope, lib);
	}
	
    }
    
    @Override public void genCode(CodeFile f) {
	
    }
}

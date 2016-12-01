package no.uio.ifi.pascal2100.parser;

import java.util.ArrayList;
import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class StatmList extends PascalSyntax {
	
    public ArrayList<Statement> list = new ArrayList<Statement>();
    
    public StatmList(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<StatmList> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	list.get(0).prettyPrint();
    	for(int i = 1; i < list.size(); i++) {
	    Main.log.prettyPrintLn(";");
	    list.get(i).prettyPrint();
    	}
	Main.log.prettyPrintLn("");
    }
    
    public static StatmList parse(Scanner s) {
    	enterParser("StatmList");
      	
    	StatmList SL = new StatmList(s.curLineNum());
    	
    	Statement tmpItem = Statement.parse(s);
    	SL.list.add(tmpItem);
    	while(s.curToken.kind == semicolonToken){
	    s.readNextToken();
	    tmpItem = Statement.parse(s);
	    SL.list.add(tmpItem);
    	}
    	
    	leaveParser("StatmList");
    	return SL;
    }
    
    @Override
    public void check(Block curScope, Library lib) {
	for(int i = 0;i<list.size();i++){
	    list.get(i).check(curScope, lib);
	}	
    }
    
    @Override public void genCode(CodeFile f) {
	for(int i = 0; i < list.size(); i++) {
	    list.get(i).genCode(f);
	}
    }
}

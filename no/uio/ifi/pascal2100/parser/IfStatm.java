package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public class IfStatm extends Statement {

    Expression E;
    Statement St1;
    Statement St2;
    
    public IfStatm(int n) {
	super(n);
    }
    
    
    public static IfStatm parse(Scanner s){
    	enterParser("IfStatm");
    	s.skip(ifToken);
    	IfStatm IS = new IfStatm(s.curLineNum());
    	IS.E = Expression.parse(s);
    	s.skip(thenToken);
    	IS.St1 = Statement.parse(s);
    	if(s.curToken.kind == elseToken){
    		s.skip(elseToken);
    		IS.St2 = Statement.parse(s);
    	}
    	leaveParser("IfStatm");
    	return IS;
    }
    
    
    @Override
    public String identify() {
    	return "<IfStatm> on line " + lineNum;
    }
    
    @Override void prettyPrint() {
    	Main.log.prettyPrint("if "); 
    	E.prettyPrint();
    	Main.log.prettyPrintLn(" then"); 
    	Main.log.prettyIndent();
    	St1.prettyPrint(); 
    	Main.log.prettyOutdent();
    	if(St2 != null) {
    		Main.log.prettyPrintLn("else"); Main.log.prettyIndent();
    		St2.prettyPrint(); Main.log.prettyOutdent();
    	}
    }


    @Override
    public void check(Block curScope, Library lib) {
    	E.check(curScope, lib);
    	St1.check(curScope, lib);
    	if(St2 != null){
    		St2.check(curScope, lib);
    	}	
    }

    @Override public void genCode(CodeFile f) {
    	String endLabel = f.getLocalLabel();
    	String elseLabel;

    	f.genInstr("", "", "", "#Start of if-statement");
    	E.genCode(f);
    	f.genInstr("", "cmpl", "$0,%eax", "");
    	if(St2 != null) {
    		//If it's an if-else statement
    		elseLabel = f.getLocalLabel();
    		f.genInstr("", "je", elseLabel, "");
    		St1.genCode(f);
    		f.genInstr("", "jmp", endLabel, "");
    		f.genInstr(elseLabel, "", "", "Else-statement");
    		St2.genCode(f);
    	}else {
    		//If it's just an if statement
    		f.genInstr("", "je", endLabel, "");
    		St1.genCode(f);
    	}

    	f.genInstr(endLabel, "", "", "#End of if-statement");
    }
}

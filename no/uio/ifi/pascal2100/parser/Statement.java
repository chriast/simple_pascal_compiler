package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public abstract class Statement extends PascalSyntax {	
	
    public Statement(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<Statement> on line " + lineNum;
    }
    
    public static Statement parse(Scanner S){
	enterParser("Statement");
	
	if (S.curToken.kind == whileToken){
	    Statement st = WhileStatm.parse(S);
	    leaveParser("Statement");
	    return st; 
	}
	else if (S.curToken.kind == beginToken){
	    Statement st = CompoundStatm.parse(S);
	    leaveParser("Statement");
	    return st;
	}
	else if (S.curToken.kind == ifToken){
	    Statement st = IfStatm.parse(S);
	    leaveParser("Statement");
	    return st;
	}
	else if(S.curToken.kind == endToken){
	    Statement st = EmptyStatm.parse(S);
	    leaveParser("Statement");
	    return st; 
	}
	else if(S.curToken.kind == nameToken){
	    if(S.nextToken.kind == leftBracketToken || S.nextToken.kind == assignToken){
		Statement st = AssignStatm.parse(S);
		leaveParser("Statement");
		return st;
	    }
	    else{
		Statement st = ProcCall.parse(S);
		leaveParser("Statement");
		return st;
	    }
	}
	
	
	else{
	    Main.error(S.curLineNum(),"Invalid statement. found: " + S.curToken.kind);
	}
	
	
	return null;
    }    
}

package no.uio.ifi.pascal2100.parser;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;

public abstract class Factor extends PascalSyntax {
    public Factor(int n) {
	super(n);
    }
    
    @Override
    public String identify() {
	return "<Factor> on line " + lineNum;
    }
    
    public static Factor parse(Scanner s){
	enterParser("Factor");
	
	if(s.curToken.kind == intValToken){			 
	    Factor f = Constant.parse(s);
	    leaveParser("Factor");
	    return f;
	}
	else if(s.curToken.kind == stringValToken){
	    Factor f = Constant.parse(s);
	    leaveParser("Factor");
	    return f;
	}
	else if(s.curToken.kind == leftParToken){
	    Factor f = InnerExp.parse(s);
	    leaveParser("Factor");
	    return f;
	}
	else if(s.curToken.kind == notToken){		
	    Factor f = Negation.parse(s);
	    leaveParser("Factor");
	    return f;
	}
	else if(s.curToken.kind == nameToken){
	    if(s.nextToken.kind == leftParToken){
		Factor f = FuncCall.parse(s);
		leaveParser("Factor");
		return f;
	    }
	    else{
		Factor f = Variable.parse(s);//it could be a named constant which will need correcting later in check, how to find out?
		leaveParser("Factor");
		return f;
	    }
	}
	else{
	    Main.error(s.curLineNum(),"Invalid factor. found" + s.curToken.kind);
	}
	return null;
    }
}

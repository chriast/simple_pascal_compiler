package no.uio.ifi.pascal2100.parser;

import java.util.HashMap;
import java.util.ArrayList;

import no.uio.ifi.pascal2100.scanner.*;
import no.uio.ifi.pascal2100.main.*;
import static no.uio.ifi.pascal2100.scanner.TokenKind.*;


public class Block extends PascalSyntax {
    public ConstDeclPart CDP = null;
    public TypeDeclPart TDP = null;
    public VarDeclPart VDP = null;
    public FuncDecl FD = null;
    public ProcDecl PD = null;
    public ArrayList<ProcDecl> PDL = new ArrayList<ProcDecl>();
    public StatmList SL = null;
    
    HashMap<String,PascalDecl> decls = new HashMap<String,PascalDecl>();
    
    Block outerScope = null;
    
    Program context = null;//What on earth is this for?
    
    int blockLevel;
    int curOffset;

    Block(int lNum) {		
	super(lNum);
	//All block-levels are initialized at 0. When check goes through the blocks this will be set to the correct value
	blockLevel = 0;
	curOffset = 32;
    }
    
    @Override public String identify() {
	return "<Block> on line " + lineNum;	
    }
    
    @Override void prettyPrint() {
    	if(CDP != null) {
	    CDP.prettyPrint();
    	}
    	if(TDP != null) {
	    TDP.prettyPrint();
    	}
    	if(VDP != null) {
	    VDP.prettyPrint();
    	}
    	for(int i = 0; i < PDL.size(); i++) {
	    PDL.get(i).prettyPrint();
    	}
	
    	Main.log.prettyPrintLn("begin"); 
    	Main.log.prettyIndent();
    	if(SL != null) {
	    SL.prettyPrint();
    	}
    	Main.log.prettyOutdent(); 
    	Main.log.prettyPrint("end");
    }
    
    public static Block parse(Scanner s){
	enterParser("Block");//logging purposes
	
	Block b = new Block(s.curLineNum());//the static method creates an object of the block, starting from the initial line it starts at
	
	if(s.curToken.kind == constToken){
	    b.CDP = ConstDeclPart.parse(s);
	}
	
	if(s.curToken.kind == typeToken){
	    b.TDP = TypeDeclPart.parse(s);
	}
	if(s.curToken.kind == varToken){
	    b.VDP = VarDeclPart.parse(s);
	}
	
	
	
	
	while(s.curToken.kind != beginToken){
	    if(s.curToken.kind == functionToken){
		b.FD = FuncDecl.parse(s);
		b.PDL.add(b.FD);
	    }
	    else if(s.curToken.kind == procedureToken){
		b.PD = ProcDecl.parse(s);//correction
		b.PDL.add(b.PD);
		
	    }
	    else{
		Main.error(s.curLineNum(),"expected \"function\", \"procedure\", or \"Begin at start of line");
	    }
	}
	
	s.skip(beginToken);//passes begin
	
	
	b.SL = StatmList.parse(s);
	
	s.skip(endToken);
		
	leaveParser("Block");//logging purposes
	return b;//goes up and returns to the doTestParser method and completes it and completes the compiler
	
    }
    
    void addDecl(String id, PascalDecl d) {//the addDecl method is taken from the lecture notes
	if (decls.containsKey(id)){
	    d.error(id + " declared twice in same block!");
	}			
	decls.put(id, d);
    }
    
    PascalDecl findDecl(String id, PascalSyntax where, boolean ncSearch) {//the findDecl method is partially taken from the lectures, modified with the ability to handle searching for variables that should be namedConstants
	PascalDecl d = decls.get(id);
	if (d != null) {
	    if(ncSearch == false){
		Main.log.noteBinding(id, where, d);
	    }
	    
	    return d;
	}
	if (outerScope != null)
	    return outerScope.findDecl(id,where,ncSearch);		
	where.error("Name " + id + " is unknown!");
	return null;
    }
    
    
    @Override public void check(Block curScope, Library lib) {
	//adding decls to hashmaps
	this.outerScope = curScope;
	
	blockLevel = outerScope.blockLevel + 1;

	if (CDP != null) {
	    //CDP.check(this, lib);
	    for (int i = 0;i< CDP.constList.size();i++) {
		ConstDecl ctmp = CDP.constList.get(i);
		addDecl(ctmp.name.toLowerCase(), ctmp);
	    }
	}
	
	if (CDP != null) {
	    CDP.check(this, lib);
	}
	
	
	
	if (TDP != null) {
	    //TDP.check(this, lib);
	    for (int i = 0;i< TDP.typeList.size();i++) {
		TypeDecl ttmp = TDP.typeList.get(i);
		addDecl(ttmp.name.toLowerCase(), ttmp);
		
		if (ttmp.T instanceof EnumType) {
		    //enum check, does not handle arrays
		    EnumType etmp = (EnumType) ttmp.T;
		    for (int j = 0;j< etmp.enumList.size();j++) {
			EnumLiteral eetmp = etmp.enumList.get(j);
			addDecl(eetmp.name.toLowerCase(), eetmp);
			
		    }		    
		    
		}
		if (ttmp.T instanceof ArrayType){
		    ArrayType atmp = (ArrayType) ttmp.T;
		    
		    atmp.recursiveEnumCheck(this);
		    
		}
	    }
	}	
	
	if (VDP != null) {
	    //VDP.check(this, lib);
	    for (int i = 0;i< VDP.varList.size();i++) {
		VarDecl vtmp = VDP.varList.get(i);
		addDecl(vtmp.name.toLowerCase(), vtmp);
		if(vtmp.T instanceof ArrayType) {
		    ArrayType arrayPointer = (ArrayType) vtmp.T;
		    curOffset += arrayPointer.findSize(this) * 4;
		    System.out.println(arrayPointer.findSize(this));
		}else {
		    curOffset += 4;
		}
		vtmp.offset = curOffset * (-1);
		
		if (vtmp.T instanceof EnumType) {
		    EnumType etmp = (EnumType) vtmp.T;
		    for (int j = 0;j< etmp.enumList.size();j++) {
			EnumLiteral eetmp = etmp.enumList.get(j);
			addDecl(eetmp.name.toLowerCase(), eetmp);
		    }
		    
		}
		
		if (vtmp.T instanceof ArrayType){
		    ArrayType btmp = (ArrayType) vtmp.T;
		    btmp.recursiveEnumCheck(this);
		}
		
	    }
	    
	}
	if (PDL != null) {
	    //PDL.check(this, lib);
	    for (int i = 0;i< PDL.size();i++) {//adds procedure declarations, does NOT add their parameters
		addDecl(PDL.get(i).name.toLowerCase(), PDL.get(i));				
		
		if (PDL.get(i).params != null) {
		    
		    for (int j = 0;j<PDL.get(i).params.paramList.size();j++) {
			ParamDecl paptmp = PDL.get(i).params.paramList.get(j);
			PDL.get(i).block.addDecl(paptmp.name.toLowerCase(), paptmp);//adds parameters to the hashmap of the block the procedure creates before running check on it
			
		    }
		}
	    }
	}
	//here we start with searching the hashmaps
	
	
	if (TDP != null) {
	    TDP.check(this, lib);
	}
	if (VDP != null) {
	    VDP.check(this, lib);
	}
	for(int i = 0; i < PDL.size(); i++) {
	    PDL.get(i).check(this, lib);
	    PDL.get(i).block.check(this, lib);
	}
	
	SL.check(this, lib);
	
    }	
    
    @Override public void genCode(CodeFile f) {	
	f.genInstr("", "enter", "$"+curOffset+",$"+blockLevel, "");
	SL.genCode(f);
    }
}

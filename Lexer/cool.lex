/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    	
    private int curr_lineno = 1;
    private boolean lateUpdate = false;
    
    int get_curr_lineno() {
    	if(!lateUpdate){
    		return curr_lineno;
    	}
    	lateUpdate = false;
    	return (curr_lineno - 1);
    }
    
    // contatore per le parentesi dei commenti multiriga
	private int numpar = 0;

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */


%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yystate()) {
	case COMMENT:
		yybegin(YYINITIAL);
		return new Symbol(TokenConstants.ERROR, "EOF in comment");
	case STRING:
		yybegin(YYINITIAL);
		return new Symbol(TokenConstants.ERROR, "EOF in string constant");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup
%state COMMENT
%state STRING
%state NULL_FINDED
%line

digit 		= [0-9]
lowercase 	= [a-z]
uppercase 	= [A-Z]
letter 		= [a-zA-Z_]
letterdigit = {letter}|{digit}
lineEnd 	= \n|\r\n
str_space	= [ \f\t\v]
whitespace 	= {lineEnd}|{str_space}
IdObj		= {lowercase}{letterdigit}* 
IdType		= {uppercase}{letterdigit}* 
Number		= {digit}+
TRUE 		= t[rR][uU][eE] 
FALSE		= f[aA][lL][sS][eE]
IF 			= [iI][fF]
INHERITS 	= [iI][nN][hH][eE][rR][iI][tT][sS]
LOOP 		= [lL][oO]{2}[pP]
POOL 		= [pP][oO]{2}[lL]
CASE 		= [cC][aA][sS][eE]
ESAC 		= [eE][sS][aA][cC]
NOT 		= [nN][oO][tT]
IN			= [iI][nN]
THEN		= [tT][hH][eE][nN]
ELSE		= [eE][lL][sS][eE]
FI			= [fF][iI]
WHILE		= [wW][hH][iI][lL][eE]
CLASS		= [cC][lL][aA][sS]{2}
LET			= [lL][eE][tT]
OF			= [oO][fF]
NEW			= [nN][eE][wW]
ISVOID		= [iI][sS][vV][oO][iI][dD]
%%

<YYINITIAL>{
	"--".*{lineEnd}	{curr_lineno++;}
	"(*"			{yybegin(COMMENT); numpar = 1;}
	"*)"			{return new Symbol(TokenConstants.ERROR, "Unmatched *)");}
	\"				{string_buf.setLength(0);yybegin(STRING);}
	"=>"			{return new Symbol(TokenConstants.DARROW); }
	"("				{return new Symbol(TokenConstants.LPAREN);}
	";"				{return new Symbol(TokenConstants.SEMI);}
	")"				{return new Symbol(TokenConstants.RPAREN);}
	"<="			{return new Symbol(TokenConstants.LE);}
	"<-"			{return new Symbol(TokenConstants.ASSIGN);}
	"<"				{return new Symbol(TokenConstants.LT);}
	","				{return new Symbol(TokenConstants.COMMA);}	
	"\."			{return new Symbol(TokenConstants.DOT);}
	"="				{return new Symbol(TokenConstants.EQ);}
	":"				{return new Symbol(TokenConstants.COLON);}
	"{"				{return new Symbol(TokenConstants.LBRACE);}
	"}"				{return new Symbol(TokenConstants.RBRACE);}
	"@"				{return new Symbol(TokenConstants.AT);}
	"*"				{return new Symbol(TokenConstants.MULT);}
	"/"				{return new Symbol(TokenConstants.DIV);}
	"+"				{return new Symbol(TokenConstants.PLUS);}
	"~"				{return new Symbol(TokenConstants.NEG);}
	"-"				{return new Symbol(TokenConstants.MINUS);}
	{ELSE}			{return new Symbol(TokenConstants.ELSE);}
	{WHILE}			{return new Symbol(TokenConstants.WHILE);}
	{ESAC}			{return new Symbol(TokenConstants.ESAC);}
	{LET}			{return new Symbol(TokenConstants.LET);}
	{THEN}			{return new Symbol(TokenConstants.THEN);}
	{CLASS}			{return new Symbol(TokenConstants.CLASS);}
	{NOT}			{return new Symbol(TokenConstants.NOT);}
	{IN}			{return new Symbol(TokenConstants.IN);}
	{FI}			{return new Symbol(TokenConstants.FI);}
	{LOOP}			{return new Symbol(TokenConstants.LOOP);}
	{IF}			{return new Symbol(TokenConstants.IF);}
	{OF}			{return new Symbol(TokenConstants.OF);}
	{NEW}			{return new Symbol(TokenConstants.NEW);}
	{ISVOID}		{return new Symbol(TokenConstants.ISVOID);}
	{POOL}			{return new Symbol(TokenConstants.POOL);}
	{CASE}			{return new Symbol(TokenConstants.CASE);}
	{INHERITS}		{return new Symbol(TokenConstants.INHERITS);}
	{TRUE}			{return new Symbol(TokenConstants.BOOL_CONST,"true");}
	{FALSE}			{return new Symbol(TokenConstants.BOOL_CONST,"false");} 
	{Number}		{return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext()));}
	{IdObj}			{return new Symbol(TokenConstants.OBJECTID,	AbstractTable.idtable.addString(yytext()));}
	{IdType}		{return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));}

	{lineEnd}		{curr_lineno++;}
	{whitespace}|\r	{}
	.				{return new Symbol(TokenConstants.ERROR, yytext()); }
}

<COMMENT>{
	"(*"			{numpar++;}
	"*)"			{if(--numpar == 0){yybegin(YYINITIAL);}}
	{lineEnd}		{curr_lineno++;}
	.				{}
}


<STRING>{
	\"					{	
							yybegin(YYINITIAL);
							if (string_buf.length() >= MAX_STR_CONST)
								return new Symbol(TokenConstants.ERROR, "String constant too long");
								
							return new Symbol(TokenConstants.STR_CONST,
							AbstractTable.stringtable.addString(string_buf.toString()));
						}

	\0					{
							yybegin(NULL_FINDED);
							if(string_buf.length() >= MAX_STR_CONST)
								return new Symbol(TokenConstants.ERROR, "String constant too long");
							return new Symbol(TokenConstants.ERROR, "String contains null character.");
						}
	\\\0				{	yybegin(NULL_FINDED);
							return new Symbol(TokenConstants.ERROR, "String contains escaped null character."); 
						}
	\n					{	
							yybegin(YYINITIAL);
							curr_lineno++;
							if (string_buf.length() >= MAX_STR_CONST){
								lateUpdate = true;
								return new Symbol(TokenConstants.ERROR, "String constant too long");
							}
								
							return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
						}
	\\n					{ string_buf.append('\n'); }
	\\t					{ string_buf.append('\t'); }
	\\b					{ string_buf.append('\b'); }
	\\f					{ string_buf.append('\f'); }
	\\\"				{ string_buf.append('\"'); }
	\\\\				{ string_buf.append('\\'); }

	
	\\\n				{curr_lineno++; string_buf.append('\n');}
	
	\\					{}
	{str_space}			{ string_buf.append(yytext()); }
	
	[^\n\r\"\\\0]+		{
							string_buf.append(yytext());
						}
}

<NULL_FINDED>{
	\" 				{ yybegin(YYINITIAL); }
	{lineEnd}		{ yybegin(YYINITIAL); curr_lineno++;}
	.				{}
}
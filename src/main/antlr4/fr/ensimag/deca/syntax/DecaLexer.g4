lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}


// Deca lexer rules.
fragment FILENAME_CHAR : [a-zA-Z0-9] | '_' | '-' | '.';
INCLUDE__DIRECTIVE : '#include' (' ')* '"' (FILENAME_CHAR | '/')+ '"' {
       
       

       doInclude(getText());

} -> skip;

OBRACE : '{' ;
CBRACE : '}' ;
OPARENT : '(';
CPARENT : ')' ;
SEMI : ';' ;
COMMA: ',' ;
DOT : '.' ;

//PRINTLN
PRINTLNX : 'printlnx';
PRINTLN : 'println';
PRINTX : 'printx';
PRINT : 'print' ;



//CLASSES
CLASS : 'class';
EXTENDS : 'extends';
PROTECTED : 'protected';
NEW : 'new';
THIS : 'this';
NULL : 'null';
INSTANCEOF : 'instanceof';
ASM : 'asm';

// COMMENTAIRES
COMMENT : '//' ~[\n\r]* -> skip;
MULTI_COMMENT : '/*' .*? '*/' -> skip;

//STRING

fragment STRING_CAR : ~('"' | '\\' | '\n' | '\r');
// On définit les échappements autorisés (\", \\, \n, \t, \r)
fragment ESC : '\\' ["\\nrt] ;
STRING : '"' (STRING_CAR | ESC)* '"' ;
MULTI_LINE_STRING: '"' (STRING_CAR | '\\"' | '\\\\' | '\n')* '"';


//INT ET FLOAT
fragment NUM : [0-9]+;
fragment DEC : NUM '.' NUM;
fragment SIGN : [+-]?; 
fragment EXP : [eE] SIGN NUM;
fragment FFIN : [fF]?;
fragment FLOATDEC : (DEC | DEC EXP ) FFIN;
fragment DIGITHEX :[0-9a-fA-F];
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX [pP] SIGN NUM FFIN;
FLOAT : FLOATDEC | FLOATHEX;//acceptant 1.
INT : '0' | [1-9] [0-9]* ;
READINT : 'readInt';
READFLOAT : 'readFloat';
  
 


//OPERATEURS
PLUS : '+';
MINUS : '-';
TIMES: '*';
SLASH : '/';
PERCENT : '%' ;

//COMPARAISON
LEQ : '<=';
GEQ : '>=';
GT : '>';
LT : '<' ;

NEQ : '!=';
EQEQ : '==';
EQUALS : '=';
EXCLAM : '!';

//BOOLEAN 
TRUE : 'true';
FALSE : 'false';
//LOGIQUE
AND : '&&' ;
OR : '||';

//LOOP
WHILE : 'while';
//FOR : 'for';
IF : 'if';
ELSE : 'else';
//ELSEIF traité comme ELSE + IF
RETURN : 'return';
//Include section


//IDENTIFIANTS
IDENT : [$_a-zA-Z] [$_a-zA-Z0-9]*;//comme spédifié dans le poly

//WHITE SPACE en fin pour ne pas confondre
WS : [\n\t\r ] -> skip ;//IGNORANT  LES ESPACE

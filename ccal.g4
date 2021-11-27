grammar ccal;

// The language is not case sensitive
fragment A:    [aA];
fragment B:    [bB];
fragment C:    [cC];
fragment D:    [dD];
fragment E:    [eE];
fragment F:    [fF];
fragment G:    [gG];
fragment H:    [hH];
fragment I:    [iI];
fragment J:    [jJ];
fragment K:    [kK];
fragment L:    [lL];
fragment M:    [mM];
fragment N:    [nN];
fragment O:    [oO];
fragment P:    [pP];
fragment Q:    [qQ];
fragment R:    [rR];
fragment S:    [sS];
fragment T:    [tT];
fragment U:    [uU];
fragment V:    [vV];
fragment W:    [wW];
fragment X:    [xX];
fragment Y:    [yY];
fragment Z:    [zZ];

// Reserved words
VAR:     V A R;
CONST:   C O N S T;
RETURN:  R E T U R N;
INTEGER: I N T E G E R;
BOOLEAN: B O O L E A N;
VOID:    V O I D;
MAIN:    M A I N;
IF:      I F;
ELSE:    E L S E;
TRUE:    T R U E;
FALSE:   F A L S E;
WHILE:   W H I L E;
BREAK:    S K I P;

reserved: (
	VAR |
	CONST |
	RETURN |
	type |
	MAIN |
	IF |
	ELSE |
	TRUE |
	FALSE |
	WHILE |
	BREAK
);

// Operators
NOT: '~'; //RL
ADD: '+';
SUB: '-';
LT:  '<';
LEQ: '<=';
GT:  '>';
GEQ: '>=';
EQ:  '==';
NEQ: '!=';
AND: '&&';
OR:  '||';
ASSIGN: '='; //RL

SEP: ',';
TERM: ';';

fragment DIGIT: [0-9];
fragment CHAR: [a-zA-Z];

// Integers are represented by a string of one or more digits (‘0’-‘9’) that
// do not start with the digit ‘0’, but may start with a minus sign (‘-’), e.g.
// 123, -456.
NUMBER: '-'? ( '0' | ('1'..'9' DIGIT*) );

// Identifiers are represented by a string of letters, digits or underscore
// character (‘_’) beginning with a letter. TODO Identifiers cannot be reserved words.
ID: CHAR (CHAR | DIGIT | '_' )*;

// Comments can appear between any two tokens. There are two forms of
// comment: one is delimited by /* and */ and can be nested; the other begins
// with // and is delimited by the end of line and this type of comments may
Line_comment: '//' .*? '\n'? -> skip;
Multi_comment: '/*' ( Multi_comment | . )*? '*/' -> skip;

Whitespace: [ \t\n\r]+ -> skip;

program: decl_list function_list main;
decl_list: decl TERM decl_list?;
decl: var_decl | const_decl;
var_decl: VAR ID':'type;
const_decl: CONST ID':'type ASSIGN expression;
function_list: function function_list?;
function: type ID '('parameter_list')'
'{'
decl_list
statement_block
RETURN '(' expression? ')' TERM
'}';
type: ( INTEGER | BOOLEAN | VOID );
parameter_list: nemp_parameter_list?;
nemp_parameter_list: parameter | ( parameter SEP nemp_parameter_list );
parameter: ID':'type;
main: MAIN '{'
decl_list
statement_block
'}';
statement_block: (statement statement_block)?;
statement:
	ID ASSIGN expression TERM #assignment |
	ID '(' arg_list ')' TERM #function_call |
	'{' statement_block '}' #encapsulated_statements |
	IF condition '{' statement_block '}' ELSE '{' statement_block '}' #branch |
	WHILE condition '{' statement_block '}' #loop |
	BREAK TERM #break;
expression:
	frag binary_arith_op frag |
	ID '(' arg_list ')' |
	frag;
binary_arith_op: ADD | SUB;
frag:
	ID |
	SUB ID |
	NUMBER |
	bool |
	'(' expression ')' |
	'(' frag ')';
bool: TRUE | FALSE;
condition:
	NOT condition |
	'(' condition ')' |
	expression comp_op expression |
	condition ( OR | AND ) condition;
comp_op: EQ | NEQ | LT | LEQ | GT | GEQ;
arg_list: nemp_arg_list?;
nemp_arg_list: ID | ID SEP nemp_arg_list;

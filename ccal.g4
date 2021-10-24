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
fragment VAR:     V A R;
fragment CONST:   C O N S T;
fragment RETURN:  R E T U R N;
fragment INTEGER: I N T E G E R;
fragment BOOLEAN: B O O L E A N;
fragment VOID:    V O I D;
fragment MAIN:    M A I N;
fragment IF:      I F;
fragment ELSE:    E L S E;
fragment TRUE:    T R U E;
fragment FALSE:   F A L S E;
fragment WHILE:   W H I L E;
fragment SKIP:    S K I P;

RESERVED: (
	VAR |
	CONST |
	RETURN |
	type
	MAIN |
	IF |
	ELSE |
	TRUE |
	FALSE |
	WHILE |
	SKIP
);

// Operators
NOT: '~'; //RL
NEG: '-'; //RL
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
TERM: ';'

fragment DIGIT: [0-9];
fragment CHAR: [a-zA-Z];

// Integers are represented by a string of one or more digits (‘0’-‘9’) that
// do not start with the digit ‘0’, but may start with a minus sign (‘-’), e.g.
// 123, -456.
number: '-'? ( '0' | '1'..'9' DIGIT* );

// Identifiers are represented by a string of letters, digits or underscore
// character (‘_’) beginning with a letter. TODO Identifiers cannot be reserved words.
id: CHAR (CHAR | DIGIT | '_' )*;

// Comments can appear between any two tokens. There are two forms of
// comment: one is delimited by /* and */ and can be nested; the other begins
// with // and is delimited by the end of line and this type of comments may
line_comment: '//' .*? '\n'?;
multi_comment: '/*' ( MULTI_COMMENT | . )*? '*/';
comment:  ( LINE_COMMENT | MULTI_COMMENT );

program: decl_list function_list main;
decl_list: decl ';' decl_list?;
decl: var_decl | const_decl;
var_decl: VAR id':'type;
const_decl: CONST id':'type ASSIGN expression;
function_list: function function_list?;
function: type id '('parameter_list')'
'{'
decl_list
statement_block
RETURN '(' expression? ')' ';'
'}';
type: ( INTEGER | BOOLEAN | VOID );
parameter_list: nemp_parameter_list?;
nemp_parameter_list: parameter | ( parameter SEP nemp_parameter_list );
parameter: id':'type;
main: MAIN '{'
decl_list
statement_block
'}';
statement_block: (statement statement_block)?;
statement:
	id '=' expression ';' |
	id '(' arg_list ')' ';' |
	'{' statement_block '}' |
	IF condition '{' statement_block '}' ELSE '{' statement_block '}' |
	WHILE condition '{' statement_block '}' |
	SKIP ';';
expression:
	frag binary_arith_op frag |
	'(' expression ')' |
	id '(' arg_list ')' |
	frag;
binary_arith_op: ADD | SUB;
frag: id | NEG id | number | bool | expression;
bool: TRUE | FALSE;
condition:
	NOT condition |
	'(' condition ')' |
	expression comp_op expression |
	condition ( OR | AND ) condition;
comp_op: EQ | NEQ | LT | LEQ | GT | GEQ;
arg_list: nemp_arg_list?;
nemp_arg_list: id | id SEP nemp_arg_list;

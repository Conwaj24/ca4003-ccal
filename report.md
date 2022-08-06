###### Usage

`make %.ir` will create a new IR file from a `.ccl` file of the same name, and print any semantic errors found to `stdout`

`make` will compile the program and all of its dependencies

`java ccal %.ccl` (once compiled) will read the supplied ccal file, print three-address code to `stdout` and errors to `stderr`, it can also read from `stdin` if not given any arguments.

If, for whatever reason, `make`  is not available on your system, the Makefile may still serve as some form of additional documentation about the usage of the program.

###### Visitor

My implementation has a single visitor which is responsible for building the Symbol Table, printing 3AC, and finding semantic errors. These tasks all share a lot of context, which I will talk more about later, and so the value of having a different visitor for each task wasn't really apparent.

The visitor contains a reference to a Symbol Table, though which symbol table it points to may change depending in the active scope. The visitor methods return a String which generally refers to a symbol's ID or its literal value, or in some cases, its jump label. In other cases the string is left empty because there is nothing meaningful to return. I toyed with using a more sophisticated data type than the string, but the string mostly proved to be all I needed for this application. There were a couple of places, once I started doing type-checking, where the ability to pass something other than a String would have saved me from having to create a bunch of needless symbols and thus needless 3AC but that ultimately doesn't matter for reasons I'll mention later.

Antlr's Visitor supports a combination of top-down and bottom-up tree traversal, however there are a lot of limitations in the kind of data you can send from the top-down: You can control the return type of the visitor methods but not the parameters, which would have been my preferred way to implement Symbol Table scope, however it's entirely possible to define your own visitor methods and invoke them yourself. I ended up doing this for conditions: I decided to implement boolean logic with short-circuiting rather than by splitting the expression into three-address codes like I had done for arithmetic expressions. In order to do this I had to keep track of the relevant labels to jump to depending on the condition, which only seemed to make sense in the form of parameters.

Among the things that never got implemented are operator preference and associativity. It produces correct 3AC with all the inputs I've tested but I'm sure it will start to break down with more complicated arithmetic or boolean expressions except insofar as Antlr has magically figured it out for me, or that I got it right by coincidence. I know Antlr does *some* stuff behind the scenes to handle operator precedence for you but I haven't looked much into it and I doubt I did it right. There are also very likely language features missing simply because they weren't present in any of the example files I was using and so I never noticed its absence. Unfortunately I did not allow myself time to be exhaustive, and hopefully this was not intended to be an excersize in exhaustiveness.

###### Symbol Table

The symbol table provides a set of access methods to a Hash Table of Symbols, which consist of a String value and a Type Signature, which in-turn consists of a type name and optionally a list of parameters in the case of a function. Type Signatures can be compared to check if they match and Symbols can be read and written to (except if they are constants).  Aside from that, a Symbol Table can serve as a node in a Symbol Tree, which it can traverse upwards but not downwards, and is responsible for providing unique names for symbols.

It uses dot-namespacing to name symbols according to their scope; a dot is a good character to use because it doesn't appear in a valid ccgal identifier and it's used to denote scope in many other contexts, such as Java. It uses counters to give incrementing names for labels and temporary variables.

A new symbol table needs to be created for every scope. A scope is anywhere a declaration can occur which, looking at the grammer, is only the top of the program, the top of a function, and the top of main, so not actually enough places to necessitate a recursive symbol table. If I had thought of that sooner I probably would still have made it recursive because it's easier to reason about I think. 

###### Three Address Code

The intermediate representation (three-address code) is generated in one pass, without any lookbacks or lookaheads. Implementing it this way came with some interesting design challenges later for the more complicated instructions, it also gave me far fewer options for optimizing the code; I started to produce more and more redundant 3AC the more of the language I implemented. I could have fixed this by, perhaps, putting some clever declarations on the symbol table that reuse existing variables or being more clever about how I make labels, but that would just be more things that could go wrong, and correctness is really always the highest priority. It also seemed silly to try and optimize an intermediate representation when most of the reasons you would generate such a thing would be to hand it to an optimizer. Adding code to simplify the resulting 3AC would be choosing the simplicity of the 3AC over the simplicity of the compiler.

The 3AC has a close relationship with the symbol table: The symbol table provides the names that appear in the 3AC, it's responsible for determining what an identifier means in a given context and giving the equivalent global name. Part of the reason the 3AC is so verbose is that the implementation of the visitor assumes that every symbol in the symbol table is also declared in the 3AC, and so it needs to be declared in the 3AC for safety. To mitigate this, you would need to keep track of what has and has not been declared in the 3AC with something akin to a symbol table, you see the problem.

###### Semantic Checks

Rather than having a visitor generate semantic checks, they are distributed among the various parts of the program with the specific context required to recognize them.

The symbol table is in charge of mapping IDs to symbols, and knowing what's in scope. It provides the following checks

- ID is in scope

- ID maps to a symbol with a value

The Symbol stores information about type and controls access to data. It provides the following checks.

- When assigned the value of another symbol, that symbol is of matching type

- Symbol is not assigned to if it is a constant

The Visitor traverses the tree, has access to the textual and grammar rules and so can know the type of a literal. It checks that

- The operands of an arithmetic operator resolve into integers

- the operand of the negation operator resolves into an integer

The visitor is also responsible for catching and printing all errors, since only it can provide meaningful information such as line numbers or the actual value of a token as it appears in the text.



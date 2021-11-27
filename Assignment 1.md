My grammar has the same general layout and order as the CCAL document it's based on; I worked top to bottom from the document. The first step was defining case-inksensitive forms of all the letters, then defining case-insensitive reserved words. The `skip` keyword was defined as "BREAK" because "SKIP" is reserved by antlr4, I could have used "Skip" (only first letter is uppercase) instead but this was easier and I don't think it's neccesary for the symbolic name of a lexer rule to directly corespond to what it represents.

The `reserved`rule isn't used for anything. In order to enforce in the lexer that identifiers could not be reserved words, I think I would need an intersection of two rules (e.g.  not reserved `AND` ... ) whereas I could only find information on unions ( not reserved `OR` ... ) which wouldn't have worked. I figured maybe that was out of scope for a lexer and parser.

The lexer rule for `number` was a bit tricky. It is referred to as 'number' and 'integer' interchangably in the spec, or that's my best guess atleast as there doesn't appear to be any definition of `number`. The spec also implies that `0` is an invalid number but `-0` is valid, I decided to make `0` a valid number regardless as I was assured by my classmates that would be the case.

My `Line_comment` lexer rule has an optional newline character at the end, which differs from some of the examples I've seen from past years. I think the newline needs to be optional incase the comment appears at the end of the file, after the final newline.

The parser rules were comparatively very straightforward; more-or-less a direct transcription. There was one bit which was pretty tricky, though: According to the spec, every `expression` is a valid `fragment` and every `fragment` is a valid `expression` which makes the parser unsolvable, and also makes it quite unclear what the logical distinction between a `fragment` and an `expression` is. One of my colleagues suggested wrapping the `expression` in parentheses in the definition of `fragment`. I'm not sure of his rationale for this, but I think doing it this way makes it a lot clearer what the distinction between the two is. A fragment would be something that can be evaluated at any time without loss of information, so literals are valid fragments, and encapsulated expressions are valid fragments. But a bare expression, `2 + 2` for example, can't be evaluated to `4` immediately because `2 + 2 * 3` has a different meaning to `4 * 3`.

The last thing to mentions is the conflict between the `SUB` and `NEG` lexer rules. I asked my classmates how they solved this and all the answers I got were something along the lines of "just make it one rule, they're the same character after all". This doesn't seem right to me, as they're distinct operators with distinct associativity and precedence. The lexical distinction is that the `NEG` operator is unary whereas the `SUB` operator is binary, perhaps I should have tried to reflect that in my lexer rules. On the other hand, perhaps I'm missing the point and the goal of the lexer is to match characters, not semantic meaning.


















































































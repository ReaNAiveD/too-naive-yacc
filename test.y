%token a b c d
%start S
%%
S
    : A a
    | b A c
    | B c
    | b B a
    ;

A
    : d
    ;

B
    : d
    ;
%%
test
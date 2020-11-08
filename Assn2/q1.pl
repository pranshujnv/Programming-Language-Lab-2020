%% Relation and Gender data

parent(jatin,avantika).
parent(jolly,jatin).
parent(jolly,kattappa).
parent(manisha,avantika).
parent(manisha,shivkami).
parent(bahubali,shivkami).
male(kattappa).
male(jolly).
female(shivkami).
female(avantika).
male(bahubali).


%% Rule for relation of grandparent
grandparent(X,Y) :-
	parent(X,Z), parent(Z,Y).
    

%% For X to be an uncle of Y, X must be a male, X can not be a parent of Y &
%% parent of X and grandparent of Y must be same .
uncle(X, Y) :- 	
	male(X), not(parent(X, Y)),
	grandparent(Z,Y), parent(Z,X).
 
	
%% Rule for relation of HalfSister
%% For X to be a half-sister of Y, X must be a female and exactly one of the parent of X is common.
halfsister(X, Y) :-	
	female(X),
	parent(A,X), parent(A,Y),     
	parent(C,X), parent(D,Y),
	A \== C, A \== D, C \== D.
    				
    				




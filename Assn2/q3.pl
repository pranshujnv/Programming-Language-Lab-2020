% Data of road with starting gate , finishing gate and length between them.

road(g1,g5,4).
road(g2,g5,6).
road(g3,g5,8).
road(g4,g5,9).
road(g1,g6,10).
road(g2,g6,9).
road(g3,g6,3).
road(g4,g6,5).
road(g5,g7,3).
road(g5,g10,4).
road(g5,g11,6).
road(g5,g12,7).
road(g5,g6,7).
road(g5,g8,9).
road(g6,g8,2).
road(g6,g12,3).
road(g6,g11,5).
road(g6,g10,9).
road(g6,g7,10).
road(g7,g10,2).
road(g7,g11,5).
road(g7,g12,7).
road(g7,g8,10).
road(g8,g9,3).
road(g8,g12,3).
road(g8,g11,4).
road(g8,g10,8).
road(g10,g15,5).
road(g10,g11,2).
road(g10,g12,5).
road(g11,g15,4).
road(g11,g13,5).
road(g11,g12,4).
road(g12,g13,7).
road(g14,g12,8).
road(g15,g13,3).
road(g13,g14,4).
road(g14,g17,5).
road(g14,g18,4).
road(g17,g18,8).


% first represents gate name for start point of jail and lastone represennts gate name for exit from jail.

first(g1).
first(g2).
first(g3).
first(g4).
lastone(g17).


% It will run dfs and give path without cycle considering given roads are undirected.
% It will check if road exist between first and lastone. If it does not exist it take some gate between the path recursively and try to get path from middle one to lastone.
% While adding first and middle gate to path, it also checks that it is not visited so that cycle is avoided

dfs(Start,End,_,[Start,End],W) :- (road(Start,End,W);road(End,Start,W)).
dfs(Start,End,Path,[Start|R],W1) :- (road(Start,Mid,W);road(Mid,Start,W)),
				\+member(Mid,Path), dfs(Mid,End,[Mid|Path],R,W2),
				\+member(Start,R),W1 is W + W2.
dfs(Start,End,R,W):-dfs(Start,End,[],R,W).


% It will print the path by taking head out recursively from the list and printing it.

printpath1([]) :- !.
printpath1([H|T]) :-  write(" --> "),write(H),printpath1(T).
printpath([H|T]) :- write(H),printpath1(T).


% It will print all path from jail to exit

find_all_possible_path() :-
    findall(_,find_all_possible_path(_,_,_),_).
find_all_possible_path(Start,End,Path) :-
    first(Start),
    lastone(End),
    dfs(Start, End, Path,W),printpath(Path),write("  :: Distance = "),write(W),writeln(" ft").


% This is the recursion to get all path and distance .
% It will check if road exist between first and lastone. If it does not exist it take some gate between the path recursively and try to get path from middle one to lastone.
% The distance of path is distance from first to mid + distance from mid to lastone.
% While adding first to path it checks that it is not visited so cycle is avoided. Roads are considered undirected

get_path(S, E, D, [S,E], _) :- road(S, E, D);road(E, S, D).
get_path(S , E, D, [S|Path], Visited) :- \+ member(S, Visited),
                                 (road(S, Mid, D1);road(Mid, S, D1)),
                                 get_path(Mid, E, D2, Path, [S|Visited]),
                                 D is D1 + D2.


min_dist_path([], M, MinPath, MinPath,M). 					% It is recursion to return shortest path when list is empty.                
min_dist_path( [(D, Path)|Pathlist], MinDistance, _, Output,O) :-		% Recursion to update min path, when current path smaller than min path found.      
    D < MinDistance,min_dist_path(Pathlist, D, Path, Output,O).
min_dist_path([(D, _)|Pathlist], MinDistance, MinPath, Output,O) :-		% Recursion , keep searching for path and when current path is not less than current min
    D >= MinDistance,
    min_dist_path(Pathlist, MinDistance, MinPath, Output,O).


% We will first find all the valid paths  and then getting min from it.

optimal():-optimal1(X),nl,write("Path is "),printpath(X).
optimal1(X) :-
    findall((D,Path),(first(S),lastone(E),get_path(S, E, D, Path, [])), R),
    aggregate_all(sum(D),road(_,_,D),Sum),
    min_dist_path(R, Sum, [], X,Ans),write("Length of the path = "),write(Ans),write(" ft").


% Check if road exist between Gi and Gj

check(_,[]).
check(Gi,[Gj|Gk]) :- road(Gi,Gj,_),check(Gj,Gk);road(Gj,Gi,_),check(Gj,Gk).


% Recursively go to end of list then check each pair that road exist or not
valid([]):-!.
valid([Gi|Path]) :-
    check(Gi,Path).


% Check if road exist and check lastone gate is exit from jail.

check_exit(Gi,[]) :- lastone(Gi).
check_exit(Gi,[Gj|_]) :- road(Gi,Gj,_).
check_exit(Gi,[Gj|_]) :- road(Gj,Gi,_).


% Check if starting gate is jail and then validate all other pair to have a road.

valid_lastonepoints([]).
valid_lastonepoints([H|T]) :-
    first(H),valid_check_exit([H|T]).


% Recursively go to end of list and check lastone to be exit and then check between each pair of path that road exist or not.

valid_check_exit([]).
valid_check_exit([Gi|Path]) :-
    valid_check_exit(Path),
    check_exit(Gi,Path).


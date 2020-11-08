% Bus data with bus no. , start and end stop with arrival time at that stops , distance covered between those stops and fare for that journey.

bus(901,'Adabari','Bharalumukh',15,15.5,3,10).
bus(901,'Bharalumukh','Kachari',15.5,16.2,3,10).
bus(901,'Kachari','Chandmari',16.5,17.5,4,15).
bus(901,'Chandmari','Ganeshguri',17.5,19,5,20).

bus(902,'Jhalukbari','Adabari',14.6,14.7,1,5).
bus(902,'Adabari','Bharamulukh',14.7,15.6,3,8).
bus(902,'Bharamulukh','Uzanbazar',15.7,16.6,10,8).
bus(902,'Uzanbazar','Maligaon',16.7,16.8,1,5).
bus(902,'Maligaon','Paltanbazar',16.8,17.1,2,5).
bus(902,'Paltanbazar','Chandmari',17.1,17.5,2,5).
bus(902,'Chandmari','Ganeshguri',17.5,18.1,4,5).

bus(903,'Amingaon','Jhalukbari',14,14.5,8,10).
bus(903,'Jhalukbari','Maligaon',14.5,15.5,12,30).
bus(903,'Maligaon','Paltanbazar',15.5,15.7,2,10).


% It will find the data between two directly connected points to travel.

data(Source,Destination,Dep,Arr,Dis,Time,Cost,Bus):- bus(Bus,Source,Destination,Dep,Arr,Dis,Cost), Time is Arr-Dep.


% It will find path between two points and correspoding details such as distance, fare, time needed to travel.

dfs(Source,Destination,[Source,Destination],Start,Dis,Time,Cost,[Bus]):-
    (Start=0;Start=1), data(Source,Destination,_,_,Dis,Time,Cost,Bus);
    \+(Start=0;Start=1), data(Source,Destination,Dep,_,Dis,Time,Cost,Bus), Dep >= Start.

% It will find the path between two points which are not directly connected using recursion through 
% intermediate points.

dfs(Source,Destination,[Source|X],_,Dis,Time,Cost,[Bus1|Bus]):-
    data(Source,Mid,_,Arr1,Dis1,Time1,Cost1,Bus1),
    dfs(Mid,Destination,X,Arr1,Dis2,Time2,Cost2,Bus),
    Dis is Dis1 + Dis2, Cost is Cost1 + Cost2, Time is Time1 + Time2.


% It will find all the possible path between given two points and sort the list of list with respect Distance and get the first tuple of the list.

optDis(Source,Source,[Source],0,[],0,0):-!.
optDis(Source,Destination,PathOptDistance,MinDis,BusOptDistance,TimeOptDistance,CostOptDistance):-findall([Dis,Path,Bus,Time,Cost],dfs(Source,Destination,Path,_,Dis,Time,Cost,Bus),ListD),
    sort(ListD,SortedListD),
    SortedListD = [[MinDis,PathOptDistance,BusOptDistance,TimeOptDistance,CostOptDistance]|_].


% It will find all the possible path between given two points and sort the list of list with respect Distance and get the first tuple of the list.

optTime(Source,Source,[Source],0,[],0,0):-!.
optTime(Source,Destination,PathOptTime,MinTime,BusOptTime,DistanceOptTime,CostOptTime):-findall([Time,Path,Bus,Dis,Cost],dfs(Source,Destination,Path,_,Dis,Time,Cost,Bus),ListT),
    sort(ListT,SortedListT),
    SortedListT = [[MinTime,PathOptTime,BusOptTime,DistanceOptTime,CostOptTime]|_].


% It will find all the possible path between given two points and sort the list of list with respect Distance and get the first tuple of the list.

optCost(Source,Source,[Source],0,[],0,0):-!.
optCost(Source,Destination,PathOptCost,MinCost,BusOptCost,DistanceOptCost,TimeOptCost):-findall([Cost,Path,Bus,Dis,Time],dfs(Source,Destination,Path,_,Dis,Time,Cost,Bus),ListC),
    sort(ListC,SortedListC),
    SortedListC = [[MinCost,PathOptCost,BusOptCost,DistanceOptCost,TimeOptCost]|_].


% It will print the path by taking head out recursively from the list and printing it.

printlist([H],[]):-write(H).
printlist([D|L],[B|B2]):-write(D),write(","),write(B),write("--->"),printlist(L,B2).


% It will print the details between two points for the travel corresponding to optimal distance.

printOptDis(Source,Destination,OptDisPath,Distance,BusOptDistance,TimeOptDistance,CostOptDistance):-
    optDis(Source,Destination,OptDisPath,Distance,BusOptDistance,TimeOptDistance,CostOptDistance),
    write("Optimum Distance:"),nl,
    printlist(OptDisPath,BusOptDistance),nl,
    write("Distance: "),write(Distance),
    write(", Time: "),write(TimeOptDistance),
    write(", Cost: "),write(CostOptDistance),nl,nl.


% It will print the details between two points for the travel corresponding to optimal distance.

printOptTime(Source,Destination,OptTimePath,Time,BusOptTime,DistanceOptTime,CostOptTime):-
    optTime(Source,Destination,OptTimePath,Time,BusOptTime,DistanceOptTime,CostOptTime),
    write("Optimum Time:"),nl,
    printlist(OptTimePath,BusOptTime),nl,
    write("Distance: "),write(DistanceOptTime),
    write(", Time: "),write(Time),
    write(", Cost: "),write(CostOptTime),nl,nl.



% It will print the details between two points for the travel corresponding to optimal distance.

printOptCost(Source,Destination,OptCostPath,Cost,BusOptCost,DistanceOptCost,TimeOptCost):-
    optCost(Source,Destination,OptCostPath,Cost,BusOptCost,DistanceOptCost,TimeOptCost),
    write("Optimum Cost:"),nl,
    printlist(OptCostPath,BusOptCost),nl,
    write("Distance: "),write(DistanceOptCost),
    write(", Time: "),write(TimeOptCost),
    write(", Cost: "),write(Cost),nl,nl.


% These are utility functions to print desired details using only source and destination names.

disutil(Source,Destination):-printOptDis(Source,Destination,OptDisPath,Distance,BusOptDistance,TimeOptDistance,CostOptDistance).
costutil(Source,Destination):-printOptCost(Source,Destination,OptCostPath,Cost,BusOptCost,DistanceOptCost,TimeOptCost).
timeutil(Source,Destination):-printOptTime(Source,Destination,OptTimePath,Time,BusOptTime,DistanceOptTime,CostOptTime).


% It takes source and destination as input and print all the desired details.

route(Source,Destination):-
    nl,disutil(Source,Destination),timeutil(Source,Destination),costutil(Source,Destination),!;
    write("No path found for this route").



!!! Important :- Make sure you have installed Swi-prolog beforehand in your system.

Q1.
Run the program in command line by : swipl q1.pl
In this question two types of queries are being performed.
	a). uncle(name1,name2).
	b). halfsister(name1,name2).
	(You can refer to given sample queries in the assignment).

Q2.
Run the program in command line by : swipl q2.pl
Ask the queries using this :- route('Source','Destination').
Sample query : route('Amingaon','Paltanbazar').
               route('Adabari','Ganeshguri').

Q3.
Run the program in command line by : swipl q3.pl
In this question three types of queries are being performed.
	a). find_all_possible_path().   // It will print all the paths starting from jail to exit point with the path length.
	b). optimal().					// It will print the optimal path with its path length.
	c). valid([]).					// Given a path, it will find if it is valid or invalid in terms of escaping the jail.

	Sample query for (c). valid([g1, g6, g8, g9, g8, g7, g10, g15, g13, g14, g18, g17]).
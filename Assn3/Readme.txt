!!! Make sure you have installed haskell in your system.

Q1.
Commands to run:
1. ghci
2. Load and compile the progam by
            :l q1
sample Queries:
    a. empty[] - Output: True    ,   empty[3,4] - Output: False
    b. union [2,4,5,7,8][6,3,5,2]    - Output: [4,7,8,6,3,5,2] 
    c. intersection [4,5,6,7][1,4,5] - Output: [4,5]
    d. subtractuv [4,5,6,7][1,4,5]   - Output: [6,7]
    e. add [4,5,6,7][1,4,5]          - Output: [5,6,9,7,10,8,11,12]



Q2.
Commands to run:
1. ghci
2. Load and compile the progam by
            :l q2
3. To generate a random fixture, type :
            main
Queries:
    a. To get complete fixture, type :
                fixture "all"
    b. To get team specifiic fixture, type :
                fixture "teamname"           i.e. fixture "DS", fixture "MA", etc.
    c. to get next match from input date and time, type :
                nextmatch date time          i.e  nextmatch 1 4.5 , nextmatch 2 16.8 ,etc.
                (for date only give date without month and year and time in 24-hr format. Here 4.5 does not repesent 04:05 a.m. , it shows 04:30 a.m.)




-- funtioon to create union of two set
newset1 [] = []
newset1 set@(u:utemp) 
    | elem u v = v                   ---If u is alreadv there do nothing
    | otherwise = u:v 				 --- if it is not add to the list
    where v = newset1 utemp


--function for emptv set, return true if emptv else false
empty u = null u 

-- function for union of two sets
union u v = newset1(u ++ v)

-- function for intersection of two sets. 
intersect u v = newset1 [e | e <- u,elem e  v]    -- If e is the element of u and also present in v

-- function for subtraction of two set.
subtractuv u v = let temp = intersect u v in [e | e <- u, not (elem e temp )]  --first find the intersection of both set and return all
                                                                               -- those elemnt of u which are not present in intersection.

--function to add two set
add u v = newset1 [a+b|a<-u,b<-v]  -- add each elemnt of u and v and find the union.




module Main where

import System.IO
import Control.Monad (replicateM)
import Data.Function (on)
import Data.List     (sortBy)
import System.Random (randomRIO,randomRs,mkStdGen)
import Control.Applicative((<$>))

teams :: [[Char]]
teams = ["BS","CM","CH","CV","CS","DS","EE","HU","MA","ME","PH","ST"]      -- initial team list

--Function to combine two team list
mergeTeams :: [[Char]] -> [[Char]] -> [[Char]]
mergeTeams [] _ = []
mergeTeams _ [] = []
mergeTeams (x:xtemp) (y:ytemp) = (x ++ " vs " ++ y) : mergeTeams xtemp ytemp

--function to combine team list and time list
mergeTeamWithTime :: [[Char]] -> [[Char]] -> [[Char]]
mergeTeamWithTime [] _ = []
mergeTeamWithTime _ [] = []
mergeTeamWithTime (x:xtemp) (y:ytemp) = (x ++ " " ++ y) : mergeTeamWithTime xtemp ytemp

rearrange :: [a] -> IO [a]
rearrange xtemp = do
  ytemp <- replicateM (length xtemp)  (randomRIO (1 :: Int, 100000))  --generate list of random numbers map them with list of teams sort according to random numbers
  pure $ map fst ( sortBy (compare `on` snd) (zip xtemp ytemp))       -- this will rearrange the team list in random order every time

store :: [String] -> IO ()
store xtemp = do
           writeFile "test.txt" (unlines xtemp)



isValid :: String -> [Char] -> IO ()
isValid x team = do let a = addSpace ' ' x                        -- add space and get first element i.e. team name
                    if team == a!!0                               -- if team in query equals first team in team teams pair
                        then do putStrLn x                        -- display the fixture of that team
                           else if team == a!!2                   -- if team in query equals second team in team teams pair
                              then do putStrLn x                  -- display the fixture of that team
                                else return ()

-- function to print elements of the teams
display :: [String] -> IO ()
display [] = return ()
display (x:xtemp) = do putStrLn x
                       display xtemp



-- function to add space
addSpace :: Eq a => a -> [a] -> [[a]]
addSpace x y = func x y [[]]
    where
        func x [] z = reverse $ map (reverse) z
        func x (y:ytemp) (z:ztemp) = if y==x then
            func x ytemp ([]:(z:ztemp))
        else
            func x ytemp ((y:z):ztemp)


find :: [String] -> [Char] -> IO ()
find [] team = return ()
find (x:xtemp) team = do isValid x team
                         find xtemp team

--function to get fixture of all teams
allMatch :: IO ()
allMatch = do
   content <- readFile "test.txt"                         --read the stored schedule line by line
   let fileLines = lines content
   display fileLines                                      -- display the output

-- function to get fixture of specific team
match :: [Char] -> IO ()
match team = do
   content <- readFile "test.txt"
   let fileLines = lines content
   find fileLines team                                    -- function call for find

fixture :: [Char] -> IO ()
fixture team = do                                         --function to get fixture
   if team == "all"                                       -- if request is to get all team's schedule call allMatch function
     then do allMatch
     else if team `elem` teams                            --find team in teams
             then do match team
             else putStrLn "Please enter a valid team"


-- function to get next match
utilNext:: (Ord a, Fractional a) => String -> [Char] -> a -> IO ()
utilNext x date time = do
                                if time <= 9.5                     -- if time is less than 9.5 than next match will be at 9:30
                                     then do let m = "9:30"
                                             validateDate x date m  -- function call
                                     else if time <= 19.5
                                          then do let m = "7:30"    -- if time is less than 19.5 than next match will be at 7:30
                                                  validateDate x date m
                                          else if time < 24
                                              then do let m = "9:30"     -- if time is between 19.5 and 24 than next match on next date at 9:30
                                                      if date == "1-12-2020"
                                                          then do validateDate x "2-12-2020" m
                                                          else if date == "2-12-2020"
                                                              then do validateDate x "3-12-2020" m
                                                              else return ()
                                              else return ()


-- function call to get match with time and time of argument
validateDate :: String -> [Char] -> [Char] -> IO ()
validateDate x date time = do let a = addSpace ' ' x                    -- add space
                              if date == a!!3                           -- Date matching
                                    then do if time == a!!4             -- Time matching
                                            then putStrLn x
                                            else return ()
                                    else return ()


findNext:: (Ord t, Fractional t) => [String] -> [Char] -> t -> IO ()
findNext [] date time = return ()                                       -- if teams is empty return
findNext (x:xtemp) date time = do utilNext x date time                     
                                  findNext xtemp date time


-- function to get next match after current time
nextmatch:: (Num a, Ord a, Ord t, Fractional t, Show a) => a -> t -> IO ()
nextmatch date time = do
   if date < 1 || date > 31 || time < 0 || time >= 24                   -- check for valid date and time 
         then putStrLn "Please Enter valid date time "
         else if date > 3 || (date == 3 && time > 19.50)                --check if any match left
                  then putStrLn "All Matches are Over."
                  else do content <- readFile "test.txt"
                          let fileLines = lines content
                          let s = show date
                          let b = s ++ "-12-2020"                       -- concatenate month and year with date
                          findNext fileLines b time                     --function call to check the match



main :: IO ()
main = do
  ranl <- rearrange teams                               --  Rearrange order of elements of the teams randomly
  let (list1,list2) = splitAt 6 ranl                    --  Split teams into two equal half
  let list3 = mergeTeams list1 list2                    --  combine both half to make pair of corresponding indexes
  let date = ["1-12-2020 9:30 AM","1-12-2020 7:30 PM","2-12-2020 9:30 AM","2-12-2020 7:30 PM","3-12-2020 9:30 AM","3-12-2020 7:30 PM"]  --teams of available time slots
  bar <- rearrange list3                                --  Rearrange order of elements of the teams randomly
  let list4 = mergeTeamWithTime bar date                --  Merge team pair with date and time
  store list4                                           --  Save final schedule

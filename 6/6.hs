import qualified Data.Vector as V
import Data.List.Split
import Data.Array.IArray
import Data.List

data Point = Point Int Int
data Range = Range Point Point

main = do  
        contents <- readFile "input"

        print $ map readInstruction $ map words $ lines $ contents


readInstruction :: [String] -> String
readInstruction ("toggle" : range : _) = "tgl"
readInstruction ("turn" : "on" : range) = "on"
readInstruction ("turn" : "off" : range : _) = "off"
readInstruction _ = error "Unknown instruction"

readRange :: [String] -> Range
readRange (from : "through" : to : _) = Range (readPoint from) (readPoint to)

readPoint :: String -> Point
readPoint point = makePoint $ map readInt $ splitOn "," point

makePoint :: [Int] -> Point
makePoint (a : b : _) = Point a b

readInt :: String -> Int
readInt = read

foldt            :: (a -> a -> a) -> a -> [a] -> a
foldt f z []     = z
foldt f z [x]    = x
foldt f z xs     = foldt f z (pairs f xs)

pairs xs = zip xs (tail xs)
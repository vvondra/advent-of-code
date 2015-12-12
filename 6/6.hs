import qualified Data.Vector as V
import Data.List.Split
import Data.Array.IArray
import Data.List
import Data.Matrix


data Action = Action (Bool -> Bool)
data Point = Point Int Int
data Range = Range Point Point
data Instruction = Instruction Action Range

main = do
    contents <- readFile "input2"

    print $
        sum $
            map (length . filter (\i -> i)) $
                compose
                    (map executeInstruction $ map readInstruction $ map words $ lines $ contents)
                    (replicate 2 $ replicate 2 True)

executeInstruction :: Instruction -> [[Bool]] -> [[Bool]]

executeInstruction (Instruction (Action action) (Range (Point sx sy) (Point ex ey))) matrix =
    [ if y < sy || y > ey then row else
        [ if x < sx || x > ex then el else action el | (el, x) <- zip row [0..]]
        | (row, y) <- zip matrix [0..]]

readInstruction :: [String] -> Instruction
readInstruction ("toggle" : range) = Instruction (Action (\x -> not x)) (readRange range)
readInstruction ("turn" : "on" : range) = Instruction (Action (\x -> True)) (readRange range)
readInstruction ("turn" : "off" : range) = Instruction (Action (\x -> False)) (readRange range)
readInstruction _ = error "Unknown instruction"

readRange :: [String] -> Range
readRange (from : "through" : to : _) = Range (readPoint from) (readPoint to)

readPoint :: String -> Point
readPoint point = makePoint $ map readInt $ splitOn "," point

makePoint :: [Int] -> Point
makePoint (a : b : _) = Point a b

readInt :: String -> Int
readInt = read

compose :: [a -> a] -> a -> a
compose fs v = foldl (flip (.)) id fs $ v
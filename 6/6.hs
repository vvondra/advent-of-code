import qualified Data.Vector as V
import Data.List.Split
import Data.Array.IArray
import Data.List
import Data.Matrix


data Action = On | Off | Toggle deriving Show
data Point = Point Int Int deriving Show
data Range = Range Point Point deriving Show
data Instruction = Instruction Action Range deriving Show

main = do
    contents <- readFile "input2"

    print $ compose (map executeInstruction $ map readInstruction $ map words $ lines $ contents) [[False,False],[False,False]]

executeInstruction :: Instruction -> [[Bool]] -> [[Bool]]

executeInstruction (Instruction On (Range (Point sx sy) (Point ex ey))) matrix =
    [ if y < sy || y > ey then row else
        [ if x < sx || x > ex then el else True | (el, x) <- zip row [0..]]
        | (row, y) <- zip matrix [0..]]
executeInstruction (Instruction Toggle (Range (Point sx sy) (Point ex ey))) matrix =
    [ if y < sy || y > ey then row else
        [ if x < sx || x > ex then el else not el | (el, x) <- zip row [0..]]
        | (row, y) <- zip matrix [0..]]
executeInstruction (Instruction Off (Range (Point sx sy) (Point ex ey))) matrix =
    [ if y < sy || y > ey then row else
        [ if x < sx || x > ex then el else False | (el, x) <- zip row [0..]]
        | (row, y) <- zip matrix [0..]]

readInstruction :: [String] -> Instruction
readInstruction ("toggle" : range) = Instruction Toggle (readRange range)
readInstruction ("turn" : "on" : range) = Instruction On (readRange range)
readInstruction ("turn" : "off" : range) = Instruction Off (readRange range)
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
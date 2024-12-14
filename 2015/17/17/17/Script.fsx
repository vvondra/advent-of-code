open System
open System.IO

let (+/) path1 path2 = Path.Combine(path1, path2)
let readLines filePath = System.IO.File.ReadLines(filePath)
let capacities = readLines (__SOURCE_DIRECTORY__ +/ "./input.txt") |> Seq.map (string >> int) |> Seq.toList

let rec powerset: int list capacity: int = 
   function
   | [] -> [[]]
   | (x::xs) -> 
      let xss = powerset xs capacity
      (if List.sum xss < capacity then List.map (fun xs' -> x::xs') xss else []) @ xss

subsets capacities 150
     
printfn "%A" capacities
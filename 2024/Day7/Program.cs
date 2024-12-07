var input = File.ReadLines("input.txt")
    .Select(line => line.Split(": "))
    .Select(line => (Total: long.Parse(line[0]), Vals: line[1].Split(' ').Select(long.Parse)));



int Combinations(long total, long acc, IEnumerable<long> vals, bool withConcat = false)
{
    if (acc == total && vals.Count() == 0) {
        return 1;
    }

    if (vals.Count() == 0) {
        return 0;
    }

    if (acc > total) {
        return 0;
    }

    return Combinations(total, acc + vals.First(), vals.Skip(1), withConcat)
     + Combinations(total, acc * vals.First(), vals.Skip(1), withConcat)
     + (withConcat ? Combinations(total, long.Parse($"{acc}{vals.First()}"), vals.Skip(1), withConcat) : 0);
}

var result = input
    .Where(i => Combinations(i.Total, 0, i.Vals) > 0)
    .Sum(i => i.Total);

Console.WriteLine(result);

var result2 = input
    .Where(i => Combinations(i.Total, 0, i.Vals, true) > 0)
    .Sum(i => i.Total);

Console.WriteLine(result2);
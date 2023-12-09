var input = File.ReadLines("input.txt")
    .Select(line => line.Split(" ", StringSplitOptions.TrimEntries).Select(int.Parse).ToArray())
    .ToArray();


IEnumerable<int> Differences(IEnumerable<int> seq) => seq.Zip(seq.Skip(1), (a, b) =>  b - a);
bool AllZero(IEnumerable<int> seq) => seq.All(x => x == 0);
IEnumerable<IEnumerable<int>> Reduce(IEnumerable<int> seq)
{
    var reduced = seq;
    yield return seq;
    while (!AllZero(reduced)) {
        reduced = Differences(reduced);
        yield return reduced;
    }
}

var result = input
    .Select(Reduce)
    .SelectMany(x => x.Select(y => y.Last()))
    .Sum();

Console.WriteLine(result);
var input = File.ReadLines("input.txt")
    .Select(line => line.Split(" ", StringSplitOptions.TrimEntries).Select(int.Parse).ToArray())
    .ToArray();

IEnumerable<int> Differences(IEnumerable<int> seq) => seq.Zip(seq.Skip(1), (a, b) => b - a);
bool AllZero(IEnumerable<int> seq) => seq.All(x => x == 0);
IEnumerable<IEnumerable<int>> Reduce(IEnumerable<int> seq)
{
    var reduced = seq;
    yield return seq;
    while (!AllZero(reduced))
    {
        yield return reduced = Differences(reduced);
    }
}

var result = input
    .Select(Reduce)
    .SelectMany(x => x.Select(y => y.Last()))
    .Sum();

Console.WriteLine(result);

var result2 = input
    .Select(Reduce)
    .Select(x => x
            .Reverse()
            .Select(x => x.First())
            .Aggregate(0, (acc, next) => next - acc))
    .Sum();

Console.WriteLine(result2);
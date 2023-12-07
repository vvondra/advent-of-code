var input = File.ReadAllText("input.txt").Split(Environment.NewLine + Environment.NewLine, StringSplitOptions.TrimEntries).ToArray();

var seeds = input[0].Split(": ")[1].Split(" ", StringSplitOptions.TrimEntries).Select(long.Parse).ToArray();
var seedRanges = seeds.Chunked(2).Select(x => x.ToArray()).ToArray();

var maps = input[1..]
    .Select(block => {
        var split = block.Split(Environment.NewLine);
        var mapping = split[0][..(split[0].Length - 5)].Split("-to-");
        var mappings = split[1..].Select(line => line.Split(" ").Select(long.Parse).ToArray()).ToArray();

        return (mapping: (from: mapping[0], to: mapping[1]), mappings);
    })
    .ToDictionary(x => x.mapping.from, x => x);

long GetLocation(long seed) {
    var current = "seed";
        var num = seed;

        while (current != "location") {
            var mapping = maps[current];

            var range = mapping.mappings.FirstOrDefault(ranges => num >= ranges[1] && num < ranges[1] + ranges[2])
                ?? [1, 1, 1];

            num = num - range[1] + range[0];
            current = mapping.mapping.to;
        }

        return num;
}

var result = seeds.Select(GetLocation).Min();

Console.WriteLine(result);

// I have paid for all of my 12 cores
var result2 = seedRanges
    .SelectMany(x => LongRange(x[0], x[1]))
    .Chunked(int.MaxValue - 1) // lol, PLINQ can only support up to max int32 values
    .Select(x => x.AsParallel().Select(GetLocation).Min())
    .Min();
Console.WriteLine(result2);

// glue code below

IEnumerable<long> LongRange(long start, long count)
{
    if (count < 0)
    {
        throw new ArgumentOutOfRangeException(nameof(count), "Count cannot be negative.");
    }

    long end = start + count;
    for (long i = start; i < end; i++)
    {
        yield return i;
    }
}

public static class EnumerableExtensions
{
    public static IEnumerable<IEnumerable<T>> Chunked<T>(this IEnumerable<T> source, int chunkSize)
    {
        if (chunkSize <= 0)
            throw new ArgumentException("Chunk size must be greater than 0.", nameof(chunkSize));

        using (var enumerator = source.GetEnumerator())
        {
            while (enumerator.MoveNext())
            {
                yield return GetChunk(enumerator, chunkSize);
            }
        }
    }

    private static IEnumerable<T> GetChunk<T>(IEnumerator<T> enumerator, int chunkSize)
    {
        do
        {
            yield return enumerator.Current;
        }
        while (--chunkSize > 0 && enumerator.MoveNext());
    }
}
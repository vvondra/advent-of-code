var input = File.ReadLines("input.txt").Select(long.Parse).ToList();


long mod = 16777216;
long Next(long start) {
    var first = ((start * 64) ^ start) % mod;
    var second = ((long)Math.Truncate(first / 32.0) ^ first) % mod;
    var third = ((second * 2048) ^ second) % mod;

    return third;
}

var result = input.Select(number => {
    for (int i = 0; i < 2000; i++) {
        number = Next(number);
    }
    return number;
}).ToList();

Console.WriteLine(result.Sum());

IEnumerable<((long, long, long, long), long)> GenerateSequence(long seed, int iterations) {
    var sequence = new List<long> { seed };
    for (int i = 0; i < iterations; i++) {
        sequence.Add(Next(sequence.Last()));
    }

    var ones = sequence.Select(x => x % 10).ToList();
    for (int i = 4; i < ones.Count; i++) {
        yield return (
            (
                ones[i - 3] - ones[i - 4],
                ones[i - 2] - ones[i - 3],
                ones[i - 1] - ones[i - 2],
                ones[i] - ones[i - 1]
            ),
            ones[i]
        );
    }
}

var maxes = input.Select(x => GenerateSequence(x, 2000).GroupBy(x => x.Item1).ToDictionary(x => x.Key, x => x.First().Item2)).ToList();

var allKeys = maxes.SelectMany(x => x.Keys).ToHashSet();
var bestKey = allKeys.MaxBy(x => maxes.Select(y => y.GetValueOrDefault(x, 0)).Sum());

Console.WriteLine(maxes.Select(x => x.GetValueOrDefault(bestKey, 0)).Sum());
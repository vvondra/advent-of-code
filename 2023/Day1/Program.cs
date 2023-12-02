var input = File.ReadLines("input.txt");

var result = input
    .Select(x => x.ToCharArray().Where(y => char.IsDigit(y)))
    .Select(x => $"{x.First()}{x.Last()}")
    .Select(int.Parse)
    .Sum();

Console.WriteLine(result);

var replacements = new Dictionary<string, int> {
    {"one", 1 },
    {"two", 2 },
    {"three", 3 },
    {"four", 4 },
    {"five", 5 },
    {"six", 6 },
    {"seven", 7 },
    {"eight", 8 },
    {"nine", 9}
};
var digits = replacements.Keys.Concat(Enumerable.Range(1, 9).Select(x => x.ToString()));

var result2 = input
    .Select(str =>
    {
        var min = digits.Select(digit => (digit, str.IndexOf(digit))).Where(x => x.Item2 > -1).MinBy(x => x.Item2);
        var max = digits.Select(digit => (digit, str.LastIndexOf(digit))).Where(x => x.Item2 > -1).MaxBy(x => x.Item2);

        return replacements.Aggregate($"{min.Item1}{max.Item1}", (acc, replacement)
            => acc.Replace(replacement.Key, replacement.Value.ToString()));

    })
    .Select(int.Parse)
    .Sum();

Console.WriteLine(result2);
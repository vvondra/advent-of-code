var input = File.ReadLines("input.txt");

var instructions = input.First().Trim();

var map = input
    .Skip(2)
    .Select(line =>
    {
        var parts = line.Split(" = ");
        var parts2 = parts[1].Trim([')', '(']).Split(", ");

        return (from: parts[0], to: (left: parts2[0], right: parts2[1]));
    })
    .ToDictionary(x => x.from, x => x.to);

var result = instructions
    .RepeatIndefinitely()
    .Travel("AAA", map)
    .TakeWhile(current => current != "ZZZ")
    .Count() + 1;

Console.WriteLine(result);

var result2 = map.Keys
    .Where(x => x.EndsWith('A'))
    .Select(x => instructions.RepeatIndefinitely().Travel(x, map))
    .Select(x => x.TakeWhile(current => !current.EndsWith('Z')).Count() + 1)
    .ToArray();

Console.WriteLine(LCMN(result2));

long GCD(long a, long b)
{
    while (b != 0)
    {
        long temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

long LCM(long a, long b)
{
    return Math.Abs(a * b) / GCD(a, b);
}

long LCMN(int[] numbers)
{
    return numbers.Aggregate(1L, (x, y) => LCM(x, y));
}

static class EnumerableExtensions
{
    public static IEnumerable<T> RepeatIndefinitely<T>(this IEnumerable<T> source)
    {
        while (true)
        {
            foreach (var item in source)
            {
                yield return item;
            }
        }
    }

    public static IEnumerable<string> Travel(
        this IEnumerable<char> source,
        string from,
        Dictionary<string, (string left, string right)> map
    )
    {
        var current = from;

        using var enumerator = source.GetEnumerator();
        while (enumerator.MoveNext())
        {
            current = enumerator.Current switch
            {
                'L' => map[current].left,
                'R' => map[current].right,
                _ => throw new NotImplementedException()
            };
            //Console.WriteLine($"{enumerator.Current}: {current}");
            yield return current;
        }
    }
}

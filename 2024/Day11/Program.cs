var input = File.ReadAllText("input.txt")
    .Split(" ", StringSplitOptions.TrimEntries | StringSplitOptions.RemoveEmptyEntries)
    .Select(long.Parse);

var memo = new Dictionary<(long, int), long>();

long Expand(long n, int remaining)
{
    if (remaining == 0) {
        return 1;
    }

    if (memo.TryGetValue((n, remaining), out var result))
    {
        return result;
    }

    if (n == 0) {
        result = Expand(1, remaining - 1);
    } else if ((((int) Math.Log10(n)) + 1) % 2 == 0) {
        var rep = n.ToString();
        var leftHalf = long.Parse(rep[..(rep.Length / 2)]);
        var rightHalf = long.Parse(rep[(rep.Length / 2)..]);

        result = Expand(leftHalf, remaining - 1) + Expand(rightHalf, remaining - 1);
    } else {
        result = Expand(n * 2024, remaining - 1);
    }

    memo[(n, remaining)] = result;
    return result;
}

var result = input.Select(n => Expand(n, 25)).Sum();
Console.WriteLine(result);

var result2 = input.Select(n => Expand(n, 75)).Sum();
Console.WriteLine(result2);
var input = File.ReadAllText("input.txt").Split("\n\n");
var towels = input[0].Split(", ", StringSplitOptions.TrimEntries);
var patterns = input[1].Split("\n", StringSplitOptions.TrimEntries);

var root = new TrieNode();

foreach (var towel in towels)
{
    var node = root;
    foreach (var c in towel)
    {
        if (!node.Children.ContainsKey(c))
        {
            node.Children[c] = new TrieNode();
        }
        node = node.Children[c];
    }
    node.IsWordEnd = true;
}

long CountWays(string pattern, int start, Dictionary<int, long> memo)
{
    if (start == pattern.Length)
    {
        return 1;
    }
    if (memo.TryGetValue(start, out var cached))
    {
        return cached;
    }

    long ways = 0;
    var node = root;
    for (int i = start; i < pattern.Length; i++)
    {
        if (!node.Children.TryGetValue(pattern[i], out node))
        {
            break;
        }
        if (node.IsWordEnd)
        {
            ways += CountWays(pattern, i + 1, memo);
        }
    }

    memo[start] = ways;
    return ways;
}

var results = new List<long>();
foreach (var p in patterns)
{
    var memo = new Dictionary<int, long>();
    results.Add(CountWays(p, 0, memo));
}

Console.WriteLine(results.Count(x => x > 0));
Console.WriteLine(results.Sum());

class TrieNode
{
    public Dictionary<char, TrieNode> Children = new();
    public bool IsWordEnd;
}
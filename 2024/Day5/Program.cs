var sections = File.ReadAllText("input.txt").Split("\n\n").ToArray();
var rules = sections[0].Split('\n')
    .Select(l => l.Split("|"))
    .GroupBy(x => x[0])
    .ToDictionary(g => g.Key, g => g.Select(x => x[1]).ToHashSet());
var updates = sections[1].Split('\n').Select(l => l.Split(",").ToArray()).ToArray();

var result = updates
    .Where(CheckUpdate)
    .Select(x => x[x.Length / 2])
    .Select(int.Parse)
    .Sum();

Console.WriteLine(result);

bool CheckUpdate(string[] update)
{
    var seen = new HashSet<string>();

    foreach (var el in update)
    {
        if (rules.ContainsKey(el))
        {
            if (rules[el].Any(seen.Contains))
            {
                return false;
            }
        }

        seen.Add(el);
    }

    return true;
}

var sortedUpdates = updates
    .Where(x => !CheckUpdate(x))
    .Select(update => update.OrderBy(x => x, Comparer<string>.Create(RuleSort)).ToArray())
    .ToArray();

var result2 = sortedUpdates
    .Select(x => x[x.Length / 2])
    .Select(int.Parse)
    .Sum();
Console.WriteLine(result2);

int RuleSort(string a, string b)
{
    if (rules.ContainsKey(a) && rules[a].Contains(b))
    {
        return -1;
    }
    if (rules.ContainsKey(b) && rules[b].Contains(a))
    {
        return 1;
    }
    return 0;
}

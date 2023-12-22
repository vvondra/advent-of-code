var input = File.ReadAllText("input.txt").Split(Environment.NewLine + Environment.NewLine);

var flows = input[0]
    .Split(Environment.NewLine)
    .Select(line =>
    {
        var parts = line.Split('{');
        var from = parts[0].Trim();

        var rawRules = parts[1].Trim('}').Split(',');

        var rules = rawRules.SkipLast(1).Select(rawRule =>
        {
            var rule = rawRule.Split(':');
            var eval = rule[0].Split(['>', '<']);
            return (prop: eval[0][0], lt: rule[0].Contains('<'), value: long.Parse(eval[1]), target: rule[1]);
        }).ToArray();
        var otherwise = rawRules.Last();

        return (from, rules, otherwise);
    })
    .ToDictionary(x => x.from, x => new Rules(x.rules, x.otherwise));

var accepts = flows.Where(flow => flow.Value.rules.All(rule => rule.target == "A") && flow.Value.otherwise == "A").Select(x => x.Key);
var rejects = flows.Where(flow => flow.Value.rules.All(rule => rule.target == "R") && flow.Value.otherwise == "R").Select(x => x.Key);

Dictionary<string, Rules> Reduce(Dictionary<string, Rules> reduced) {
    int prereduced;
    do {
        Console.WriteLine("reducing");
        prereduced = reduced.Values.Sum(x => x.rules.Length);
        reduced = reduced
            .Where(kvp => !accepts.Contains(kvp.Key))
            .Select(kvp => {
                var (from, (rules, otherwise)) = kvp;
                return (
                    from,
                    rules: rules.Select(rule => (rule.prop, rule.lt, rule.value, target: accepts.Contains(rule.target) ? "A" : rule.target)).ToArray(),
                    otherwise: accepts.Contains(otherwise) ? "A" : otherwise
                );
            })
            .ToDictionary(x => x.from, x => new Rules(x.rules, x.otherwise));

        reduced = reduced
            .Where(kvp => !rejects.Contains(kvp.Key))
            .Select(kvp => {
                var (from, (rules, otherwise)) = kvp;
                return (
                    from,
                    rules: rules.Select(rule => (rule.prop, rule.lt, rule.value, target: rejects.Contains(rule.target) ? "R" : rule.target)).ToArray(),
                    otherwise: rejects.Contains(otherwise) ? "R" : otherwise
                );
            })
            .ToDictionary(x => x.from, x => new Rules(x.rules, x.otherwise));

        reduced = reduced
            .Select(kvp => {
                var (from, (rules, otherwise)) = kvp;
                return (
                    from,
                    rules: rules.Where(rule => rule.target != otherwise || otherwise != "A" || otherwise != "R").ToArray(),
                    otherwise
                );
            })
            .ToDictionary(x => x.from, x => new Rules(x.rules, x.otherwise));
    } while(reduced.Values.Sum(x => x.rules.Length) != prereduced);

    return reduced;
}

flows = Reduce(flows);

var parts = input[1]
    .Split(Environment.NewLine)
    .Select(line => line
        .Trim('{', '}')
        .Split(',')
        .Select(pair => pair.Split('='))
        .ToDictionary(pair => pair[0][0], pair => long.Parse(pair[1]))
    );


bool Accept(Dictionary<char, long> part) {
    var current = "in";

    while (true) {
        if (current == "A") {
            return true;
        } else if (current == "R") {
            return false;
        }

        var (rules, otherwise) = flows[current];

        var rule = rules.FirstOrDefault(rule => rule.lt ? part[rule.prop] < rule.value : part[rule.prop] > rule.value);

        if (rule == default) {
            current = otherwise;
        } else {
            current = rule.target;
        }
    }
}

var result = parts.Where(Accept).Sum(x => x.Values.Sum());

Console.WriteLine(result);

Dictionary<char, long[]> breakpoints = flows.Values
    .SelectMany(x => x.rules)
    .Select(x => (x.prop, x.value))
    .GroupBy(x => x.prop)
    .Select(x => (x.Key, new long[] {1}.Concat(x.Select(y => y.value).SelectMany(x => new long [] {x, x + 1})).Append(4001).Distinct().OrderBy(x => x).ToArray()))
    .ToDictionary(x => x.Key, x => x.Item2);

long total = 0;
long count = breakpoints.Aggregate(1, (acc, next) => acc * next.Value.Length);
foreach (var x in breakpoints['x'].Zip(breakpoints['x'][1..])) {
    Console.WriteLine(x);
    foreach (var m in breakpoints['m'].Zip(breakpoints['m'][1..])) {
        Console.WriteLine(m);

        foreach (var a in breakpoints['a'].Zip(breakpoints['a'][1..])) {
            foreach (var s in breakpoints['s'].Zip(breakpoints['s'][1..])) {
                if (count-- % 1_000_000_000 == 0) {
                    Console.WriteLine(count);
                }
                if (Accept(new Dictionary<char, long> {
                    ['x'] = x.First,
                    ['m'] = m.First,
                    ['a'] = a.First,
                    ['s'] = s.First
                })) {
                    total += (x.Second - x.First) * (m.Second - m.First) * (a.Second - a.First) * (s.Second - s.First);
                }
            }
        }
    }
}

Console.WriteLine(total);

internal record struct Rules((char prop, bool lt, long value, string target)[] rules, string otherwise)
{
    public static implicit operator ((char prop, bool lt, long value, string target)[] rules, string otherwise)(Rules value)
    {
        return (value.rules, value.otherwise);
    }

    public static implicit operator Rules(((char prop, bool lt, long value, string target)[] rules, string otherwise) value)
    {
        return new Rules(value.rules, value.otherwise);
    }
}
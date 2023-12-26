Circuit BuildCircuit() {
    var input = File.ReadLines("input.txt")
        .Select<string, (string, IModule)>((x, i) =>
        {
            var parts = x.Split(" -> ");
            var destinations = parts[1].Split(", ").ToList();

            return parts[0][0] switch
            {
                'b' => ("broadcaster", new Broadcaster(destinations)),
                '%' => (parts[0][1..], new FlipFlop(destinations)),
                '&' => (parts[0][1..], new Conjuction(destinations)),
                _ => throw new NotImplementedException(),
            };
        })
        .ToDictionary(x => x.Item1, x => x.Item2);

    return new Circuit(input);
}

var circuit = BuildCircuit();
var (high, low) = (0L, 0L);
for (var i = 0; i < 1000; i++)
{
    var (newHigh, newLow) = circuit.Send(false);
    high += newHigh;
    low += newLow;
}
Console.WriteLine(high * low);

List<string> nodes = ["tr", "xm", "dr", "nh"]; // load the input as a graphviz graph to see why

var times = nodes
    .Select(node => {
        var circuit2 = BuildCircuit();
        return (node, circuit2.TimeToLowPulse(node));
    })
    .ToDictionary(x => x.Item1, x => x.Item2);

Console.WriteLine(LCMN([.. times.Values]));

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

class Circuit
{
    private readonly Dictionary<string, IModule> modules;

    private string? watch;
    private int watchedLow = 0;
    private int watchedHigh = 0;

    public Circuit(Dictionary<string, IModule> modules)
    {
        this.modules = modules;

        foreach (var module in modules.Keys)
        {
            var destinations = modules[module].Connections;
            foreach (var destination in destinations)
            {
                if (modules.ContainsKey(destination) && modules[destination] is Conjuction conjuction) {
                    conjuction.Remember(module);
                }
            }
        }
    }

    public int TimeToLowPulse(string dest) {
        var counter = 0;
        watch = dest;
        watchedLow = 0;
        watchedHigh = 0;

        while (watchedLow == 0) {
            Send(false);
            counter++;
        }

        return counter;
    }

    public (long high, long low) Send(bool input)
    {
        var (high, low) = (0, 0);
        var queue = new Queue<(string, string, bool)>();
        queue.Enqueue(("button", "broadcaster", input));

        while (queue.Count > 0)
        {
            var (from, module, pulse) = queue.Dequeue();
            if (pulse)
            {
                high++;
            }
            else
            {
                low++;
            }

            if (!modules.ContainsKey(module))
            {
                continue;
            }

            if (watch != null) {
                if (module == watch) {
                    if (pulse) {
                        watchedHigh++;
                    } else {
                        watchedLow++;
                    }
                }
            }

            foreach (var (nextModule, nextPulse) in modules[module].Receive(pulse, from))
            {
                //Console.WriteLine($"{module} -{(nextPulse ? "high" : "low")}-> {nextModule}");

                queue.Enqueue((module, nextModule, nextPulse));
            }
        }

        return (high, low);
    }
}

internal interface IModule
{
    IEnumerable<(string, bool)> Receive(bool pulse, string from);

    IEnumerable<string> Connections { get; }
}
internal class Broadcaster(List<string> connectedModules) : IModule
{
    public IEnumerable<string> Connections => connectedModules;

    public IEnumerable<(string, bool)> Receive(bool pulse, string from)
    {
        foreach (var module in Connections)
        {
            yield return (module, pulse);
        }
    }
}

internal class FlipFlop(List<string> connectedModules) : IModule
{
    private bool state = false;

    public IEnumerable<string> Connections => connectedModules;

    public IEnumerable<(string, bool)> Receive(bool pulse, string from)
    {
        if (!pulse)
        {
            state = !state;

            foreach (var module in connectedModules)
            {
                yield return (module, state);
            }
        }
    }
}

internal class Conjuction(List<string> connectedModules) : IModule
{
    public IEnumerable<string> Connections => connectedModules;
    private readonly Dictionary<string, bool> memory = [];

    public void Remember(string module, bool state = false)
    {
        memory[module] = state;
    }

    public IEnumerable<(string, bool)> Receive(bool pulse, string from)
    {
        memory[from] = pulse;

        var allTrue = memory.Values.All(x => x);
        foreach (var module in connectedModules)
        {
            yield return (module, !allTrue);
        }
    }
}

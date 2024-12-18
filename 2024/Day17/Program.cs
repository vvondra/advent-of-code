var input = File.ReadAllText("input.txt").Split("\n\n");

var registers = input[0]
    .Split("\n")
    .Select(line => line.Split(": "))
    .ToDictionary(x => x[0].Last(), x => long.Parse(x[1]));
var ops = input[1].Split(": ")[1].Split(",").Select(long.Parse).ToList();

IEnumerable<long> RunVM(Dictionary<char, long> registers, List<long> ops)
{
    var reg = registers.ToDictionary(x => x.Key, x => x.Value);
    int pc = 0; 

    while (pc < ops.Count)
    {
        long op = ops[pc];
        long literal = ops[pc + 1];
        long combo = literal switch
        {
            < 4 => literal,
            4 => reg['A'],
            5 => reg['B'],
            6 => reg['C'],
            _ => throw new InvalidOperationException($"Unknown operand {literal}")

        };
        switch (op)
        {
            case 0:
                reg['A'] = (long)Math.Truncate(reg['A'] / Math.Pow(2, combo));
                pc += 2;
                break;
            case 1:
                reg['B'] = reg['B'] ^ literal;
                pc += 2;
                break;
            case 2:
                reg['B'] = combo % 8;
                pc += 2;
                break;
            case 3:
                if (reg['A'] != 0)
                {
                    pc = (int)literal;
                }
                else
                {
                    pc += 2;
                }
                break;
            case 4:
                reg['B'] = reg['B'] ^ reg['C'];
                pc += 2;
                break;
            case 5:
                yield return combo % 8;
                pc += 2;
                break;
            case 6:
                reg['B'] = (long)Math.Truncate(reg['A'] / Math.Pow(2, combo));
                pc += 2;
                break;
            case 7:
                reg['C'] = (long)Math.Truncate(reg['A'] / Math.Pow(2, combo));
                pc += 2;
                break;
            default:
                throw new InvalidOperationException($"Unknown operation {op}");
        }
    }
}

Console.WriteLine(string.Join(",", RunVM(registers, ops)));

long TestRun(long A, IEnumerable<long> testedOps, List<long> remainingOps) {
    if (remainingOps.Count == 0) {
        return A;
    }

    var test = remainingOps.First();
    var newTested = testedOps.Concat([test]).ToList();


    for (var i = 0; i < 8; i++) {
        var reg = new Dictionary<char, long> { ['A'] = A * 8 + i, ['B'] = 0, ['C'] = 0 };
        var output = RunVM(reg, ops).Reverse().ToList();

        if (output.SequenceEqual(newTested)) {
            var result = TestRun(A * 8 + i, newTested, remainingOps.Skip(1).ToList());

            if (result != -1) {
                return result;
            }
        }
    }

    return -1;    
}

var result = TestRun(0, [], ops.AsEnumerable().Reverse().ToList());
Console.WriteLine(result);
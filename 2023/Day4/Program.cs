var input = File.ReadLines("input.txt");

var cards = input
    .Select(line => {
        var split = line.Split(": ");
        return(
            card: int.Parse(split[0].Split(" ").Last().Trim()),
            numbers: split[1].Split(" | ").Select(y => y.Split(" ", StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries).Select(int.Parse)).ToArray()
        );
    })
    .ToList();

var result = cards
    .Select(card => card.numbers[0].Where(x => card.numbers[1].Contains(x)).Count())
    .Where(n => n > 0)
    .Select(n => Math.Pow(2, n - 1))
    .Sum();


Console.WriteLine(result);
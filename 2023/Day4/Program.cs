var input = File.ReadLines("input.txt");

var cards = input
    .Select(line => {
        var split = line.Split(": ");
        return(
            card: int.Parse(split[0].Split(" ", StringSplitOptions.TrimEntries).Last()),
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

var result2 = cards
    .Aggregate(cards.ToDictionary(x => x.card, x => 1), (acc, card) => {
        var points = card.numbers[0].Where(x => card.numbers[1].Contains(x)).Count();
        for (var i = 1; i <= points; i++) {
            acc[card.card + i] = acc.GetValueOrDefault(card.card + i) + acc[card.card];
        }

        return acc;
    })
    .Where(kv => kv.Key <= cards.Count)
    .Select(kv => kv.Value)
    .Sum();

Console.WriteLine(result2);
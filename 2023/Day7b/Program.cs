var input = File.ReadLines("input.txt");

var result = input
    .Select(line => line.Split(" "))
    .Select(parts => (hand: new PokerHand(parts[0]), bet: int.Parse(parts[1])))
    .OrderBy(c => c.hand)
    .Select((acc, idx) => (idx + 1) * acc.bet)
    .Sum();

Console.WriteLine(result);


public record Card(char Value) : IComparable<Card>
{
    public int CompareTo(Card other)
    {
        const string order = "J23456789TQKA";
        return order.IndexOf(Value).CompareTo(order.IndexOf(other.Value));
    }

    public override String ToString() => $"{Value}";
}

public record PokerHand(Card[] Cards) : IComparable<PokerHand>
{
    public PokerHand(String chars) : this(chars.Select(c => new Card(c)).ToArray()) { }

    public int Jokers => Cards.Count(c => c.Value == 'J');

    public int[] HighestPairs
    {
        get
        {
            var highest = Cards
                        .Where(c => c.Value != 'J')
                        .GroupBy(card => card.Value)
                        .ToDictionary(group => group.Key, group => group.Count()).Values.ToArray()
                        .OrderByDescending(c => c)
                        .ToArray();
            if (highest.Length == 0) { // all jokers
                return [5];
            }

            highest[0] = highest[0] + Jokers;

            return highest;
        }
    }

    public int CompareTo(PokerHand? other)
    {
        if (HighestPairs[0] != other.HighestPairs[0])
        {
            return HighestPairs[0] - other.HighestPairs[0];
        }
        else
        {
            if (HighestPairs[0] == 3 && other.HighestPairs[0] == 3)
            {
                if (HighestPairs[1] != other.HighestPairs[1])
                {
                    return HighestPairs[1] - other.HighestPairs[1];
                }
            }

            if (HighestPairs[0] == 2 && other.HighestPairs[0] == 2) {
                if (HighestPairs[1] != other.HighestPairs[1])
                {
                    return HighestPairs[1] - other.HighestPairs[1];
                }
            }

            for (int i = 0; i < Cards.Length; i++)
            {
                if (Cards[i] != other.Cards[i])
                {
                    return Cards[i].CompareTo(other.Cards[i]);
                }
            }

            return 0;
        }
    }

    public override string ToString() => string.Join("", (IEnumerable<Card>)Cards);
}

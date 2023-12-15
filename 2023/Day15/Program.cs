var input = File.ReadAllText("input.txt").Split(",", StringSplitOptions.TrimEntries);

int Hash(string s) => s.Aggregate(0, (acc, ch) => (acc + ch) * 17 % 256);

var result = input.Select(Hash).Sum();

Console.WriteLine(result);

var hashmap = input.Aggregate(new Dictionary<int, Lens>(), (acc, next) =>
{
    var split = next.Split(['=', '-']);
    var label = split[0];
    var hash = Hash(label);

    if (next.Contains('='))
    {
        if (acc.TryGetValue(hash, out Lens? lens))
        {
            var last = lens;
            while (last.Next != null && last.Label != label)
            {
                last = last.Next;
            }

            if (last.Label == label)
            {
                last.Focus = int.Parse(split[1]);
            }
            else
            {
                last.Next = new Lens(label, int.Parse(split[1]));
            }
        }
        else
        {
            acc.Add(hash, new Lens(label, int.Parse(split[1])));
        }
    }
    else
    {
        if (acc.TryGetValue(hash, out Lens? lens))
        {
            var last = lens;
            Lens? previous = null;
            while (last.Next != null && last.Label != label)
            {
                previous = last;
                last = last.Next;
            }

            if (last.Label == label)
            {
                if (previous != null)
                {
                    previous.Next = last.Next;
                }
                else if (last.Next == null)
                {
                    acc.Remove(hash);
                }
                else
                {
                    acc[hash] = last.Next;
                }
            }
        }
    }

    return acc;
});

int CalculateScore(Dictionary<int, Lens> hashmap)
{
    int score = 0;
    foreach (var kvp in hashmap)
    {
        int bucketNumber = kvp.Key + 1;
        Lens? node = kvp.Value;
        int index = 1;

        while (node != null)
        {
            score += node.Focus * index * bucketNumber;

            node = node.Next;
            index++;
        }
    }

    return score;
}

int result2 = CalculateScore(hashmap);
Console.WriteLine(result2);


public class Lens
{
    public string Label { get; }

    public int Focus { get; set; }

    public Lens? Next { get; set; }

    public Lens(string label, int focus)
    {
        this.Label = label;
        this.Focus = focus;
    }
}

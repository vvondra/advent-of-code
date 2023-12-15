var input = File.ReadLines("input.txt").Select(line =>
{
    var parts = line.Split(" ");

    return (record: parts[0], nums: parts[1].Split(",").Select(int.Parse).ToArray());
}).ToList();

static long Arrangements(string origTail, int[] nums)
{
    var tail = origTail + 'X';
    var memo = new Dictionary<(char, int, int, int), long>();

    long Arrangements2(char head, int tailIndex, int chomped, ArraySegment<int> nums)
    {
        var key = (head, tailIndex, chomped, nums.Count);
        if (memo.ContainsKey(key))
        {
            return memo[key];
        }

        if (nums.Count == 0 && tail.IndexOf('#', tailIndex) > -1)
        {
            return 0;
        }

        if (nums.Count == 0 && tail.IndexOf('#', tailIndex) == -1)
        {
            return 1;
        }

        var result = head switch
        {
            '?' => Arrangements2('#', tailIndex, chomped, nums)
                    + Arrangements2('.', tailIndex, chomped, nums),
            '.' => chomped switch
            {
                0 => Arrangements2(tail[tailIndex + 1], tailIndex + 1, 0, nums),
                _ when chomped == nums[0] => Arrangements2(tail[tailIndex + 1], tailIndex + 1, 0, nums[1..]),
                _ => 0,
            },
            '#' => chomped switch
            {
                _ when nums.Count == 0 => 0,
                _ when chomped <= nums[0] => Arrangements2(tail[tailIndex + 1], tailIndex + 1, chomped + 1, nums),
                _ => 0,
            },
            'X' => chomped switch
            {
                _ when nums.Count == 0 && chomped == 0 => 1,
                _ when chomped == nums[0] && nums.Count == 1 => 1,
                _ => 0,
            },
            _ => throw new NotImplementedException()
        };

        memo[key] = result;
        return result;
    }

    return Arrangements2(tail[0], 0, 0, nums);
}


var result = input.Select(x => Arrangements(x.record, x.nums)).Sum();
Console.WriteLine(result);

var result2 = input
    .Select(x => (record: string.Join('?', Enumerable.Repeat(x.record, 5)), nums: Enumerable.Repeat(x.nums, 5).SelectMany(x => x).ToArray()))
    .Select(x => Arrangements(x.record, x.nums))
    .Sum();

Console.WriteLine(result2);
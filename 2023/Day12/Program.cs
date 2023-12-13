var input = File.ReadLines("input.txt").Select(line =>
{
    var parts = line.Split(" ");

    return (record: parts[0], nums: parts[1].Split(",").Select(int.Parse).ToArray());
}).ToList();

static long Arrangements(string tail, int[] nums)
{
    long Arrangements2(string tail, int chomped, ArraySegment<int> nums)
    {
        if (nums.Sum() + nums.Count > tail.Length + chomped) {
            return 0;
        }

        if (nums.Count == 0 && tail.Contains('#')) {
            return 0;
        }

        if (nums.Count == 0 && !tail.Contains('#')) {
            return 1;
        }

        return tail[0] switch
        {
            '?' => Arrangements2("#" +tail[1..], chomped, nums)
                    + Arrangements2("." + tail[1..], chomped, nums),
            '.' => chomped switch
            {
                0 => Arrangements2(tail[1..], 0, nums),
                _ when chomped == nums[0] => Arrangements2(tail[1..], 0, nums[1..]),
                _ => 0,
            },
            '#' => chomped switch
            {
                _ when nums.Count == 0 => 0,
                _ when chomped <= nums[0] => Arrangements2(tail[1..], chomped + 1, nums),
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
    }
    return Arrangements2(tail + 'X', 0, nums);
}


var result = input.Select(x => Arrangements(x.record, x.nums)).Sum();
Console.WriteLine(result);

var result2 = input
    .Select(x => (record: string.Join('?', Enumerable.Repeat(x.record, 5)), nums: Enumerable.Repeat(x.nums, 5).SelectMany(x => x).ToArray()))
    .AsParallel().WithDegreeOfParallelism(8).WithExecutionMode(ParallelExecutionMode.ForceParallelism)
    .Select(x => (x, Arrangements(x.record, x.nums)))
    .Select(x => {Console.WriteLine($"{x.Item2,20} {x.Item1.record}"); return x.Item2;})
    .ToList();

    //.Sum();

Console.WriteLine(result2.Sum());
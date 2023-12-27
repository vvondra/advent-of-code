var input = File.ReadLines("input.txt")
    .Select(line =>
    {
        var parts = line.Split('@');
        var coordinates = parts[0].Split(',').Select(long.Parse).ToArray();
        var offsets = parts[1].Split(',').Select(long.Parse).ToArray();
        return new long[][] { coordinates, offsets };
    })
    .ToArray();


var counter = 0;
for (int i = 0; i < input.Length; i++)
{
    for (int j = i + 1; j < input.Length; j++)
    {
        if (WillIntersect(input[i][0], input[i][1], input[j][0], input[j][1], 200000000000000, 400000000000000))
        {
            counter++;
        }
    }
}
Console.WriteLine(counter);

bool WillIntersect(long[] point1, long[] vector1, long[] point2, long[] vector2, long min, long max)
{
    // Calculate the slopes of the two vectors
    double slope1 = (double)vector1[1] / vector1[0];
    double slope2 = (double)vector2[1] / vector2[0];

    if (Math.Abs(slope1 - slope2) < 1e-10)
    {
        return false;
    }

    // Calculate the y-intercepts of the two vectors
    double yIntercept1 = point1[1] - slope1 * point1[0];
    double yIntercept2 = point2[1] - slope2 * point2[0];

    // Calculate the x-coordinate of the intersection point
    double intersectionX = (yIntercept2 - yIntercept1) / (slope1 - slope2);

    // Calculate the y-coordinate of the intersection point
    double intersectionY = slope1 * intersectionX + yIntercept1;

    // Calculate the vectors from the points to the intersection point
    double[] vectorToIntersection1 = [intersectionX - point1[0], intersectionY - point1[1]];
    double[] vectorToIntersection2 = [intersectionX - point2[0], intersectionY - point2[1]];

    // Check if the intersection point is in the direction of the vector from the point
    if (ScalarProduct2d(vector1, vectorToIntersection1) >= 0 && ScalarProduct2d(vector2, vectorToIntersection2) >= 0)
    {

        // Check if the intersection point is within the range of the ray
        if (intersectionY >= min && intersectionY <= max && intersectionX >= min && intersectionX <= max)
        {
            return true;
        }
    }

    // The vectors will not intersect
    return false;
}

double ScalarProduct2d(long[] vector1, double[] vector2)
{
    return vector1[0] * vector2[0] + vector1[1] * vector2[1];
}
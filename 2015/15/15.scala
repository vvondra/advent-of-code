case class Ingredient(name: String, a: Int, b: Int, c: Int, d: Int, e: Int)

val pattern = raw"([A-Za-z]+): capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)".r

val ingredients = io.Source.stdin.getLines
  .map {
    case pattern(name, a, b, c, d, e) => Ingredient(name, a.toInt, b.toInt, c.toInt, d.toInt, e.toInt)
  }
  .toList

def score(ingredients: Traversable[Ingredient])
  = ingredients
    .foldLeft((0, 0, 0, 0))((sum, i)
      => (sum._1 + i.a, sum._2 + i.b, sum._3 + i.c, sum._4 + i.d))
    .productIterator
    .map(_.asInstanceOf[Int])
    .map(Math.max(_, 0))
    .product

val bestRecipe = List.fill(100)(ingredients).flatten.combinations(100)
  .filter(ingredients => ingredients.foldLeft(0)((calories, i) => calories + i.e) == 500)
  .maxBy(score)

println(score(bestRecipe))
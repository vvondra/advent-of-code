import * as fs from "fs";

type Resource = { name: string, count: number };
type Formulas = {
  [key: string]: {
    result: number,
    inputs: Resource[],
  },
};
const input = fs.readFileSync("14.input", "utf-8")
  .split("\n")
  .map(s => s.trim())
  .filter(x => x)
  .map(l => l.split(" => "))
  .map(([input, output]: [string, string]) => {
    const inputs = input.split(", ")
      .map(i => {
        const parts = i.split(" ");
        return { name: parts[1], count: parseInt(parts[0], 10) };
      });
    return [output.split(" "), inputs] as [string[], Resource[]];
  })
  .reduce((formulas, [[outCount, outName], inputs]) => {
    formulas[outName] = { result: parseInt(outCount, 10), inputs };
    return formulas;
  }, {});

const refine = (formulas: Formulas, fuel: number = 1): number => {
  const leftovers = {};
  const needed = {
    FUEL: fuel,
  };
  let ore = 0;

  while (Object.keys(needed).length) {
    const next = Object.keys(needed)[0];

    const prevNeeded = needed[next];
    needed[next] = Math.max(0, needed[next] - (leftovers[next] || 0));
    leftovers[next] = Math.max(0, (leftovers[next] || 0) - prevNeeded);

    if (needed[next] > 0) {
      const times = Math.ceil(needed[next] / formulas[next].result);
      leftovers[next] += (formulas[next].result * times) - (needed[next]);
      formulas[next].inputs.forEach((f: Resource) => {
        if (f.name === "ORE") {
          ore += f.count * times;
        } else {
          needed[f.name] = (needed[f.name] || 0) + (f.count * times);
        }
      });
    }

    delete needed[next];
  }

  return ore;
};

const trillion = 1e12;

const ore = refine(input);
console.log(ore);

const findFuel = (min: number, max: number): number => {
  const mid = Math.floor((min + max) / 2);
  if (mid === max || mid === min) {
    return mid;
  }

  if (refine(input, mid) <= trillion) {
    return findFuel(mid, max);
  } else {
    return findFuel(min, mid);
  }
};

console.log(findFuel(0, 1e9));

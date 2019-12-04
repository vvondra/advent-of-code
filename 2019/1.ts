import * as fs from "fs";
import * as rd from "readline";

const reader = rd.createInterface(fs.createReadStream("1.input"));

const calculateFuel = (n: number) => Math.trunc(n / 3) - 2;
const addFuel = function*(base: number): Generator<number> {
  let last = calculateFuel(base);
  while (last > 0) {
    yield last;
    last = calculateFuel(last);
  }
};

const modules: number[] = [];
reader
  .on("line", (input: string) => {
    modules.push(parseInt(input, 10));
  })
  .on("close", () => {
    const takeoffFuel = modules
      .map(calculateFuel)
      .reduce((sum, n) => sum + n, 0);

    console.log(takeoffFuel);

    const takeoffFuelWithExtra = modules
      .map(calculateFuel)
      .map((fuel) => [fuel, ...addFuel(fuel)])
      .flat()
      .reduce((sum, n) => sum + n, 0);

    console.log(takeoffFuelWithExtra);
  });

import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("19.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const check = async (c: number[]) => {
  return await new Program(input, c).nextVal();
};

const scan = function* () {
  for (let i = 0; i < 50; i++) {
    for (let j = 0; j < 50; j++) {
      yield [j, i];
    }
  }
};

const nextTop = async (c: number[]) => {
  let test = [c[0] + 1, c[1]];
  let pulled = await check(test);
  while (!pulled) {
    test = [test[0], test[1] + 1];
    pulled = await check(test);
  }

  return [test[0], test[1]];
}


const nextBottom = async (c: number[]) => {
  let test = [c[0], c[1] + 1];
  let pulled = await check(test);
  while (!pulled) {
    test = [test[0] + 1, test[1]];
    pulled = await check(test);
  }

  return [test[0], test[1]];
}

(async () => {
  let counter = 0;
  for (const c of scan()) {
    const res = await check(c);
    counter += res;
  }

  console.log(counter);

  // There are some nasty edge cases at the top with an empty/row column
  let topEdge = [3, 5];
  let bottomEdge = [3, 5];

  const size = 99; // the bounds are inclusive, so one less than puzzle def
  let topMoving = true;

  // So we reace the top and bottom edge of the beam against each other
  while (true) {
    if (topMoving) {
      topEdge = await nextTop(topEdge);
    } else {
      bottomEdge = await nextBottom(bottomEdge);
    }

    const vertical = bottomEdge[1] - topEdge[1];
    const horizontal = topEdge[0] - bottomEdge[0];

    if (vertical < size) {
      topMoving = false;
    }
    if (horizontal < size) {
      topMoving = true;
    }

    if (vertical >= size && horizontal >= size) {
      break;
    }
  }

  const min = [Math.min(topEdge[0], bottomEdge[0]), Math.min(topEdge[1], bottomEdge[1])];
  console.log(min[0] * 10000 + min[1]);
})();

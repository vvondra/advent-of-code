import * as fs from "fs";

const input = fs.readFileSync("16.input", "utf-8").trim().split("").map(Number);

const pattern = function* (position: number): Generator<[number, number]> {
  const base = [0, 1, 0, -1];
  let digit = 0;
  let skipped = false;
  let order = 0;
  while (true) {
    if (order > 100) { break ; }
    if (base[digit] === 0) {
      const shift = skipped ? position : position - 1;
      console.log(digit, base[digit], shift, position)
      if (shift > 0) {
        digit = (digit + shift) % base.length;
        order += shift;
        continue;
      }

    }

    for (let i = 0; i < position; i++) {
      if (skipped) {
        yield [order, base[digit]];
        order++;
      } else {
        skipped = true;
      }
    }
    digit = (digit + 1) % base.length;
  }
}

const row = (digits: number[], position: number): number => {
  let digit = 0;
  for (const [i, c] of pattern(position)) {
    if (i === digits.length) {
      break;
    }

    digit += c * digits[i];
  }

  return Math.abs(digit) % 10;
}

const phase = (digits: number[]): number[] => {
  const out = []
  for (let pos = 1; pos <= digits.length; pos++) {
    out.push(row(digits, pos));
  }

  return out;
}

let result = input;
for (let i = 0; i < 4; i++) {
  result = phase(result);
}
console.log(result.slice(0, 8).join(""))

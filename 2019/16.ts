import * as fs from "fs";

const input = fs.readFileSync("16.input", "utf-8").trim().split("").map(Number);

const pattern = function* (position: number): Generator<[number, number]> {
  const base = [1, 0, -1, 0];
  let digit = 0;
  let order = position - 1;
  while (true) {
    for (let i = 0; i < position; i++) {
      yield [order, base[digit]];
      order++;
    }
    digit = (digit + 1) % base.length;
    if (base[digit] === 0) {
      order += position;
      digit = (digit + 1) % base.length;
    }
  }
}

const row = (digits: number[], position: number): number => {
  let digit = 0;
  for (const [i, c] of pattern(position)) {
    if (i >= digits.length) {
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

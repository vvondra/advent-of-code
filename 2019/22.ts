import * as fs from "fs";

const input = fs.readFileSync("22.input", "utf-8")
  .split("\n")
  .map(s => s.trim())
  .filter(x => x);

const seq = (n: number): number[] => Array(n).fill(0).map((_, i) => i);

const apply = (line: string, deck: number[]): number[] => {
  const ins = line.split(" ");
  const param = parseInt(ins.slice(-1)[0], 10);

  if (line.match(/^cut/)) {
    return deck.slice(param).concat(deck.slice(0, param));
  } else if (line.match(/^deal with/)) {
    return deck.reduce((next, n, i) => {
      const index = (i * param) % next.length;
      next[index] = n;
      return next;
    }, seq(deck.length));
  } else if (line.match(/^deal into/)) {
    return deck.reverse();
  }

  throw new Error("whastup");
}

const mod = (a: bigint, b: bigint) => ((a % b) + b) % b;
const shift = (lines: string[], length: bigint, position: bigint) => {
  let moved = position;

  lines.forEach(line => {
    if (line.match(/^cut/)) {
      let param = BigInt(line.split(" ").slice(-1)[0]);
      moved = mod(moved - param, length);
    } else if (line.match(/^deal with/)) {
      const param = BigInt(line.split(" ").slice(-1)[0]);
      moved = (param * moved) % length;
    } else if (line.match(/^deal into/)) {
      moved = length - moved - BigInt(1);
    }
  });

  return moved;
}

const transform = (lines: string[], length: number): number[] => {
  return lines.reduce((deck, next) => {
    const res = apply(next, deck);
    return res;
  }, seq(length));
}

const transform2 = (lines: string[], length: number): number[] => {
  let res = []
  for (let i = 0; i < length; i++) {
    const pos = Number(shift(lines, BigInt(length), BigInt(i)));
    res[pos] = i;
  }
  return res;
};

console.log(transform(input, 10007).findIndex(n => n === 2019));

const orig = BigInt(2020);
const bigDeck = 119315717514047;

let counter = 1;
let position = shift(input, BigInt(119315717514047), BigInt(2020));
while (position != orig) {
  const newPosition = shift(input, BigInt(119315717514047), BigInt(position));
  position = newPosition;
  counter++;

  if (counter % 100000 === 0) {
    console.log(".")
  }
}

console.log(counter);




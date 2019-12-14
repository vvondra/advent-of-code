import * as fs from "fs";

const input = fs.readFileSync("8.input", "utf-8");
const width = 25;
const height = 6;
const layerStrings = input.match(new RegExp(".{" + (width * height) + "}", "g"));

const leastZeros = layerStrings.reduce((prev: [string, number], next: string) => {
  const zeros = next.match(/0/g).length;

  if (zeros < prev[1]) {
    return [next, zeros];
  }

  return prev;
}, ["", Infinity])[0] as string;

console.log(leastZeros.match(/1/g).length * leastZeros.match(/2/g).length);

const layers = layerStrings.map(l => l.split(""));

const firstOpaque = (row: number, col: number): string => {
  const index = row * width + col;
  return layers.find(layer => layer[index] !== "2")[index];
};

const chars = {
  0: " ",
  1: "▒",
  2: "▓",
};

for (let row = 0; row < height; row++) {
  for (let col = 0; col < width; col++) {
    process.stdout.write(chars[firstOpaque(row, col)]);
  }
  process.stdout.write("\n");
}

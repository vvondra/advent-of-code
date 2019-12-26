import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("21.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const toAscii = (s: string) => s.split("").map(s => s.charCodeAt(0));

const walk = async () => {
  const droid = new Program(input);
  const program = [
    "NOT C J", // if third is a hole
    "AND D J", // and 4 is safe, jump
    "NOT A T",
    "OR T J" // jump if next is hole, we'd die anyway
  ];

  const encoded = toAscii(program.concat(["WALK\n"]).join("\n"));

  droid.addInput(...encoded)

  let col = 0;
  let start = 0;
  let floor = false;
  for await (const out of droid.process) {
    if (out > 255) {
      console.log(out);
      break;
    }
    const char = String.fromCharCode(out);
    process.stdout.write(char);
    if (char === "\n") {
      if (floor) {
        process.stdout.write(" ".repeat(start) + "ABCD\n");
        start = 0;
      }
      floor = false;
      col = 0;
    }
    if (char === "#") {
      floor = true;
    }
    if (char === "@") {
      start = col;
    }
    col++;
  }
}

const run = async () => {
  const droid = new Program(input);
  const program = [
    "NOT C J", // if third is a hole
    "NOT F T",
    "OR T J",

    //"NOT I T", //test
    "AND I T",
    "OR H T",
    "AND T J",

    "AND D J", // and 4 is safe, jump
    //"NOT H T", // there's this edge case which forces this jump
    "AND H J", // but it then forces jump into hole at H

    "NOT E T",
    "AND D T",
    "AND H T",
    "AND I T",
    "OR T J",

    "NOT A T",
    "OR T J" // jump if next is hole, we'd die anyway
  ];

  const encoded = toAscii(program.concat(["RUN\n"]).join("\n"));

  droid.addInput(...encoded)

  let col = 0;
  let start = 0;
  let floor = false;
  for await (const out of droid.process) {
    if (out > 255) {
      console.log(out);
      break;
    }
    const char = String.fromCharCode(out);
    process.stdout.write(char);
    if (char === "\n") {
      if (floor) {
        process.stdout.write(" ".repeat(start) + "ABCDEFGHI\n");
        start = 0;
      }
      floor = false;
      col = 0;
    }
    if (char === "#") {
      floor = true;
    }
    if (char === "@") {
      start = col;
    }
    col++;
  }
}

(async () => {
  await walk();
  await run();
})();

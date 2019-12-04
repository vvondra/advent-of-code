const min = 234208;
const max = 765869;

const toDigits = (n: number) => n.toString().split('').map(Number);
const isMonotonic = (digits: number[]) => {
  for (let i = 0; i < digits.length - 1; i++) {
    if (digits[i] > digits[i + 1]) {
      return false;
    }
  }

  return true;
}

const hasSameAdjacent = (digits: number[]) => {
  for (let i = 0; i < digits.length - 1; i++) {
    if (digits[i] == digits[i + 1]) {
      return true;
    }
  }

  return false;
}

const hasTwoSameAdjacent = (digits: number[]) => {
  let prev = digits[0];
  let freq = 1;
  for (let i = 0; i < digits.length - 1; i++) {
    prev = digits[i]
    if (digits[i] == digits[i + 1]) {
      freq++;
    }

    if (digits[i] != digits[i + 1] && freq == 2) {
      return true;
    }

    if (digits[i] != digits[i + 1]) {
      freq = 1;
    }
  }

  if (freq == 2) {
    return true;
  }

  return false;
}

let matching = 0;
let matching2 = 0;
for (let n = min; n <= max; n++) {
  const digits = toDigits(n);

  if (isMonotonic(digits)) {
    if (hasSameAdjacent(digits)) {
      matching++;
    }

    if (hasTwoSameAdjacent(digits)) {
      matching2++;
    }
  }
}


console.log(matching);
console.log(matching2);

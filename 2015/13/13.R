library(gtools)
library(stringr)

con = file("input", "r")

lines <- lapply(readLines(con), function(line) {
    str_match_all(line, "([A-Za-z]+) would (gain|lose) ([:digit:]+) happiness units by sitting next to ([A-Za-z]+)")[[1]][,-1]
})

rules <- lapply(lines, function(list) {
    gain <- as.numeric(list[3])
    if (list[2] == "lose") {
        gain <- gain * -1
    }
    c(from=list[1], gain=gain, to=list[4])
})

lookup <- data.frame(
    from = sapply(rules, function(rule) rule["from"]),
    to = sapply(rules, function(rule) rule["to"]),
    gain = sapply(rules, function(rule) rule["gain"])
)

gain <- function(from, to) {
    as.numeric(as.character(lookup[which(lookup$from==from & lookup$to == to), 'gain']))
}

people <- levels(unique(lookup$from))
seats <- people[-1]

# circular permuations
seatings <- permutations(length(seats), length(seats), seats)
first_person <- matrix(rep(people[1], nrow(seatings)))
all_seatings = cbind(seatings, first_person)

max(apply(all_seatings, 1, function(row) {
    sum(apply(cbind(row, c(row[-1], row[1])), 1, function(pair) { gain(pair[2], pair[1]) + gain(pair[1], pair[2]) } ))
}))

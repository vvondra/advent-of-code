BEGIN {
    TIME=2503
}
{
    NAMES[NR]=$1
    STATS[$1,"SPEED"]=$4
    STATS[$1,"DURATION"]=$7
    STATS[$1,"PAUSE"]=$14
}
END {

    ELAPSED=0

    while (ELAPSED <= TIME) {

        for (no in NAMES) {
            print STATS[NAMES[no],"SPEED"]
        }

        ELAPSED=ELAPSED+1
    }

    for (no in NAMES) {
        print STATS[NAMES[no],"SPEED"]
    }

}

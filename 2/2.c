#include<stdio.h>

#define MIN(x, y) (((x) < (y)) ? (x) : (y))

int main()
{
    printf("Elf Paper Packer\n");

    FILE *fp;
    int w, h, l;
    int sa, sb, sc;
    int buffer, needed;
    long needed_total = 0;

    fp = fopen("input", "r");

    while (fscanf(fp,"%dx%dx%d", &w, &h, &l) == 3) {
        printf("%d %d %d\n", w, h, l);

        sa = w * h;
        sb = h * l;
        sc = w * l;

        buffer = MIN(sa, MIN(sb, sc));

        needed = 2 * sa + 2 * sb + 2 * sc + buffer;
        needed_total += needed;

        printf("Needed: %d\n", needed);

    }

    printf("Needed total: %ld\n", needed_total);

    fclose(fp);
}
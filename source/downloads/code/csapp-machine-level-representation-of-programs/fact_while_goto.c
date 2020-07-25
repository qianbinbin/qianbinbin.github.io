long fact_while_goto(long n) {
    long result = 1;
loop:
    if (n <= 1)
        goto done;
    result *= n;
    n = n - 1;
    goto loop;
done:
    return result;
}

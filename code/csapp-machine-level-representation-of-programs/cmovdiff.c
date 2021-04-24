long cmovdiff(long x, long y) {
    long eval = y - x;
    long rval = x - y;
    long ntest = x < y;
    if (ntest)
        rval = eval;
    return rval;
}

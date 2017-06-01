# neo_loop_performance
Testing loop performance


Last Run
--

    Benchmark                              (friendsCount)  (userCount)   Mode  Cnt       Score       Error  Units
    LoopBenchmark.cacheEverything                     100        10000  thrpt   50  177199.270 ± 8617.445  ops/s
    LoopBenchmark.cachedUsernames                     100        10000  thrpt   50   92746.310 ±  3428.030  ops/s
    LoopBenchmark.collectRels                         100        10000  thrpt   50   49542.602 ±  1852.141  ops/s
    LoopBenchmark.iterateNormally                     100        10000  thrpt   50   49684.784 ±  2030.379  ops/s
    LoopBenchmark.justLongsNodeId                     100        10000  thrpt   50  225987.409 ±  9650.679  ops/s
    LoopBenchmark.justLongsbyEndNodeGetId             100        10000  thrpt   50  103944.622 ±  7572.533  ops/s
    LoopBenchmark.justLongsbyEndNodeId                100        10000  thrpt   50  124742.440 ±  6328.344  ops/s
    LoopBenchmark.justLongscachedUserId               100        10000  thrpt   50  216917.105 ± 13893.118  ops/s
    LoopBenchmark.sortNodeIds                         100        10000  thrpt   50   42755.311 ±  1783.655  ops/s
    LoopBenchmark.sortNodes                           100        10000  thrpt   50   49269.666 ±  1842.202  ops/s
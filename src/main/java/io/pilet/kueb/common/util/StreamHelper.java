package io.pilet.kueb.common.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class StreamHelper {

    public static IntStream intStream(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue);
    }

    public static LongStream longStream(List<Long> list) {
        return list.stream().mapToLong(Long::longValue);
    }
}

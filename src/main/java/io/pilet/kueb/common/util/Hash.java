package io.pilet.kueb.common.util;


import org.apache.commons.codec.digest.DigestUtils;

public class Hash {

    public static String of(Object... args) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<args.length; i++)
            builder.append(args[i]);
        return DigestUtils.sha256Hex(builder.toString().getBytes());
    }

    public static String idOf(String prefix, int len, Object... args) {
        return prefix + of(args).substring(0,len);
    }
    public static String idOf(int len, Object... args) {
        return of(args).substring(0,len);
    }

    public static String ofBytes(int len, byte[] bytes) {
        return DigestUtils.sha256Hex(bytes).substring(0, len);
    }

}

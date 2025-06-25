package io.pilet.kueb.common.data;

public record Filter(
        String field,
        Operation operation,
        String value
) {
    public String toSQL() {
        return operation.format(this);
    }

    public static Filter entity(String name) {
        return new Filter("entity", Operation.EQ, name);
    }
    public static Filter eq(String field, String value) {
        return new Filter(field, Operation.EQ, value);
    }

    public static Filter neq(String field, String value) {
        return new Filter(field, Operation.NEQ, value);
    }

    public static Filter lt(String field, String value) {
        return new Filter(field, Operation.LT, value);
    }

    public static Filter gt(String field, String value) {
        return new Filter(field, Operation.GT, value);
    }

    public static Filter like(String field, String value) {
        return new Filter(field, Operation.LIKE, value);
    }

}

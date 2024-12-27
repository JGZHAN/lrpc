package cn.jgzhan.lrpc.common.dto;

import java.util.Objects;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class Pair<A, B> {

    public final A left;
    public final B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "Pair[" + left + "," + right + "]";
    }

    public boolean equals(Object other) {
        return other instanceof Pair<?,?> pair &&
                Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    public int hashCode() {
        if (left == null) return (right == null) ? 0 : right.hashCode() + 1;
        else if (right == null) return left.hashCode() + 2;
        else return left.hashCode() * 17 + right.hashCode();
    }

    public static <A,B> Pair<A,B> of(A a, B b) {
        return new Pair<>(a,b);
    }
}

package obee;

import blake.Digest.Blake256;

/**
 * Created by Obee on 23/08/15.
 */
public class test {
    public static void main(String[] args) {
        Blake256 b = new Blake256();
        System.out.println(b.digest("leon123".getBytes()));
    }
}

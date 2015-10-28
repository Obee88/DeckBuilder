package obee;

import blake.Digest.Blake256;
import suport.TappedOut.TODeck;
import suport.TappedOut.TOParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by Obee on 23/08/15.
 */
public class test {
    public static void main(String[] args) throws IOException {


        TOParser to = TOParser.getInstance();
        TODeck deck = to.getDeck("bwg-radnaverzija");
        System.out.println();
    }


    ///////////////////////////////////////////////
    public void digestPassword(String p){
        Blake256 b = new Blake256();
        System.out.println(b.digest(p.getBytes()));
    }
}

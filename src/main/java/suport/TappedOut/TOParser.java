package suport.TappedOut;


import com.mongodb.util.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Obee on 28/09/15.
 */
public class TOParser implements Serializable {

    private String baseUrl;
    private Object page;

    public static TOParser instance = null;

    private TOParser(){}

    public static TOParser getInstance(){
        if (instance==null)
            instance = new TOParser();
        return instance;
    }

    public TODeck getDeck(String deckName) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        TODeck deck = new TODeck(deckName);

        try {
            HttpPost httppost = new HttpPost("http://tappedout.net/api/deck/widget/");
            List<NameValuePair> params = new ArrayList<NameValuePair>(5);
            params.add(new BasicNameValuePair("deck", deckName));
            params.add(new BasicNameValuePair("c", "type"));
            params.add(new BasicNameValuePair("side", null));
            params.add(new BasicNameValuePair("cols", "6"));
            params.add(new BasicNameValuePair("board", null));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    String str = EntityUtils.toString(entity);
                    HashMap<String,String> o = (HashMap<String,String>) JSON.parse(str);
                    String html = o.get("board");
                    Document board = Jsoup.parse(html);
                    Elements cols = board.select(".tappedout-board-col");
                    for (Element col : cols ){
                        Elements GroupNames = col.select("h3");
                        Elements groups = col.select("ul");
                        for (int i=0; i< GroupNames.size();i++){
                            String groupName = GroupNames.get(i).text().split(" ")[0];
                            for (Element card : groups.get(i).select("li")) {
                                int num = Integer.parseInt(card.childNode(0).toString().split("x")[0].trim());
                                Element a = card.select("a").first();
                                String cardName = a.text();
                                String url = a.attr("data-card-img");
                                TOCard tcrd = new TOCard(cardName,url,groupName, num);
                                deck.add(tcrd);
                            }
                        }

                    }
                    System.out.println(str);

                } finally {
                    instream.close();
                }
            }
            System.out.println();
        }catch (Exception ex) {
            System.out.println();
        } finally {
            httpClient.getConnectionManager().shutdown(); //Deprecated
        }
        return deck;
    }

    public TODeck getDeck(String deckName, File f) throws IOException {
        List<String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
        TODeck deck = new TODeck(deckName);
        String category = "";
        for (String line : lines){
            if (line.startsWith("-"))
                category = line.toLowerCase().substring(1);
            else{
                String[] parts = line.split("x ");
                Integer num = Integer.parseInt(parts[0]);
                String name = parts[1];
                TOCard c = new TOCard(name,"http://i.imgur.com/x7wSe5y.gif",category,num);
                deck.add(c);
            }
        }
        return deck;
    }

}

package suport.TappedOut;

import java.io.Serializable;

/**
 * Created by Obee on 28/09/15.
 */
public class TOCard implements Serializable {
    private int quantity;
    private String cardName, url, category;

    public TOCard(String cardName, String url, String category, int quantity){
        this.cardName = cardName;
        this.url = url;
        this.category = category;
        this.quantity=quantity;
    }

    public String getName(){
        return this.cardName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (!(obj instanceof TOCard))
            return false;
        TOCard tcrd = (TOCard)obj;
        return tcrd.getName().toLowerCase().equals(this.getName().toLowerCase());
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "{"+this.getName()+"}";
    }

    public String getUrl() {
        return url;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}

package obee.pages;

import custom.classes.Card;
import custom.classes.CardGenerator;
import custom.classes.User;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.HashMap;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StorePage extends MasterPage {
    private static final long serialVersionUID = 1L;
    private Form form;
    private HashMap<String, Integer> prices;
    private Label commonPriceLbl,uncommonPriceLbl,rarePriceLbl,mythicPriceLbl;
    private HiddenField<String> commonCnt,uncommonCnt,rareCnt,mythicCnt;

    private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(StorePage.class, "StorePage.js");

    @SuppressWarnings("serial")
    public StorePage(final PageParameters params) {
        super(params, "Admin");
        initLists();
        initForms();
        initComponents();
//        initBehaviours();
    }

    private void initComponents() {
        commonPriceLbl = new Label("commonPriceLbl",prices.get("common").toString());
        add(commonPriceLbl);
        uncommonPriceLbl = new Label("uncommonPriceLbl",prices.get("uncommon").toString());
        add(uncommonPriceLbl);
        rarePriceLbl = new Label("rarePriceLbl",prices.get("rare").toString());
        add(rarePriceLbl);
        mythicPriceLbl = new Label("mythicPriceLbl",prices.get("mythic").toString());
        add(mythicPriceLbl);
        commonCnt = new HiddenField<String>("commonCnt",new Model<String>("0"));
        form.add(commonCnt);
        uncommonCnt = new HiddenField<String>("uncommonCnt",new Model<String>("0"));
        form.add(uncommonCnt);
        rareCnt = new HiddenField<String>("rareCnt",new Model<String>("0"));
        form.add(rareCnt);
        mythicCnt = new HiddenField<String>("mythicCnt",new Model<String>("0"));
        form.add(mythicCnt);
    }

    private void initForms() {
        form = new Form("buyForm"){
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                super.onSubmit();
                int commonCount = Integer.parseInt(commonCnt.getDefaultModelObjectAsString());
                int uncommonCount = Integer.parseInt(uncommonCnt.getDefaultModelObjectAsString());
                int rareCount = Integer.parseInt(rareCnt.getDefaultModelObjectAsString());
                int mythicCount = Integer.parseInt(mythicCnt.getDefaultModelObjectAsString());
                int cardsAdded = 0;
                User u = mongo.getUser(userName);
                for (int i = 0 ; i<mythicCount;i++){
                    String rarity = "mythic";
                    int price = prices.get(rarity);
                    if (u.getJadBalance()>=price){
                        u.decreaseJadBalance(price);
                        Card c = CardGenerator.generateOneCard(u.getUserName(), rarity);
                        u.addToBooster(c.getCardId());
                        cardsAdded++;
                    }
                }
                for (int i = 0 ; i<rareCount;i++){
                    String rarity = "rare";
                    int price = prices.get(rarity);
                    if (u.getJadBalance()>=price){
                        u.decreaseJadBalance(price);
                        Card c =CardGenerator.generateOneCard(u.getUserName(),rarity);
                        u.addToBooster(c.getCardId());
                        cardsAdded++;
                    }
                }
                for (int i = 0 ; i<uncommonCount;i++){
                    String rarity = "uncommon";
                    int price = prices.get(rarity);
                    if (u.getJadBalance()>=price){
                        u.decreaseJadBalance(price);
                        Card c =CardGenerator.generateOneCard(u.getUserName(),rarity);
                        u.addToBooster(c.getCardId());
                        cardsAdded++;
                    }
                }
                for (int i = 0 ; i<commonCount;i++){
                    String rarity = "common";
                    int price = prices.get(rarity);
                    if (u.getJadBalance()>=price){
                        u.decreaseJadBalance(price);
                        Card c =CardGenerator.generateOneCard(u.getUserName(),rarity);
                        u.addToBooster(c.getCardId());
                        cardsAdded++;
                    }
                }
                u.UPDATE();
                String msg = cardsAdded+" cards added to booster!";
                setResponsePage(StorePage.class,new PageParameters().add("infoMsg",msg));
            }
        };
        add(form);
    }

    private void initLists() {
        prices = new HashMap<String, Integer>();
        prices.put("common",6);
        prices.put("uncommon",20);
        prices.put("rare",50);
        prices.put("mythic",130);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
    }

}

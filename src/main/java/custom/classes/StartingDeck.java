package custom.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import suport.StarterDeckGenerator;
import database.MongoHandler;

@SuppressWarnings("serial")
public class StartingDeck implements Serializable {

	private static List<String> deckNames = Arrays.asList(new String[]{"Armada","Bomber","Decay","Infestation","Way Wild"});
	MongoHandler mongo = MongoHandler.getInstance();
	public String name, Link;
	
	public StartingDeck(String name) {
		this.name = name;
		this.Link = "http://www.wizards.com/magic/tcg/productarticle.aspx?x=mtg_tcg_seventh_themedeck#deck"
		+(deckNames.indexOf(name)+1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IModel<? extends List<? extends StartingDeck>> getDecks() {
		List<StartingDeck> list = new ArrayList<StartingDeck>();
		for(String name: deckNames)
			list.add(new StartingDeck(name));
		return new Model((Serializable) list);
	}

	public void dodajSe(String userName) {
		User u = mongo.getUser(userName);
		List<Card> deck = StarterDeckGenerator.generate(name, userName);
		u.addToBooster(deck);
		u.starterDeck= name;
		u.UPDATE();
	}
	public String getLink() {
		return Link;
	}

	@Override
	public String toString() {
		return name;
	}
}

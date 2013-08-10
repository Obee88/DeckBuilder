package suport;

import java.util.ArrayList;
import java.util.List;

import custom.classes.Card;

public class StarterDeckGenerator {

	public static List<Card> generate(String name, String userName) {
		if(name.toLowerCase().equals("armada"))
			return getArmada(userName);
		if(name.toLowerCase().equals("decay"))
			return getDecay(userName);
		if(name.toLowerCase().equals("bomber"))
			return getBomber(userName);
		if(name.toLowerCase().equals("infestation"))
			return getInfestation(userName);
		if(name.toLowerCase().equals("way wild"))
			return getWayWild(userName);
		return null;
	}

	private static List<Card> getWayWild(String userName) {
		List<Card> ret = new ArrayList<Card>();
		Card c;
		c =Card.generateFromCardName("Llanowar Elves", userName);
		ret.add(c);
		c =Card.generateFromCardName("Llanowar Elves", userName);
		ret.add(c);
		c =Card.generateFromCardName("Grizzly Bears", userName);
		ret.add(c);
		c =Card.generateFromCardName("Grizzly Bears", userName);
		ret.add(c);
		c =Card.generateFromCardName("Grizzly Bears", userName);
		ret.add(c);
		c =Card.generateFromCardName("Fyndhorn Elder", userName);
		ret.add(c);
		c =Card.generateFromCardName("Wood Elves", userName);
		ret.add(c);
		c =Card.generateFromCardName("Trained Armodon", userName);
		ret.add(c);
		c =Card.generateFromCardName("Trained Armodon", userName);
		ret.add(c);
		c =Card.generateFromCardName("Giant Spider", userName);
		ret.add(c);
		c =Card.generateFromCardName("Giant Spider", userName);
		ret.add(c);
		c =Card.generateFromCardName("Gorilla Chieftain", userName);
		ret.add(c);
		c =Card.generateFromCardName("Redwood Treefolk", userName);
		ret.add(c);
		c =Card.generateFromCardName("Spined Wurm", userName);
		ret.add(c);
		c =Card.generateFromCardName("Pride of Lions", userName);
		ret.add(c);
		c =Card.generateFromCardName("Ancient Silverback", userName);
		ret.add(c);
		c =Card.generateFromCardName("Thorn Elemental", userName);
		ret.add(c);
		c =Card.generateFromCardName("Giant Growth", userName);
		ret.add(c);
		c =Card.generateFromCardName("Wild Growth", userName);
		ret.add(c);
		c =Card.generateFromCardName("Regeneration", userName);
		ret.add(c);
		c =Card.generateFromCardName("Blanchwood Armor", userName);
		ret.add(c);
		c =Card.generateFromCardName("Lure", userName);
		ret.add(c);
		c =Card.generateFromCardName("Creeping Mold", userName);
		ret.add(c);
		c =Card.generateFromCardName("Creeping Mold", userName);
		ret.add(c);
		c =Card.generateFromCardName("Stream of Life", userName);
		ret.add(c);
		c =Card.generateFromCardName("Rod of Ruin", userName);
		ret.add(c);
		return ret;
	}

	private static List<Card> getInfestation(String userName) {
		List<Card> ret = new ArrayList<Card>();
		Card c;
		c =Card.generateFromCardName("Goblin Digging Team", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Digging Team", userName);
		ret.add(c);
		c =Card.generateFromCardName("Raging Goblin", userName);
		ret.add(c);
		c =Card.generateFromCardName("Raging Goblin", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Glider", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Raider", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Raider", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Chariot", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Chariot", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Matron", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin King", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Gardener", userName);
		ret.add(c);
		c =Card.generateFromCardName("Goblin Gardener", userName);
		ret.add(c);
		c =Card.generateFromCardName("Fire Elemental", userName);
		ret.add(c);
		c =Card.generateFromCardName("Trained Orgg", userName);
		ret.add(c);
		c =Card.generateFromCardName("Patagia Golem", userName);
		ret.add(c);
		c =Card.generateFromCardName("Shock", userName);
		ret.add(c);
		c =Card.generateFromCardName("Shock", userName);
		ret.add(c);
		c =Card.generateFromCardName("Spitting Earth", userName);
		ret.add(c);
		c =Card.generateFromCardName("Spitting Earth", userName);
		ret.add(c);
		c =Card.generateFromCardName("Pillage", userName);
		ret.add(c);
		c =Card.generateFromCardName("Pillage", userName);
		ret.add(c);
		c =Card.generateFromCardName("Lightning Blast", userName);
		ret.add(c);
		c =Card.generateFromCardName("Blaze", userName);
		ret.add(c);
		return ret;
	}

	private static List<Card> getBomber(String userName) {
		List<Card> ret = new ArrayList<Card>();
		Card c;
		c =Card.generateFromCardName("Merfolk of the Pearl Trident", userName);
		ret.add(c);
		c =Card.generateFromCardName("Coral Merfolk", userName);
		ret.add(c);
		c =Card.generateFromCardName("Merfolk Looter", userName);
		ret.add(c);
		c =Card.generateFromCardName("Sage Owl", userName);
		ret.add(c);
		c =Card.generateFromCardName("Glacial Wall", userName);
		ret.add(c);
		c =Card.generateFromCardName("Horned Turtle", userName);
		ret.add(c);
		c =Card.generateFromCardName("Prodigal Sorcerer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Wind Drake", userName);
		ret.add(c);
		c =Card.generateFromCardName("Daring Apprentice", userName);
		ret.add(c);
		c =Card.generateFromCardName("Wall of Air", userName);
		ret.add(c);
		c =Card.generateFromCardName("Wind Drake", userName);
		ret.add(c);
		c =Card.generateFromCardName("Fighting Drake", userName);
		ret.add(c);
		c =Card.generateFromCardName("Thieving Magpie", userName);
		ret.add(c);
		c =Card.generateFromCardName("Air Elemental", userName);
		ret.add(c);
		c =Card.generateFromCardName("Force Spike", userName);
		ret.add(c);
		c =Card.generateFromCardName("Unsummon", userName);
		ret.add(c);
		c =Card.generateFromCardName("Boomerang", userName);
		ret.add(c);
		c =Card.generateFromCardName("Boomerang", userName);
		ret.add(c);
		c =Card.generateFromCardName("Counterspell", userName);
		ret.add(c);
		c =Card.generateFromCardName("Counterspell", userName);
		ret.add(c);
		c =Card.generateFromCardName("Inspiration", userName);
		ret.add(c);
		c =Card.generateFromCardName("Ancestral Memories", userName);
		ret.add(c);
		c =Card.generateFromCardName("Confiscate", userName);
		ret.add(c);
		return ret;
	}

	private static List<Card> getDecay(String userName) {
		List<Card> ret = new ArrayList<Card>();
		Card c;
		c =Card.generateFromCardName("Blood Pet", userName);
		ret.add(c);
		c =Card.generateFromCardName("Blood Pet", userName);
		ret.add(c);
		c =Card.generateFromCardName("Bog Imp", userName);
		ret.add(c);
		c =Card.generateFromCardName("Drudge Skeletons", userName);
		ret.add(c);
		c =Card.generateFromCardName("Drudge Skeletons", userName);
		ret.add(c);
		c =Card.generateFromCardName("Foul Imp", userName);
		ret.add(c);
		c =Card.generateFromCardName("Crypt Rats", userName);
		ret.add(c);
		c =Card.generateFromCardName("Looming Shade", userName);
		ret.add(c);
		c =Card.generateFromCardName("Serpent Warrior", userName);
		ret.add(c);
		c =Card.generateFromCardName("Gravedigger", userName);
		ret.add(c);
		c =Card.generateFromCardName("Gravedigger", userName);
		ret.add(c);
		c =Card.generateFromCardName("Abyssal Specter", userName);
		ret.add(c);
		c =Card.generateFromCardName("Fallen Angel", userName);
		ret.add(c);
		c =Card.generateFromCardName("Dakmor Lancer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Phyrexian Hulk", userName);
		ret.add(c);
		c =Card.generateFromCardName("Duress", userName);
		ret.add(c);
		c =Card.generateFromCardName("Ostracize", userName);
		ret.add(c);
		c =Card.generateFromCardName("Dark Banishing", userName);
		ret.add(c);
		c =Card.generateFromCardName("Dark Banishing", userName);
		ret.add(c);
		c =Card.generateFromCardName("Mind Rot", userName);
		ret.add(c);
		c =Card.generateFromCardName("Greed", userName);
		ret.add(c);
		c =Card.generateFromCardName("Befoul", userName);
		ret.add(c);
		c =Card.generateFromCardName("Corrupt", userName);
		ret.add(c);
		c =Card.generateFromCardName("Charcoal Diamond", userName);
		ret.add(c);
		return ret;
	}

	private static List<Card> getArmada(String userName) {
		List<Card> ret = new ArrayList<Card>();
		Card c;
		c =Card.generateFromCardName("Eager Cadet", userName);
		ret.add(c);
		c =Card.generateFromCardName("Eager Cadet", userName);
		ret.add(c);
		c =Card.generateFromCardName("Angelic Page", userName);
		ret.add(c);
		c =Card.generateFromCardName("Crossbow Infantry", userName);
		ret.add(c);
		c =Card.generateFromCardName("Knight Errant", userName);
		ret.add(c);
		c =Card.generateFromCardName("Knight Errant", userName);
		ret.add(c);
		c =Card.generateFromCardName("Samite Healer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Samite Healer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Longbow Archer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Longbow Archer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Standing Troops", userName);
		ret.add(c);
		c =Card.generateFromCardName("Standing Troops", userName);
		ret.add(c);
		c =Card.generateFromCardName("Heavy Ballista", userName);
		ret.add(c);
		c =Card.generateFromCardName("Razorfoot Griffin", userName);
		ret.add(c);
		c =Card.generateFromCardName("Serra Advocate", userName);
		ret.add(c);
		c =Card.generateFromCardName("Master Healer", userName);
		ret.add(c);
		c =Card.generateFromCardName("Healing Salve", userName);
		ret.add(c);
		c =Card.generateFromCardName("Healing Salve", userName);
		ret.add(c);
		c =Card.generateFromCardName("Spirit Link", userName);
		ret.add(c);
		c =Card.generateFromCardName("Disenchant", userName);
		ret.add(c);
		c =Card.generateFromCardName("Pacifism", userName);
		ret.add(c);
		c =Card.generateFromCardName("Pacifism", userName);
		ret.add(c);
		c =Card.generateFromCardName("Glorious Anthem", userName);
		ret.add(c);
		c =Card.generateFromCardName("Serra's Embrace", userName);
		ret.add(c);
		c =Card.generateFromCardName("Serra's Embrace", userName);
		ret.add(c);
		return ret;
	}

}

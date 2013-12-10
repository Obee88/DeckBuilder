package database;

import com.mongodb.*;

import custom.classes.Card;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.classes.UserMessage;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {

	private static MongoHandler mongo = MongoHandler.getInstance();
	public static void init() {
		User u = new User("Admin", "admin@gmail.com", "admin");
		u.addRole("ADMIN");
		u.addRole("PRINTER");
		u.UPDATE();
		mongo.adminCollection.insert(new BasicDBObject("q","q"));
		System.out.println("updated");
		
	}
	
	public static void test(){

	}
    static Mongo loc, outer;
    public static void main(String[] args) throws UnknownHostException {
        loc = new MongoClient("localhost",27017);
        outer = new MongoClient(new MongoClientURI("mongodb://obi:obiosam@paulo.mongohq.com:10046/Magic"));
//        transferUsers();
//        transferCards();
//        transferAdministration();
//        transferCardInfos();
//        checkInfos();
//        checkStatuses();
//        setMyWishlist();
//        addInfos();
        System.out.println("DONE!");

        System.out.println();
    }

    private static void addInfos() {
        DBCursor cCur = loc.getDB("Magic").getCollection("cards").find();
        while(cCur.hasNext()){
            DBObject obj = cCur.next();
            Object infoId = obj.get("cardInfoId");
            loc.getDB("Magic").getCollection("cards").update(new BasicDBObject(),
                    new BasicDBObject("$set",new BasicDBObject("info",
                            loc.getDB("Magic").getCollection("cardInfo").findOne(
                                    new BasicDBObject("id",infoId)
                            ))))                                  ;
        }
    }

    private static void setMyWishlist() {
        DBCollection usersCol = loc.getDB("Magic").getCollection("users");
        String[] list = new String[]{"Vivid Creek",
                "Steam Vents",
                "Cascade Bluffs",
                "Sulfur Falls",
                "Shadowblood Ridge",
                "Tresserhorn Sinks",
                "Urborg Volcano",
                "Consecrated Sphinx",
                "Sturmgeist",
                "Tandem Lookout",
                "Badlands",
                "Lava Tubes",
                "Rocky Tar Pit",
                "Auntie's Hovel",
                "Lavaclaw Reaches",
                "Akoum Refuge",
                "Dragonskull Summit",
                "Terramorphic Expanse",
                "Isperia, Supreme Judge",
                "Niv-Mizzet, Dracogenius",
                "Ardent Plea",
                "Cathedral of War",
                "Battlegrace Angel",
                "Sovereigns of Lost Alara",
                "Sublime Archangel",
                "Psychatog",
                "Solemn Simulacrum",
                "Grave Titan",
                "Jace, Memory Adept",
                "Darkslick Shores",
                "Drowned Catacomb",
                "Sword of Feast and Famine",
                "Sundering Titan",
                "Crucible of Worlds",
                "Engineered Explosives",
                "Engineered Plague",
                "Gifts Ungiven",
                "Adventuring Gear",
                "Blightsteel Colossus",
                "Deathrender",
                "Ancient Ziggurat",
                "Curiosity",
                "Animate Dead",
                "Krenko, Mob Boss",
                "Toxic Nim",
                "Crackleburr",
                "Hateflayer",
                "Vengevine",
                "Kargan Dragonlord",
                "Vexing Devil",
                "Dimir Infiltrator",
                "Civilized Scholar",
                "Erayo, Soratami Ascendant",
                "Arcanis the Omnipotent",
                "Mistvein Borderpost",
                "Traumatize",
                "Skaab Ruinator",
                "Demon's Herald",
                "Empyrial Archangel",
                "The Unspeakable",
                "Llanowar Sentinel",
                "Scion of Darkness",
                "Godsire",
                "Sphinx Sovereign",
                "Mothdust Changeling",
                "Skeletal Changeling",
                "Shapesharer",
                "Gelectrode",
                "Sigil of Sleep",
                "Paranoid Delusions",
                "Last Thoughts",
                "Call of the Nightwing",
                "Undercity Plague",
                "Shadow Slice",
                "Stolen Identity",
                "Hands of Binding",
                "Yore-Tiller Nephilim",
                "Ophidian Eye",
                "Wee Dragonauts",
                "Vapor Snag",
                "Isochron Scepter",
                "Guttersnipe",
                "Niv-Mizzet, the Firemind",
                "Razorfin Hunter",
                "Cerulean Wisps",
                "Crimson Wisps",
                "Gitaxian Probe",
                "Basilisk Collar",
                "Clout of the Dominus",
                "Quicksilver Dagger",
                "Kiln Fiend",
                "Chandra's Spitfire",
                "Distortion Strike",
                "Goblin Sharpshooter",
                "Manamorphose",
                "War Elemental",
                "Charmbreaker Devils",
                "Staggershock",
                "Virulent Swipe",
                "Mana Leak",
                "Hypersonic Dragon",
                "Blood Tyrant",
                "Azami, Lady of Scrolls",
                "Nicol Bolas, Planeswalker",
                "Nicol Bolas",
                "Wrexial, the Risen Deep",
                "Darkwater Catacombs",
                "Salt Marsh",
                "Basilica Screecher",
                "Pristine Talisman",
                "Cinder Pyromancer",
                "Quest for Renewal",
                "Ancestral Recall",
                "Blightning",
                "Terminate",
                "Mox Jet",
                "Delver of Secrets",
                "Jace's Phantasm",
                "Glen Elendra Liege",
                "Beseech the Queen",
                "Entomb",
                "Intuition",
                "Terror",
                "Kederekt Creeper",
                "Grixis Grimblade",
                "Deviant Glee",
                "Fists of the Demigod",
                "Veinfire Borderpost",
                "Evolving Wilds",
                "Deathcult Rogue",
                "Mask of Riddles",
                "Noggle Bandit",
                "Kathari Remnant",
                "Thraximundar",
                "Defiler of Souls",
                "Kederekt Parasite",
                "Nightscape Familiar",
                "Sedraxis Specter",
                "Unscythe, Killer of Kings",
                "Tablet of the Guilds",
                "Dimir Cutpurse",
                "Vivid Marsh",
                "Scuttlemutt",
                "Goblin Electromancer",
                "Cryptoplasm",
                "Royal Assassin",
                "Infiltrator's Magemark",
                "Terrain Generator",
                "Vampiric Link",
                "Bribery",
                "Prince of Thralls",
                "Tidespout Tyrant",
                "Helm of the Ghastlord",
                "Moonveil Dragon",
                "Fire-Field Ogre",
                "Shrewd Hatchling",
                "Wasp Lancer",
                "Gravelgill Duo",
                "Tidehollow Strix",
                "Architects of Will",
                "Phyresis",
                "Phyrexian Arena",
                "Phyrexian Vatmother",
                "Elgaud Shieldmate",
                "Strip Mine",
                "Tolarian Academy",
                "Library of Alexandria",
                "Mishra's Factory",
                "Thawing Glaciers",
                "Onslaught",
                "Polluted Delta",
                "Bloodstained Mire",
                "Grixis Panorama",
                "Grixis Battlemage",
                "Grixis Illusionist",
                "Grixis Charm",
                "Wasteland",
                "City of Brass",
                "Maze of Ith",
                "Kjeldoran Outpost",
                "Mox Pearl",
                "Mox Emerald",
                "Mox Diamond",
                "Mox Opal",
                "Mox Sapphire",
                "Mox Ruby",
                "Sleight of Hand",
                "Sleight of Mind",
                "Elesh Norn, Grand Cenobite",
                "Malfegor",
                "Silverblade Paladin",
                "Escape Artist",
                "Stormscape Apprentice",
                "Phyrexian Crusader",
                "Wingcrafter",
                "Teleportal",
                "Flaming Sword",
                "Kamahl's Desire",
                "Robe of Mirrors",
                "Rally the Horde",
                "Manaforge Cinder",
                "Capricious Efreet",
                "Ponder",
                "Flame Slash",
                "Springleaf Drum",
                "Homura, Human Ascendant",
                "Homura's Essence",
                "Resilient Wanderer"};
        BasicDBList dbl = new BasicDBList();
        dbl.addAll(Arrays.asList(list));
        usersCol.update(new BasicDBObject("userName","Obee"),new BasicDBObject("$set",new BasicDBObject("wishList",dbl)));

    }

    private static void removeCardLists() {
        DBCollection usersCol = loc.getDB("Magic").getCollection("users");
        usersCol.update(new BasicDBObject(),new BasicDBObject("$unset" , new BasicDBObject("userCards" , 1 )),true,true);
    }

    private static void checkStatuses() {
        DBCollection usersCol = outer.getDB("Magic").getCollection("users");
        DBCollection cardsCol = outer.getDB("Magic").getCollection("cards");
        DBCursor usersCur = usersCol.find();
        while (usersCur.hasNext()){
            int cnt=0;
            DBObject usrObj = usersCur.next();
            System.out.println(usrObj.get("userName"));
            DBObject usrCards = (DBObject) usrObj.get("userCards");
            BasicDBList bl = (BasicDBList) usrCards.get("boosters");
            for(Object id : bl){
                cardsCol.update(new BasicDBObject("id",id),new BasicDBObject("$set",new BasicDBObject("status","booster")));
                System.out.println(cnt++);
            }
            BasicDBList ul = (BasicDBList) usrCards.get("using");
            for(Object id : ul){
                cardsCol.update(new BasicDBObject("id",id),new BasicDBObject("$set",new BasicDBObject("status","using")));
                System.out.println(cnt++);
            }
            BasicDBList tl = (BasicDBList) usrCards.get("trading");
            for(Object id : tl){
                cardsCol.update(new BasicDBObject("id",id),new BasicDBObject("$set",new BasicDBObject("status","trading")));
                System.out.println(cnt++);
            }
        }
    }

    static boolean write = true;

    private static void checkInfos() {
        String colName = "cards";
        DBCollection in = loc.getDB("Magic").getCollection(colName);
        DBCollection out = outer.getDB("Magic").getCollection(colName);
//        System.out.println(out.remove(new BasicDBObject()));
        DBCursor inCur = out.find();

        if(write){ while (inCur.hasNext()){
            DBObject inObj = inCur.next();
            if(inObj.get("info")==null){
                BasicDBObject outObj = new BasicDBObject();
                outObj.append("id",inObj.get("id"))
                        .append("printed", inObj.get("printed"))
                        .append("owner", inObj.get("owner"))
                        .append("cardInfoId",inObj.get("cardInfoId"))
                        .append("creationDate",inObj.get("creationDate"))
                        .append("status", inObj.get("status"))
                        .append("inProposal", inObj.get("inProposal"))
                        .append("info", loc.getDB("Magic").getCollection("cardInfo").findOne(new BasicDBObject("id", inObj.get("cardInfoId"))))
                ;
                out.update(inObj,outObj);
            }
        } }

    }


    private static void transferUsers() {
        String colName = "users";
        DBCollection in = loc.getDB("Magic").getCollection(colName);
        DBCollection out = loc.getDB("Magic2").getCollection(colName);
        System.out.println(out.remove(new BasicDBObject()));
        DBCursor inCur = in.find();
        List<DBObject> users = new ArrayList<DBObject>();

        if(write){ while (inCur.hasNext()){
            DBObject inObj = inCur.next();
//            BasicDBObject outObj = new BasicDBObject();
//            BasicDBList userCards = new BasicDBList();
//            userCards.addAll((BasicDBList) ((DBObject) inObj.get("userCards")).get("boosters"));
//            userCards.addAll((BasicDBList) ((DBObject) inObj.get("userCards")).get("using"));
//            userCards.addAll((BasicDBList) ((DBObject) inObj.get("userCards")).get("trading"));
//            outObj.append("eMail",inObj.get("eMail"))
//                    .append("facebookName",inObj.get("facebookName"))
//                    .append("lastBoosterDate",inObj.get("lastBoosterDate"))
//                    .append("passwordHash",inObj.get("passwordHash"))
//                    .append("roles",inObj.get("roles"))
//                    .append("starterDeck",inObj.get("starterDeck"))
//                    .append("userCards",userCards)
//                    .append("userName",inObj.get("userName"))
//                    .append("wantsProposalMail", inObj.get("wantsProposalMail"))
//                    .append("wantsWishlistMail", inObj.get("wantsWishlistMail"))
//                    .append("wishList", inObj.get("wishList"));
//            users.add(outObj);
            users.add(inObj);
            System.out.println(".");
        }
            System.out.println(out.insert(users));  }
    }

    private static void transferCards() {
        String colName = "cards";
        DBCollection in = loc.getDB("Magic").getCollection(colName);
        DBCollection out = outer.getDB("Magic").getCollection(colName);
        System.out.println(out.remove(new BasicDBObject()));
        DBCursor inCur = in.find();
        List<DBObject> cards = new ArrayList<DBObject>();
        if(write) {while (inCur.hasNext()){
            DBObject inObj = inCur.next();
            BasicDBObject outObj = new BasicDBObject();
            outObj.append("id",inObj.get("id"))
                    .append("printed", inObj.get("printed"))
                    .append("owner", inObj.get("owner"))
                    .append("cardInfoId",inObj.get("cardInfoId"))
                    .append("creationDate",inObj.get("creationDate"))
                    .append("status", inObj.get("status"))
                    .append("inProposal", inObj.get("inProposal"))
                    .append("info", loc.getDB("Magic").getCollection("cardInfo").findOne(new BasicDBObject("id", inObj.get("cardInfoId"))))
            ;
            cards.add(outObj);
            System.out.println(".");
        }
            System.out.println(out.insert(cards));    }
    }

    private static void transferAdministration() {
        String colName = "administration";
        DBCollection in = loc.getDB("Magic").getCollection(colName);
        DBCollection out = outer.getDB("Magic").getCollection(colName);
        System.out.println(out.remove(new BasicDBObject()));
        DBCursor inCur = in.find();
        if(write){ while (inCur.hasNext()){
            DBObject inObj = inCur.next();
//            BasicDBObject outObj = new BasicDBObject();
//            BasicDBList userCards = new BasicDBList();
//            outObj.append("authList",inObj.get("authList"))
//                    .append("maxCardId",inObj.get("maxCardId"))
//                    .append("maxTradeProposalId",inObj.get("maxTradeProposalId"))
//                    .append("printList",inObj.get("printList"))
//                    .append("tradingProposals",inObj.get("tradingProposals"));
//            out.insert(outObj,WriteConcern.NORMAL);
            out.insert(inObj);
        }                                              }
    }

    private static void transferCardInfos() {
        String colName = "cardInfo";
        DBCollection in = loc.getDB("Magic").getCollection(colName);
        DBCollection out = outer.getDB("Magic").getCollection(colName);
        System.out.println(out.remove(new BasicDBObject()));
        DBCursor inCur = in.find();
        List<DBObject> cards = new ArrayList<DBObject>();
        if(write){ while (inCur.hasNext()){
            DBObject inObj = inCur.next();
            cards.add(inObj);
            System.out.println(".");
        }
            System.out.println(out.insert(cards));}
    }

}

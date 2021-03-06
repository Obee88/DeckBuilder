package custom.classes;

import database.MongoHandler;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class ShowingCard implements Serializable,Comparable<ShowingCard> {
	public Integer cardId,cardInfoId;
	public CardInfo cardInfo;
	public String name;
	public String printed, owner,status;
	public String inProposal;
    private Date creationDate;
	private List<String> interestList = null;
	private String interestString;

	public ShowingCard() {
	}
	
	public ShowingCard(Card c) {
		cardId = c.cardId;
		cardInfoId = c.cardInfoId;
        cardInfo = c.getCardInfo();
		name = cardInfo.name;
		printed= c.printed;
		owner=c.owner;
		status = c.status;
		inProposal = c.inProposal;
        creationDate = c.creationDate;
	}
	
	@Override
	public String toString(){
		return cardInfo.name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCardId() {
		return cardId;
	}
	
	public String getId(){
		return new Integer(getCardId()).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.cardId==null) return super.equals(obj);
		if(!(obj instanceof ShowingCard)) return false;
		ShowingCard o =(ShowingCard)obj;
		return this.cardId.equals(o.cardId);
	}

	public void UPDATE() {
		Card c = MongoHandler.getInstance().getCard(cardId);
		c.printed = printed;
		c.status = status;
		c.owner = owner;
		c.inProposal = inProposal;
        c.creationDate = creationDate;
		c.UPDATE();
	}
	
	private boolean hasColor(String c) {
        return cardInfo.hasColor(c);
	}
	
	public boolean isBlack(){
		return hasColor("B") || 
			(cardInfo.type.equals("land")&&name.toLowerCase().contains("swamp"));
	}

	public boolean isBlue(){
		return hasColor("U") || 
			(cardInfo.type.equals("land")&&name.toLowerCase().contains("island"));
	}
	public boolean isWhite(){
		return hasColor("W") || 
			(cardInfo.type.equals("land")&&name.toLowerCase().contains("plain"));
	}
	public boolean isGreen(){
		return hasColor("G") || 
			(cardInfo.type.equals("land")&&name.toLowerCase().contains("forest"));
	}
	public boolean isRed(){
		return hasColor("R") || 
			(cardInfo.type.equals("land")&&name.toLowerCase().contains("mountain"));
	}

	public boolean isInProposal() {
		if(inProposal.equals("true"))
			return true;
		return false;
	}

	public void trade(User from, User to) {
		this.owner = to.toString();
		this.status = "booster";
		to.addToBooster(this.cardId);
		from.removeFromTrading(this.cardId);
		from.UPDATE();
		to.UPDATE();
		this.UPDATE();
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setStatus(String string) {
		status=string;
	}

	@Override
	public int compareTo(ShowingCard o) {
		if(o.cardId>cardId) return 1;
		if(o.cardId<cardId) return -1;
		return 0;
	}

	public boolean isInSubfolder(User owner) {
		return owner.subfolders.contains(this);
	}

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isNewer(DateTime dateTime) {
        return creationDate.after(dateTime.toDate());
    }

	public List<String> getInterestList(){
		return interestList;
	}

	public ShowingCard setInterestList(List<String> list){
		interestList = list;
		return this;
	}

	public String getInterestString() {
		StringBuilder ret = new StringBuilder();
		if(interestList==null)
			return "";
		boolean	first = true;
		for (String u : interestList){
			if(first)
				first=false;
			else ret.append(", ");
			ret.append(u);
		}
		return ret.toString();

	}

	public boolean hasInterests() {
		if (interestList==null) return false;
		return interestList.size()>0;
	}
}

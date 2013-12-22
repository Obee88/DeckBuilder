package suport;


import custom.classes.ShowingCard;
import custom.classes.TradingProposal;
import custom.classes.User;
import database.MongoHandler;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {



    public static void send(String email, String title, String body){
        try{
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("deckbuildermailsender","mailSender");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@deckbuilder.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject(title);
            message.setContent(body, "text/html");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        }catch (Exception e){}
    }

    public static void sendWishlistNotification(User u, ShowingCard sc, String luckyOwner) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Good news!</h2>").append("</br>");
        sb.append("<p>").append(luckyOwner).append(" just gethered ")
        .append(sc.name).append(" card that is in your wishlist!").append("</p></br>");
        sb.append("<img src=\"").append(sc.cardInfo.downloadLink).append("\"/></br>");
        sb.append("<a href=\"https://obee.xfer.hr/DeckBuilder-1.1/wicket/bookmarkable/obee.pages.TradePage?6\">Go and offer him a trade!</a>");
        send(u.getEmail(),"Wishlist notification!", sb.toString());
    }

    public enum ProposalNotificationType{
        Offer,
        Accept
    };

    public static void sendProposalNotification(TradingProposal tp, ProposalNotificationType type){
        try{
        StringBuilder sb = new StringBuilder();
        String[] fromImg  = new String[6];
        String[] toImg  = new String[6];
        for(int i=0;i<6;i++){
            if(i<tp.getFromList().size())
                fromImg[i]=tp.getFromList().get(i).cardInfo.downloadLink;
            else
                fromImg[i]="http://www.wizards.com/magic/images/mtgcom/fcpics/making/mr224_back.jpg";
            if(i<tp.getToList().size())
                toImg[i]=tp.getToList().get(i).cardInfo.downloadLink;
            else
                toImg[i]="http://www.wizards.com/magic/images/mtgcom/fcpics/making/mr224_back.jpg";
        }
        sb.append("<h2>");
        if(type==ProposalNotificationType.Offer)
            sb.append(tp.getFrom()).append(" sent you a trading proposal!</h2></br></br>");
        else if(type==ProposalNotificationType.Accept)
            sb.append((tp.getTo()+" accepted your trading proposal!</h2></br></br>"));
        sb.append("<table style=\"border: blue dotted 2px;\" >" ).append(
                "   <tr >" ).append(
                "     <td><h4>" ).append(tp.getFrom()).append(" is offering:" ).append(
                "     </h4></td>" ).append(
            "     </tr>" ).append(
                "     <tr>" ).append(
                "     <td>" ).append(
                "     <table>" ).append(
                "     <tr>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\"  src=\"").append(fromImg[0]).append("\" >" ).append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(fromImg[1]).append("\" >" ).append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(fromImg[2]).append("\" >" ).append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(fromImg[3]).append("\" >" ).append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(fromImg[4]).append("\" >" ).append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(fromImg[5]).append("\" >" ).append(
                "</td>" ).append(
                "     </tr>" ).append(
                "     </table>" ).append(
                "     </td>" ).append(
                "   </tr>" ).append(
                "   <tr >" ).append(
                "     <td>" ).append(
                "     <h4>for</h4> " ).append(
                "     </td>" ).append(
                "     </tr>" ).append(
                "     <tr>" ).append(
                "     <td>" ).append(
                "     <table>" ).append(
                "     <tr>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[0]).append("\">").append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[1]).append("\">").append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[2]).append("\">").append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[3]).append("\">").append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[4]).append("\">").append(
                "</td>" ).append(
                "<td>" ).append(
                "<img width=\"160\" height=\"240\" src=\"").append(toImg[5]).append("\">").append(
                "</td>" ).append(
                "     </tr>" ).append(
                "     </table>" ).append(
                "     </td>" ).append(
                "   </tr>" ).append(
                "     </table></br>");
        if(type==ProposalNotificationType.Offer){
            sb.append("<a href=\"https://obee.xfer.hr/DeckBuilder-1.1/wicket/bookmarkable/obee.pages.TradingProposalsPage\">");
            sb.append("</br><h2>Check it out</h2>").append("</a>");
            send(MongoHandler.getInstance().getUser(tp.getTo()).getEmail(),"Trading proposal from "+tp.getFrom(),sb.toString());
        }
        else if(type==ProposalNotificationType.Accept){
            sb.append("<a href=\"https://obee.xfer.hr/DeckBuilder-1.1/wicket/bookmarkable/obee.pages.BoosterPage\">");
            sb.append("</br><h2>Check it out</h2>").append("</a>");
            send(MongoHandler.getInstance().getUser(tp.getFrom()).getEmail(),"Trade successfull!",sb.toString());
        }
        }catch (Exception e)          {}
    }
}

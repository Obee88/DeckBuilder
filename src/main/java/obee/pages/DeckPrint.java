package obee.pages;

import com.sun.xml.fastinfoset.util.CharArray;
import custom.classes.CardInfo;
import custom.classes.OwnerMap;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import suport.Printer;
import suport.Slika;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Obee
 * Date: 01/08/14
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class DeckPrint extends MasterPage {


    private ByteArrayResource downloadResource;
    private Form<Object> form;
    private TextArea<String> textArea;
    MongoHandler mongo = MongoHandler.getInstance();
    private Form downloadForm;
    private ResourceLink<Object> downloadBtn;
    private boolean validateResult = true;

    public DeckPrint(final PageParameters params) {
        super(params, "DeckPrint");
        initForms();
        initComponents();
    }


    private void initComponents() {
        textArea = new TextArea<String>("deckTextArea",new Model<String>());
        textArea.add(new namesValidator());
        form.add(textArea);

    }

    private void initForms() {
        downloadForm = new Form("downloadForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                setResponsePage(DeckPrint.class);
            }
        };
        downloadForm.setVisible(false);
        add(downloadForm);
        form = new Form<Object>("printForm"){
            @Override
            protected void onSubmit() {
                String[] lines = textArea.getDefaultModelObjectAsString().split("\\r\\n");
                int counter = 0;
                List<CardInfo> pageList = new ArrayList<CardInfo>();
                List<Slika> pages = new ArrayList<Slika>();
                for (String line : lines){
                    if(line.trim().length()==0) continue;
                    String cardName = getNameFromLine(line).trim();
                    if (cardName.startsWith("#")) {
                        int id = Integer.parseInt(cardName.substring(1));
                        cardName = mongo.getCardInfo(id).name;
                    }
                    int num = getNumFromLine(line);
                    while (num-->0){
                        CardInfo ci  = null;
                        try{
                           ci = mongo.getCardInfoByName(cardName);
                        }   catch (NullPointerException e){
                            System.out.println();
                        }
                        pageList.add(ci);
                        counter++;
                        if(counter==8){
                            try {
                                pages.add(Printer.getInstance().generatePrintingPageFromInfos(pageList));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            counter=0;
                            pageList.clear();
                        }
                    }
                }
                if(!pageList.isEmpty())
                    try {
                        pages.add(Printer.getInstance().generatePrintingPageFromInfos(pageList));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                if(pages.size()>0)
                    generateZipSource(pages,new OwnerMap());
                downloadForm.add(downloadBtn = new ResourceLink<Object>("downloadButton", downloadResource){
                    @Override
                    public void onClick() {
                        super.onClick();
                    }
                });
                downloadForm.setVisible(true);

                System.out.println();
            }

            private String fix(String trim) {
                Map<String,String> map = new HashMap<String, String>();
                map.put("&#039;","'");
                for (String key : map.keySet()){
                    trim = trim.replace(key, map.get(key));
                }
                return trim;
            }

        };
        add(form);
    }

    private void generateZipSource(List<Slika> pages, OwnerMap ownerMap) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(fos);
        try {
            int index=0;
            for (Slika slika : pages) {
                zos.putNextEntry(new ZipEntry("slika"+index+++".png"));
                zos.write(getBytes(slika));
                zos.closeEntry();
            }
            zos.putNextEntry(new ZipEntry("vlasnici.txt"));
            zos.write(ownerMap.toString().getBytes());
            zos.closeEntry();
            zos.close();
            fos.flush();
            downloadResource = new ByteArrayResource("archive/zip",fos.toByteArray(),"pages.zip");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte[] getBytes(Slika s) {
        byte[] imageInByte=null;
        BufferedImage originalImage = s.toBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(originalImage, "png", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }

    class namesValidator implements IValidator<String> {
        @Override
        public void validate(IValidatable<String> validatable) {
            String[] lines = validatable.getValue().split("\\r\\n");
            for (String line : lines){
                if (line.trim().length()==0) continue;
                String name = getNameFromLine(line.trim());
                boolean isvalid = name!=null && mongo.isValidCardName(name);
                if (!isvalid){
                    error("Unknown card: "+line);
                    errorCall(validatable, "Unknown card: "+line);
                    return;
                }
            }
        }

        private boolean isDigit(String substring) {
            String digits  ="1234567890";
            for (Character c : substring.toCharArray()){
                if (!digits.contains(c.toString())) return false;
            }
            return true;
        }

        private void errorCall(IValidatable<String> validatable, String errorKey) {
            ValidationError error = new ValidationError();
            error.addMessageKey(getClass().getSimpleName() + "." + errorKey);
            validatable.error(error);
        }
    }

    private String getNameFromLine(String line){
        String digits = "0123456789";
        int i = 0;
        char[] lineChars = line.toCharArray();
        while(digits.contains(""+lineChars[i]))
            i++;
        boolean hasNum = i>0;
        if (hasNum && (Character.toLowerCase(lineChars[i++])!='x' || (lineChars[i++]!=' ' && lineChars[i-1]!='\t')))
            return null;
        return line.substring(i);
    }

    private Integer getNumFromLine(String line){
        String digits = "0123456789";
        char[] lineChars = line.toCharArray();
        int i = 0;
        while(digits.contains(""+lineChars[i]))
            i++;
        if (i<1)
            return 1;
        String digPart = line.substring(0,i);
        return Integer.parseInt(digPart);
    }
}

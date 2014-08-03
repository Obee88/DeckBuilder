package obee.pages;

import custom.classes.Administration;
import custom.classes.CardInfo;
import custom.classes.OwnerMap;
import custom.classes.ShowingCard;
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
        super(params, "Printer");
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
                    String cardName = getNameFromLine(line);
                    int num = getNumFromLine(line);
                    while (num-->0){
                        CardInfo ci  = null;
                        try{
                           ci = mongo.getCardInfo(mongo.getCardInfoIdByName(fix(cardName.trim())));
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
                boolean isvalid = mongo.isValidCardName(getNameFromLine(line.trim()));
                if (!isvalid){
                    error("Unknown card: "+line);
                    errorCall(validatable, "Unknown card: "+line);
                    return;
                }
            }
        }
        private void errorCall(IValidatable<String> validatable, String errorKey) {
            ValidationError error = new ValidationError();
            error.addMessageKey(getClass().getSimpleName() + "." + errorKey);
            validatable.error(error);
        }
    }

    private String getNameFromLine(String line){
        String[] parts = line.split("%");
        if ( parts.length==2) return parts[1];
        else return parts[0];
    }
    private Integer getNumFromLine(String line){
        try {
            String[] parts = line.split("%");
            if ( parts.length==2) return Integer.parseInt(parts[0]);
            else return  1;
        } catch (Exception e){
            return 1;
        }
    }
}

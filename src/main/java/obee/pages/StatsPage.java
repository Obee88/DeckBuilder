package obee.pages;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import custom.classes.*;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.SuccTradesPanel;
import custom.components.panels.TradingProposalPanel;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import suport.MailSender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StatsPage extends MasterPage {
    private final List allTradesChoices, screwdTradesChoices;
    private final TradingProposalPanel proposalsPanel;
    private final Label dateLabel;
    //    private final SuccTradesPanel panel;
    private ListChoice<TradingProposal> allTrades, screwdTrades;
//    DropDownChoice<String> userChooser;
    ListView listview;

    @SuppressWarnings("serial")
	public StatsPage(final PageParameters params) {
        super(params,"Stats");
        try{
            MongoHandler mongo = MongoHandler.getInstance();
            DBObject visitsObj = mongo.statisticsCollection.findOne(new BasicDBObject("id","views"));
            DBObject dailyStatsObj = (DBObject) visitsObj.get("daily_stats");
            JFreeChart dailyGroupChart =ChartFactory.createTimeSeriesChart(
                    "Daily visits on pages",      // chart title
                    "Month",                      // x axis label
                    "Visits",                      // y axis label
                    getDailyStats(dailyStatsObj),                  // data
                    true,                     // include legend
                    true,                     // tooltips
                    false                     // urls
            );
            add(new JFreeChartImage("dailyGroupVisitsChart", dailyGroupChart, 500, 300));
        } catch( Exception e){
            info(e.getMessage());
        }


//        List<String> USERS = new ArrayList<String>();
//        USERS.add("all"); USERS.addAll(mongo.getAllUserNames()); USERS.add("boosterDates");
//        userChooser = new DropDownChoice<String>("userChooser", new Model("all"),USERS);
//        OnChangeAjaxBehavior onUserchanged = new OnChangeAjaxBehavior() {
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//                String choice = userChooser.getDefaultModelObjectAsString();
//                if(choice.equals("boosterDates"))
//                    panel.setList(mongo.getBoosterDatesStrings());
//                else
//                    panel.setList(mongo.getSuccessfullTrades(choice));
//                target.add(panel);
//            }
//        };
//        userChooser.add(onUserchanged);
//        add(userChooser);

//        panel = new SuccTradesPanel("tradesPanel");
//        panel.setOutputMarkupId(true);
//        add(panel);

        allTradesChoices = mongo.getSuccessfullTrades("all");
        allTrades = new ListChoice<TradingProposal>("allTrades",
                new Model(allTradesChoices.isEmpty()?null: (Serializable) allTradesChoices.get(0)),
                new PropertyModel(this, "allTradesChoices"));
        allTrades.setOutputMarkupId(true);
        add(allTrades);

        screwdTradesChoices = mongo.getScrewdTrades("all");
        screwdTrades = new ListChoice<TradingProposal>("screwdTrades",
                new Model(screwdTradesChoices.isEmpty()?null: (Serializable) screwdTradesChoices.get(0)),
                new PropertyModel(this, "screwdTradesChoices"));
        screwdTrades.setOutputMarkupId(true);
        add(screwdTrades);

        proposalsPanel = new TradingProposalPanel("proposalsPanel") {

            @Override
            public void onDismiss(AjaxRequestTarget target) {

            }

            @Override
            public void onAcept(AjaxRequestTarget target) {

            }
        };

        if(!allTradesChoices.isEmpty()){
            proposalsPanel.setProposal((TradingProposal) allTradesChoices.get(0));
            proposalsPanel.setAcceptButtonVisible(false);
        }

        OnChangeAjaxBehavior onChangeAll = new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                TradingProposal tp = (TradingProposal) allTrades.getDefaultModelObject();
                proposalsPanel.setProposal(tp);
                target.add(proposalsPanel);
                proposalsPanel.setAcceptButtonVisible(false);
            }
        };
        allTrades.add(onChangeAll);

        OnChangeAjaxBehavior onChangeScrewd = new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                TradingProposal tp = (TradingProposal) screwdTrades.getDefaultModelObject();
                proposalsPanel.setProposal(tp);
                target.add(proposalsPanel);
                proposalsPanel.setAcceptButtonVisible(false);
            }
        };
        screwdTrades.add(onChangeScrewd);
        add(proposalsPanel);
        dateLabel = new Label(("dateLabel"),new DateTime(DateTimeZone.forID("Asia/Tokyo")).toString());
        add(dateLabel);
	}

    public XYDataset getDailyStats(DBObject dailyStatsObj){
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        try{
            if (dailyStatsObj==null) return dataset;

            DateTime now  = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
            int year = now.getYear();
            int month = now.getMonthOfYear();

            for(String pageName : dailyStatsObj.keySet()) if(!pageName.equals("month")){
                TimeSeries ts = new TimeSeries(pageName);
                for (int i = 1; i<=31; i++){
                    ts.add(new Day(i,month,year),getDailyValue(i,month,year,dailyStatsObj, pageName));
                }
                dataset.addSeries(ts);
            }
            return dataset;
        } catch (Exception e){
            info(e.getMessage());
            return new TimeSeriesCollection();
        }
    }

    private double getDailyValue(int day, int month, int year, DBObject dailyStatsObj, String pageName) {
        try{
            return Double.valueOf(((DBObject)(dailyStatsObj.get(pageName))).get(getDateString(day)).toString());
        } catch (NullPointerException e){
            return 0;
        }
    }
    private String getDateString(int month, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append("d_");
        if(month<10) sb.append("0");sb.append(month);
        sb.append("_").append(year);
        return sb.toString();
    }
    private String getDateString(int day) {
        StringBuilder sb = new StringBuilder();
        sb.append("d_");
        if(day<10) sb.append("0");sb.append(day);
//        sb.append("_").append(year);
        return sb.toString();
    }

    public class JFreeChartImage extends Image {

        private int width;
        private int height;

        public JFreeChartImage(String id, JFreeChart chart, int width, int height){
            super(id, new Model(chart));
            this.width = width;
            this.height = height;
        }

        @Override
        protected IResource getImageResource() {
            try{
                DynamicImageResource resource = new DynamicImageResource() {

                    @Override
                    protected byte[] getImageData(final Attributes attributes) {
                        JFreeChart chart = (JFreeChart) getDefaultModelObject();
                        return toImageData(chart.createBufferedImage(width, height));
                    }

                    @Override
                    protected void configureResponse(final ResourceResponse response, final Attributes attributes) {
                        super.configureResponse(response, attributes);

//                        if (isCacheAble() == false) {
//                            response.setCacheDuration(Duration.NONE);
//                            response.setCacheScope(WebResponse.CacheScope.PRIVATE);
//                        }
                    }
                };
                return resource;
            } catch (Exception e){
                info(e.getMessage());
            }
            return null;
        }

    }
}
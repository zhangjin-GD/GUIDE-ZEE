package guide.app.common;

import java.rmi.RemoteException;
import java.util.ArrayList;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;
/**
 * ZEE - Timeline（适用于不同应用程序，目前PR,PO,WO）
 * @author djy
 * 2024-8-12 14:58
 */

public class UDLogViews extends MboValueAdapter{

	public UDLogViews(MboValue mbv) {
		super(mbv);
    }
		
	@Override
    public void initValue() throws MXException, RemoteException {
        super.initValue();
        MboRemote mbo = getMboValue().getMbo();
        
        String personid = mbo.getUserInfo().getPersonId();
        String ownertable= mbo.getName();
        int id = mbo.getInt((ownertable).toLowerCase()+"id");
        int recordid = Integer.parseInt(mbo.getString(ownertable.toLowerCase().substring(0, 2)+"num"));

        // 优化后的逻辑
        ArrayList<String> udlog1 = buildWorklog(mbo, personid, recordid, ownertable);
        ArrayList<String> udlog2 = buildDoclinks(mbo, id, ownertable);
        ArrayList<String> udlog3 = buildCommlog(mbo, personid, id, ownertable);
        if(!ownertable.equalsIgnoreCase("") && ownertable.equalsIgnoreCase("WORKORDER")){
        	ArrayList<String> udlog_x= buildA_Ownertable(mbo, personid, id, ownertable);
        	String udlogValue = combineLogs(udlog1, udlog2, udlog3,udlog_x);
           if (!udlogValue.equals(mbo.getString("udlog"))) {
           mbo.setValue("udlog", udlogValue, 11L);
       }
        }else{
        String udlogValue = combineLogs(udlog1, udlog2, udlog3);
      if (!udlogValue.equals(mbo.getString("udlog"))) {
      mbo.setValue("udlog", udlogValue, 11L);
            }
        }
    }

    private ArrayList<String> buildWorklog(MboRemote mbo, String personid, int recordid, String ownertable) throws MXException, RemoteException {
        ArrayList<String> udlogcontent1 = new ArrayList<>();
        MboSetRemote worklogSet = mbo.getMboSet("WORKLOG");
        worklogSet.setWhere("recordkey = '" + recordid + "' and class = '" + ownertable + "'");
        worklogSet.reset();

        int count = worklogSet.count();
        for (int i = 0; i < count; i++) {
            MboRemote worklog = worklogSet.getMbo(i);
            String content = generateWorklogContent(worklog);
            udlogcontent1.add(content);
        }
        worklogSet.close();
        return udlogcontent1;
    }

    private ArrayList<String> buildDoclinks(MboRemote mbo, int id, String ownertable) throws MXException, RemoteException {
        ArrayList<String> udlogcontent2 = new ArrayList<>();
        MboSetRemote doclinksSet = mbo.getMboSet("DOCLINKS");
        doclinksSet.setWhere("ownerid = '" + id + "' and ownertable = '" + ownertable + "'");
        doclinksSet.reset();

        int count = doclinksSet.count();
        if (count > 0) {
            MboSetRemote docinfoSet = MXServer.getMXServer().getMboSet("DOCINFO", MXServer.getMXServer().getSystemUserInfo());
            for (int i = 0; i < count; i++) {
                MboRemote doclink = doclinksSet.getMbo(i);
                docinfoSet.setWhere("docinfoid = '" + doclink.getInt("docinfoid") + "'");
                docinfoSet.reset();
                String content = generateDoclinksContent(doclink, docinfoSet.getMbo(0), mbo);
                udlogcontent2.add(content);
            }
            docinfoSet.save();
            docinfoSet.close();
        }
        doclinksSet.close();
        return udlogcontent2;
    }

    private ArrayList<String> buildCommlog(MboRemote mbo, String personid, int id, String ownertable) throws MXException, RemoteException {
        ArrayList<String> udlogcontent3 = new ArrayList<>();
        MboSetRemote commlogSet = mbo.getMboSet("COMMLOG");
        commlogSet.setWhere(" ownerid = '" + id + "' and ownertable = '" + ownertable + "'");
        commlogSet.reset();

        int count = commlogSet.count();
        for (int i = 0; i < count; i++) {
            MboRemote commlog = commlogSet.getMbo(i);
            String content = generateCommlogContent(commlog, mbo);
            udlogcontent3.add(content);
        }
        commlogSet.close();
        return udlogcontent3;
    }
    
    private ArrayList<String> buildA_Ownertable(MboRemote mbo, String personid, int id, String ownertable) throws MXException, RemoteException{
    	ArrayList<String> udlogcontent_x = new ArrayList<>();
    	String A_Ownertable = "A_" + ownertable;
    	MboSetRemote A_OwnertableSet = MXServer.getMXServer().getMboSet(A_Ownertable, MXServer.getMXServer().getSystemUserInfo());
    	A_OwnertableSet.setWhere((ownertable.toLowerCase()+"id = ") + "'" + id + "'"+ " order by eaudittimestamp desc");
    	A_OwnertableSet.reset();
    	
    	int count = A_OwnertableSet.count();
        for (int i = 0; i < count; i++) {
            MboRemote A_Owner = A_OwnertableSet.getMbo(i);
            String content = generateA_OwnertableContent(A_Owner, mbo);
            udlogcontent_x.add(content);
        }
        A_OwnertableSet.close();
        return udlogcontent_x;
    }

    private String combineLogs(ArrayList<String>... logs) {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<String> log : logs) {
            for (String entry : log) {
                sb.append(entry).append("<br>");
            }
        }
        return sb.toString();
    }

    private String generateWorklogContent(MboRemote worklog) throws RemoteException, MXException {
        String recordkey = worklog.getString("recordkey");
        String createby1 = worklog.getString("createby");
        String createdate1 = worklog.getString("createdate");
        String description = worklog.getString("description");
        String description_longdescription = worklog.getString("description_longdescription");

        return "<p style=\"background-color: #dcdadb; border-bottom: 2px solid #1A80B6; padding: 5px; margin-bottom: 0px;\"><span style=\"\"><b>"
                + createby1
                + "</b> commented on: "
                + "<em>"
                + createdate1
                + "</em>"
                + " to "
                + "<strong>"
                + recordkey
                + "</span>"
                + "<div style=\"background-color: #F0F0F0; padding: 3px;\"><p>"
                + "Commented Theme: "
                + description
                + "</p>"
                + "<p>"
                + "Commented Content: <br>"
                + description_longdescription
                + "</p></div>";
    }

    private String generateDoclinksContent(MboRemote doclink, MboRemote docinfo, MboRemote mbo) throws RemoteException, MXException {
        String createby2 = doclink.getString("createby");
        String document = doclink.getString("document");
        String createdate2 = doclink.getString("createdate");
        String urltype = docinfo.getString("urltype");
        String urlname = docinfo.getString("urlname");

        return "<p style=\"background-color:  #F0F0F0; border-bottom: 1px solid #1A80B6; padding: 3px; margin-bottom: 0px;\"><span style=\"\"><b>"
                + createby2
                + "</b> added a:  "
                + urltype
                + "&nbsp; called: "
                + document
                + " to "
                + "<strong>"
                + mbo.getString(mbo.getName().toLowerCase().substring(0, 2)+"num")
                + "</strong>"
                + "</span>"
                + "    Create Date is: "
                + "<em>"
                + createdate2
                + "</em>"
                + "    Document Address is: "
                + "<font color=blue>"
                + "<ins>"
                + "<em>"
                + urlname
                + "</em>"
                + "</ins></span>";
    }

    private String generateCommlogContent(MboRemote commlog, MboRemote mbo) throws RemoteException, MXException {
        String createby3 = commlog.getString("createby");
        String sendto = commlog.getString("sendto");
        String createdate3 = commlog.getString("createdate");
        String subject = commlog.getString("subject");
        String message = commlog.getString("message");
 
        return "<p style=\"background-color: #dcdadb; border-bottom: 2px solid #1A80B6; padding: 5px; margin-bottom: 0px;\"><span style=\"\"><b>"
                + createby3
                + "</b> send on: "
                + "<em>"
                + createdate3
                + "</em>"
                + " to "
                + "<strong>"
                + mbo.getString(mbo.getName().toLowerCase().substring(0, 2)+"num")
                + "</span>"
                + "<div style=\"background-color: #F0F0F0; padding: 3px;\"><p>"
                + " Send To : "
                + sendto
                + "</p>"
                + "<p>"
                + "Subject : <br>"
                + subject
                + "</p>"
                + "<p>"
                + "Message : <br>"
                + message
                + "</p></div>";
    }
    
    private String generateA_OwnertableContent(MboRemote A_Owner, MboRemote mbo) throws RemoteException, MXException{
		Integer eaudittransid = A_Owner.getInt("eaudittransid");
		String actstart = A_Owner.getString("actstart");
		String actfinish = A_Owner.getString("actfinish");
		String eauditusername = A_Owner.getString("eauditusername");
		String eaudittimestamp = A_Owner.getString("eaudittimestamp");
		String checkview = "";
		if(!actstart.equalsIgnoreCase("") || !actfinish.equalsIgnoreCase("") ){
			checkview = "<p style=\"background-color:  #F0F0F0; border-bottom: 0.5px solid #1A80B6; padding: 1px; margin-bottom: 0px;\"><span style=\"\">"
					+ "Change by:"
					+ eauditusername
					+ "&nbsp;&nbsp;&nbsp;"
					+ "Change date:"
					+ eaudittimestamp
					+ "&nbsp;&nbsp;&nbsp;"
					+ "Act start date: "
					+ actstart
					+ "&nbsp;&nbsp;&nbsp;"
					+ "Act finish date: "
					+ actfinish+ "</span>";

		}
		return checkview;
    }	
}

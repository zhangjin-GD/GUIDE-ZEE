package guide.webclient.beans.doclink;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.webclient.system.beans.AppBean;


public class UDPreviewAndCountAppBean extends AppBean {

    public void previewAndCount() throws Exception{
    	MboRemote mbo = this.app.getAppBean().getMbo();
    	int row = getRowIndexFromEvent(this.clientSession.getCurrentEvent());
	    MboSetRemote rowMboSet = getMboSet();
	    MboRemote rowMbo = rowMboSet.getMbo(row);
	    Integer doclinksid = rowMbo.getInt("doclinksid");
	    Integer previewCount = rowMbo.getInt("udpreviewcount");
	    //打开附件地址
	    String docPath = MXServer.getMXServer().getProperty("mxe.doclink.path01");
        String openUrl = docPath.split("=")[1].trim();
    	MboSetRemote docinfoSet = MXServer.getMXServer().getMboSet("DOCINFO", MXServer.getMXServer().getSystemUserInfo());
    	docinfoSet.setWhere("docinfoid in(select docinfoid from DOCLINKS where doclinksid='"+doclinksid+"') ");
		if (!docinfoSet.isEmpty() && docinfoSet.count() > 0) {
			MboRemote docinfo = docinfoSet.getMbo(0);
			String urlname = docinfo.getString("urlname");
			urlname = urlname.split("DOCLINKS")[1].trim();
			//JAVA中正则表达式,用"\\\\"表示"\"
			String url = openUrl + urlname;
			url = url.replaceAll("\\\\", "/").trim();
			this.app.openURL(url, true);
		}	
		docinfoSet.close();
		
		//下载计数
		previewCount +=1;
		MboSetRemote doclinksSet = MXServer.getMXServer().getMboSet("DOCLINKS", MXServer.getMXServer().getSystemUserInfo());
		doclinksSet.setWhere("doclinksid='"+doclinksid+"'");
		if(!doclinksSet.isEmpty() && doclinksSet.count()>0){
			doclinksSet.getMbo(0).setValue("udpreviewcount", previewCount, 11L);
			doclinksSet.save();
		}
		doclinksSet.close();
		
		MboSetRemote previewRecordSet = MXServer.getMXServer().getMboSet("UDPREVIEWRECORD", MXServer.getMXServer().getSystemUserInfo());
		previewRecordSet.setWhere("1=2");
		MboRemote previewRecord = previewRecordSet.add();
		previewRecord.setValue("description", mbo.getString("document"), 11L);
		previewRecord.setValue("previewby", mbo.getUserInfo().getPersonId(), 11L);// 创建人
		previewRecord.setValue("previewtime", MXServer.getMXServer().getDate(), 11L);// 创建时间
		previewRecord.setValue("ownertable", "DOCLINKS", 11L);
		previewRecord.setValue("ownerid", doclinksid, 11L);
		previewRecordSet.save();
		previewRecordSet.close();
	  }
	
	
}
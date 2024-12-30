package guide.webclient.beans.inventory;

import guide.app.inventory.InvStock;

import java.rmi.RemoteException;

import org.json.JSONException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

import java.util.Date;

public class InvStockAppBean extends AppBean{

	public void adJustInv() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		String langCode = mbo.getUserInfo().getLangCode();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String status209 = mbo.getString("udsapstatus209");
		String status210 = mbo.getString("udsapstatus210");
		if ((status209 != null && !status209.equalsIgnoreCase(""))
				|| (status210 != null && !status210.equalsIgnoreCase(""))){
			throw new MXApplicationException("guide", "1039");
		}
		try {
			((InvStock) mbo).adjustInv();
			this.app.getAppBean().save();
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示/Tips", mbo.getMessage("guide", "1038", langCode), 1);
		} catch (JSONException e) {
			e.printStackTrace();
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示/Tips", e.toString(), 1);
		}
	}
	
	/*
	 * ZEE重新盘点冻结的物资
	 * DJY 2024-1-22-13:00
	 */
	public void UDDELETE() throws RemoteException, MXException{
		MboRemote mbo = this.app.getAppBean().getMbo();
		 String oldinvnum = mbo.getString("invstocknum");
		 String oldstockby = mbo.getString("stockby");
		 String olddescription = mbo.getString("description");
		
		 MboSetRemote oldinvstockSet = mbo.getMboSet("UDINVSTOCKLOC");
		 MboSetRemote oldbinSet = mbo.getMboSet("UDINVSTOCKBIN");

		 
		 MboSetRemote newInvstockSet = MXServer.getMXServer().getMboSet("UDINVSTOCK", MXServer.getMXServer().getSystemUserInfo());
		 newInvstockSet.setWhere(" 1=2 ");
		 

		 MboRemote newbo = newInvstockSet.add(11L);
		 newbo.setValue("stockby", oldstockby,11L);
		 newbo.setValue("description",olddescription,11L);
		 Date thisTime = MXServer.getMXServer().getDate();
		 newbo.setValue("stockdate",thisTime,11L);

		 MboSetRemote newstorelocSet = newbo.getMboSet("UDINVSTOCKLOC");
		 MboSetRemote newbinSet = newbo.getMboSet("UDINVSTOCKBIN");
		 MboSetRemote newinvstocklineSet = newbo.getMboSet("UDINVSTOCKLINE");
		 
		 
		 if (!oldinvstockSet.isEmpty() && oldinvstockSet.count() > 0)
		 {
			 for (int i = 0; i < oldinvstockSet.count(); i++){
				 MboRemote oldstoreloc = oldinvstockSet.getMbo(i);
				 String oldstorelocnum=oldstoreloc.getString("storeloc");
				 String oldsiteid=oldstoreloc.getString("siteid");		 
				 MboRemote newstoreloc = newstorelocSet.add(11L);
				 String newinvstocknum=newbo.getString("invstocknum");
				 newstoreloc.setValue("storeloc",oldstorelocnum,11L);
				 newstoreloc.setValue("siteid",oldsiteid,11L);
				 newstoreloc.setValue("invstocknum",newinvstocknum,11L);
				 newstorelocSet.save();
			 }
		 }
		 if (!oldbinSet.isEmpty() && oldbinSet.count() > 0)
		 {
			 for (int i = 0; i < oldbinSet.count(); i++){
				 MboRemote oldbin = oldbinSet.getMbo(i);
				 String oldbinnum=oldbin.getString("udbinnum");
				 String oldsiteid=oldbin.getString("siteid");
				 MboRemote newbin = newbinSet.add(11L);
				 String newinvstocknum=newbo.getString("invstocknum");
				 newbin.setValue("udbinnum",oldbinnum,11L);
				 newbin.setValue("siteid",oldsiteid,11L);
				 newbin.setValue("invstocknum",newinvstocknum,11L);
				 newbinSet.save();
			 }
		 }
		 
		 MboSetRemote oldinvstocklineSet = mbo.getMboSet("UDINVSTOCKLINE");
		 oldinvstocklineSet.setWhere("remark = 'DELETE'");
		 oldinvstocklineSet.reset();
		 if (!oldinvstocklineSet.isEmpty() && oldinvstocklineSet.count() > 0)
		 {
			 for (int i = 0; i < oldinvstocklineSet.count(); i++){
				 MboRemote oldinvstockline = oldinvstocklineSet.getMbo(i);

				 String oldsiteid=oldinvstockline.getString("siteid");
				 String oldorgid=oldinvstockline.getString("orgid");
				 String olditemnum=oldinvstockline.getString("itemnum");
				 String oldstoreloc=oldinvstockline.getString("storeloc");
				 String oldbinnum=oldinvstockline.getString("binnum");
				 String oldlotnum=oldinvstockline.getString("lotnum");
				 String oldremark=oldinvstockline.getString("remark");
				 String oldmaterialtype=oldinvstockline.getString("materialtype");
				 String oldunit=oldinvstockline.getString("unit");
				 Date oldTime=oldinvstockline.getDate("invdate");
				 
				 MboSetRemote oldinvbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
				 oldinvbalancesSet.setWhere(" itemnum='"+olditemnum+"' and location='"+oldstoreloc+"' and binnum='"+oldbinnum+"' and lotnum='"+oldlotnum+"'");
				 oldinvbalancesSet.reset();
				 double oldcurbal = 0.0D;
				 if (!oldinvbalancesSet.isEmpty() && oldinvbalancesSet.count() > 0){
					 MboRemote oldinvbalances=oldinvbalancesSet.getMbo(0);
					  oldcurbal=oldinvbalances.getDouble("curbal");
				 }
				 oldinvbalancesSet.close();
				 
				 double oldavgcost = 0.0D;
				 double oldstdcost = 0.0D;
				 double oldlastcost = 0.0D;
				 MboSetRemote oldinvcostSet = MXServer.getMXServer().getMboSet("INVCOST", MXServer.getMXServer().getSystemUserInfo());
				 oldinvcostSet.setWhere("itemnum='"+olditemnum+"' and location='"+oldstoreloc+"'");
				 oldinvcostSet.reset();
				 if (!oldinvcostSet.isEmpty() && oldinvcostSet.count() > 0){
					 MboRemote oldinvcost=oldinvcostSet.getMbo(0);
					 oldavgcost=oldinvcost.getDouble("avgcost");
					 oldstdcost=oldinvcost.getDouble("stdcost");
					 oldlastcost=oldinvcost.getDouble("lastcost");
				 }
				 oldinvcostSet.close();
				 		 
				 MboRemote newinvstockline = newinvstocklineSet.add(11L);
				 String newinvstocknum=newbo.getString("invstocknum");
				 
				 newinvstockline.setValue("siteid",oldsiteid,11L);
				 newinvstockline.setValue("orgid",oldorgid,11L);
				 newinvstockline.setValue("invstocknum",newinvstocknum,11L);
				 newinvstockline.setValue("itemnum",olditemnum,11L);
				 newinvstockline.setValue("storeloc",oldstoreloc,11L);
				 newinvstockline.setValue("binnum",oldbinnum,11L);
				 newinvstockline.setValue("lotnum",oldlotnum,11L);
				 newinvstockline.setValue("remark",oldremark,11L);
				 newinvstockline.setValue("materialtype",oldmaterialtype,11L);
				 newinvstockline.setValue("unit",oldunit,11L);
				 newinvstockline.setValue("curbal",oldcurbal,11L);
				 newinvstockline.setValue("avgcost",oldavgcost,11L);
				 newinvstockline.setValue("stdcost",oldstdcost,11L);
				 newinvstockline.setValue("lastcost",oldlastcost,11L);
				 double newavgcost = newinvstockline.getDouble("avgcost");
				 double newcurbal = newinvstockline.getDouble("curbal");
				 newinvstockline.setValue("invcost",newavgcost * newcurbal,11L);
				 newinvstockline.setValue("invdate",oldTime,11L);
				 newinvstockline.setValue("linenum",i+1,11L);
				 newinvstockline.setValue("remark","",11L);
				 newinvstocklineSet.save();
			 }
		 }
		 
		 newInvstockSet.close();
		 clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Result： Create a frozen re-stockcounting, the number is "+newbo.getString("invstocknum")+" !", 1);
		String appname = "UDINVSTOCK";
		 WebClientEvent event = this.clientSession.getCurrentEvent();
         if (event != null) {
             String value = event.getValueString();
             if (value != null) {
                     if (value.equals("")) {
                             super.execute();
                             // 获取系统session实例
                             WebClientSession wcs = sessionContext.getMasterInstance();
                             // 构建跳转至启动中心的URL
                             String url = "?event=loadapp&value="+appname+"&uniqueid="+newbo.getInt("UDINVSTOCKID")+"";
                             // 跳转动作执行
                             wcs.gotoApplink(url);
                             System.out.println("url123123----"+url);
                     }
             }
     }
    
	}
	
}

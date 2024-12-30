package guide.webclient.beans.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;

/**
 *@function:
 *@author:ZEE-修改POLINE状态和交货日期
 *@date:2023-07-21 15:39:01
 *@modify:
 */
public class UDEditStatusBean extends DataBean {
	
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote po = this.app.getAppBean().getMbo();
		if(po!=null){
			DataBean personcdsetbean = this.app.getDataBean("polines_poline_table");
			MboRemote poline = personcdsetbean.getMbo();
			if (getString("status")!=null && !getString("status").equalsIgnoreCase("")) {
				poline.setValue("udstatus",getString("status"),11L);
				//POLINE confirmed 后记录confirmed时间 2024-06-17 16:38:27 DJY
				if(getString("status").equalsIgnoreCase("CONFIRMED")){
				poline.setValue("udconfirmdate", MXServer.getMXServer().getDate(), 11L);
				}
			}
			if (getString("deliverydate")!=null && !getString("deliverydate").equalsIgnoreCase("")) {
				poline.setValue("uddeliverydate",getString("deliverydate"),11L);
			}
			if (getString("remark")!=null && !getString("remark").equalsIgnoreCase("")) {
				poline.setValue("remark",getString("remark"),11L);
			}
			//给POLINE的期望交货时间赋值
			if(getString("deliverydate")!=null && !getString("deliverydate").equalsIgnoreCase("")){
				poline.setValue("udexdeliverydate", getString("deliverydate"), 11L);
			}
			this.app.getDataBean().save();
			this.app.getDataBean().refreshTable();
			this.app.getDataBean().reloadTable();
		}
		return 1;
	}
	
}

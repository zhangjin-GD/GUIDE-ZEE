package guide.webclient.beans.po;

import guide.app.po.UDWFZEESendMailAction;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.common.commtmplt.CommTemplateRemote;
import psdi.common.commtmplt.CommTemplate;

/**
 *@function:PO-批量发送邮件
 *@author:zj
 *@date:2024-06-13 13:15:57
 *@modify:
 */
public class UDPOMulMailSentDataBean extends DataBean {
	
	public int execute() throws MXException, RemoteException {
		Vector vec = getSelection();
		if (vec.size() == 0) {
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Please select at least one PO data !", 1);
			return 1;
		} else {
			MboRemote selectMbo = null;
			for (int i = 0; i< vec.size(); i++) {
				selectMbo = (MboRemote) vec.elementAt(i);//mr是勾选的数据
				if (selectMbo != null) {
					UDWFZEESendMailAction.updateTemplateDoc(selectMbo);
					sentAllMail(selectMbo);
					setValueSentDate(selectMbo);
				}
			}
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "All sent completed !\nNumber of emails sent : "+vec.size(), 1);
			return 1;
		}
	}
	
	private void sentAllMail(MboRemote selectMbo) throws MXException, RemoteException {
		MboSetRemote templateSet = MXServer.getMXServer().getMboSet("COMMTEMPLATE", MXServer.getMXServer().getSystemUserInfo());
		templateSet.setWhere(" commtemplateid = '"+UDWFZEESendMailAction.ownerid+"' ");
		templateSet.reset();
		if (!templateSet.isEmpty() && templateSet.count() > 0) {
			CommTemplate template = (CommTemplate) templateSet.getMbo(0);
			template.sendMessage(selectMbo);
		}
		templateSet.close();
	}
	
	private void setValueSentDate(MboRemote selectMbo) throws MXException, RemoteException {
		MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
		poSet.setWhere(" ponum = '"+selectMbo.getString("ponum")+"' ");
		poSet.reset();
		if (!poSet.isEmpty() && poSet.count() > 0) {
			MboRemote po = poSet.getMbo(0);
			po.setValue("udposent", MXServer.getMXServer().getDate(), 11L);
		}
		poSet.save();
		poSet.close();
	}
	
}

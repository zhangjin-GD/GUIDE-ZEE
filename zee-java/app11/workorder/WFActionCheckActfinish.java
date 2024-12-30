package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckActfinish implements ActionCustomClass {

	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {
		String udcompany = mbo.getString("udcompany");
		if (!udcompany.equalsIgnoreCase("") || udcompany != null) {
			if (udcompany.equalsIgnoreCase("2533XOCT")) {
				Date sysDate = MXServer.getMXServer().getDate();// 系统时间
				Date actfinish = mbo.getDate("actfinish");// 维修结束时间
				double hour = sysDate.getTime() - actfinish.getTime();
				if (hour < 0) {
					throw new MXApplicationException("guide", "1202");
				}
			}
		}
	}
}
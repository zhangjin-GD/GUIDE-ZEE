package guide.app.signin;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDSchPlan extends UDMbo implements MboRemote {
	public UDSchPlan(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDate = MXServer.getMXServer().getDate();
		String period = CommonUtil.getDateFormat(currentDate, "yyyy-MM");
		setValue("period", period, 2L);
		setValue("description", period+"排班申请", 11L);
	}
	
}

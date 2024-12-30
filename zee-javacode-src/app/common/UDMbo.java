package guide.app.common;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDMbo extends Mbo implements MboRemote {

	public UDMbo(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		// 新增默认值（状态、状态时间、创建人、创建时间、公司、部门）
		String status = "WAPPR";
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("createby", personId, 2L);// 创建人
		this.setValue("createtime", currentDate, 11L);// 创建时间
		this.setValue("status", status, 11L);// 状态
		this.setValue("statustime", currentDate, 11L);// 状态时间
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changetime", MXServer.getMXServer().getDate(), 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
				&& getString("status").equalsIgnoreCase("APPR")) {
			// 批准人和批准时间
			setValue("apprby", getUserInfo().getPersonId(), 11L);
			setValue("apprtime", MXServer.getMXServer().getDate(), 11L);
		}
	}
}

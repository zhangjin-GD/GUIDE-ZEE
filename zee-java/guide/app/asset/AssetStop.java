package guide.app.asset;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class AssetStop extends UDMbo implements MboRemote{

	public AssetStop(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	public void init() throws MXException {
		super.init();
		if(!toBeAdded()){
			try {
				String status = getString("status");
				if(status != null && status.equalsIgnoreCase("APPR")){
					setFlags(7L);
				}
				Date currentTime = CommonUtil.getFormatDate(MXServer.getMXServer().getDate(), "yyyy-MM-dd");
				Date createTime = CommonUtil.getFormatDate(CommonUtil.getCalDate(getDate("createtime"), 1), "yyyy-MM-dd");
				if(currentTime.equals(createTime) || currentTime.after(createTime)){
					setFieldFlag("description", 7L, true);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		
		Date currentDate = MXServer.getMXServer().getDate();
		String currentDateStr = CommonUtil.getDateFormat(currentDate, "yyyy-MM-dd");
		Date nextDate = CommonUtil.getCalDate(currentDate, 1);
		String nextDateStr = CommonUtil.getDateFormat(nextDate, "yyyy-MM-dd");
		
		String appName = this.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("udassetstop")) {
			setValue("udapptype", "ASSETSTOP", 11L);
			setValue("description", nextDateStr+"停机申请", 11L);
		}else if (appName != null && appName.equalsIgnoreCase("udassetpm")) {
			setValue("udapptype", "ASSETPM", 11L);
			setValue("description", currentDateStr+"碎片保养", 11L);
		}
		
	}
	
	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		super.delete(accessModifier);
		this.getMboSet("UDASSETSTOPLINE").deleteAll(2L);
	}

	@Override
	public void undelete() throws MXException, RemoteException {
		super.undelete();
		this.getMboSet("UDASSETSTOPLINE").undeleteAll();
	}
	
}

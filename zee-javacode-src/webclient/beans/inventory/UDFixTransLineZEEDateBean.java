package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 * ZEE - 工单领料时，关联该物资已创建的固定资产（固定资产卡片/设备），且给设备的udparent赋值
 * 2025-2-8-14:00
 */
public class UDFixTransLineZEEDateBean extends DataBean{
	
	public synchronized int execute() throws MXException, RemoteException {
//		MboRemote matuse = this.app.getAppBean().getMbo();
		MboRemote fixed = this.getMbo();
		MboRemote matusetrans = fixed.getOwner();
		Vector<MboRemote> vector = this.getSelection();
		if (vector.size() > 0) {
			String personId = matusetrans.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote fixtranslineSet = matusetrans.getMboSet("UDFIXTRANSLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				int linenum = (int) fixtranslineSet.max("linenum") + 1;
				int invuseid = matusetrans.getInt("matusetransid");
				int invuselineid = matusetrans.getInt("matusetransid");
				String issuetype = matusetrans.getString("issuetype");
				String enterby = matusetrans.getString("enterby");
				int udfixedid = mr.getInt("udfixedid");
				String fixassetnum = mr.getString("fixassetnum");
				String useby = mr.getString("useby");
				String usebynew = mr.getString("usebynew");
				if ("ISSUE".equalsIgnoreCase(issuetype) && mr.isNull("usebynew")) {
					Object params[] = { "Notice, please select new use person!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
				MboRemote fixtransline = fixtranslineSet.addAtEnd();
				fixtransline.setValue("linenum", linenum, 11L);
				fixtransline.setValue("fixassetnum", fixassetnum, 11L);
				fixtransline.setValue("invuseid", invuseid, 11);
				fixtransline.setValue("invuselineid", invuselineid, 11L);
				fixtransline.setValue("udfixedid", udfixedid, 11L);
				fixtransline.setValue("usebyold", useby, 11L);
				fixtransline.setValue("useby", usebynew, 11L);
				fixtransline.setValue("changeby", personId, 11L);
				fixtransline.setValue("changedate", currentDate, 11L);
				if ("ISSUE".equalsIgnoreCase(issuetype)) {
					mr.setValue("useby", usebynew, 2L);
					mr.setValue("status", "7", 11L);// 领用 - 在用（7）
				} else if ("RETURN".equalsIgnoreCase(issuetype)) {
					mr.setValue("useby", enterby, 2L);
					mr.setValue("status", "5", 11L);// 退回 - 闲置（5）
				}
				if(!mr.getString("assetnum").equalsIgnoreCase("")){
					MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
					assetSet.setWhere("assetnum = '"+mr.getString("assetnum")+"' ");
					assetSet.reset();
					if(!assetSet.isEmpty() && assetSet.count()>0 && !matusetrans.getString("assetnum").equalsIgnoreCase("")){
						MboRemote asset = assetSet.getMbo(0);
						asset.setValue("udparent", matusetrans.getString("assetnum"),11L);
						assetSet.save();
					}
					assetSet.close();
				}
			}
		}
		return  super.execute();
	}
}

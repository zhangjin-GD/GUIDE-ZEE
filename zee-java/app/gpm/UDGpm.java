package guide.app.gpm;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDGpm extends UDMbo implements MboRemote {

	public UDGpm(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		setValue("status", "INACTIVE", 11L);// 状态
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (toBeAdded() || isModified("assetnum") || isModified("gjpnum")) {
			String assetNum = getString("assetnum").split("-")[0];
			String gjpNum = getString("gjpnum").split("-")[2];
			String keyNum = CommonUtil.getAbbrCompany(getString("udcompany")) + "-" + assetNum + "-" + gjpNum;
			String dbGpmNum = CommonUtil.getValue("UDGPM", "gpmnum='" + keyNum + "'", "gpmnum");
			if (dbGpmNum != null && !dbGpmNum.equalsIgnoreCase("")) {
				throw new MXApplicationException("guide", "1064");
			}
			this.setValue("gpmnum", keyNum, 11L);// 自动编号

			String gjpnum = this.getString("gjpnum");
			MboSetRemote gpmSeqSet = this.getMboSet("$UDGPMSEQUENCE", "UDGPMSEQUENCE",
					"gpmnum='" + keyNum + "' and linenum = '1'");
			if (!gpmSeqSet.isEmpty() && gpmSeqSet.count() > 0) {
				MboRemote gpmSeq = gpmSeqSet.getMbo(0);
				gpmSeq.setValue("gjpnum", gjpnum, 11L);
			} else {
				MboRemote gpmSeq = gpmSeqSet.add();
				gpmSeq.setValue("linenum", 1, 11L);
				gpmSeq.setValue("gjpnum", gjpnum, 11L);
			}
		}
	}

	public String addWoPm(Date startDate, Date compDate) throws RemoteException, MXException {
		MboSetRemote woPmSet = this.getMboSet("$WORKORDER", "WORKORDER", "1=2");
		MboRemote woPm = woPmSet.add();
		/**
		 * ZEE-LOCATION字段需避开校验逻辑
		 * 2023-08-03 14:40:12
		 */
		String udcompany = woPm.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			
			woPm.setValue("location", getString("ASSET.location"), 11L);
			woPm.setValue("udworktype2", "PM", 11L);
			
		}
		
		
		woPm.setValue("description", getString("description"), 11L);
		woPm.setValue("assetnum", getString("assetnum"), 2L);
		woPm.setValue("udgpmnum", getString("gpmnum"), 2L);
		

		
		if (startDate != null && compDate != null) {
			// 先设置空 再设置（直接设置触发字段校验）
			woPm.setValueNull("targstartdate", 11L);
			woPm.setValueNull("targcompdate", 11L);
			woPm.setValue("targstartdate", startDate, 2L);
			woPm.setValue("targcompdate", compDate, 2L);
		}
		String wonum = woPm.getString("wonum");
		woPmSet.save();
		woPmSet.close();
		return wonum;
	}

}

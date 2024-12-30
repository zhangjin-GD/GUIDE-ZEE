package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.workorder.UDWO;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.app.asset.Asset;

public class VAssetTstransSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VAssetTstransSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VAssetTstrans(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null) {
			String appName = owner.getThisMboSet().getApp();
			Date sysDate = MXServer.getMXServer().getDate();
			mbo = this.addAtEnd();
			double workhour = 0;
			double boxunit = 0;
			double boxteu = 0;
			double electrickwh = 0;
			double oill = 0;
			double worklock = 0;
			double control = 0;
			String eqnum = "";
			String type = this.getString("type");
			// 吊具管理
			if ("UDASSETTS".equalsIgnoreCase(appName) || "UDTRAILER".equalsIgnoreCase(appName)) {
				eqnum = owner.getString("udeqnum");
				workhour = owner.getDouble("udworkhour");
				boxunit = owner.getDouble("udboxunit");
				boxteu = owner.getDouble("udboxteu");
				electrickwh = owner.getDouble("udelectrickwh");
				oill = owner.getDouble("udoill");
				worklock = owner.getDouble("udworklock");
				control = owner.getDouble("udcontrol");
			} else if ("UDASSET".equalsIgnoreCase(appName) || "UDWOEM".equalsIgnoreCase(appName)
					|| "UDWOCM".equalsIgnoreCase(appName)) {
				String assetnum = owner.getString("assetnum");
				MboSetRemote assetSet = owner.getMboSet("$ASSET", "ASSET", "udeqnum='" + assetnum + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					eqnum = asset.getString("assetnum");
					workhour = asset.getDouble("udworkhour");
					boxunit = asset.getDouble("udboxunit");
					boxteu = asset.getDouble("udboxteu");
					electrickwh = asset.getDouble("udelectrickwh");
					oill = asset.getDouble("udoill");
					worklock = asset.getDouble("udworklock");
					control = asset.getDouble("udcontrol");
				}
			}
			mbo.setValue("workhour", workhour, 11L);
			mbo.setValue("boxunit", boxunit, 11L);
			mbo.setValue("boxteu", boxteu, 11L);
			mbo.setValue("electrickwh", electrickwh, 11L);
			mbo.setValue("oill", oill, 11L);
			mbo.setValue("worklock", worklock, 11L);
			mbo.setValue("control", control, 11L);
			if ("LOWER".equalsIgnoreCase(type)) {
				mbo.setValue("eqnum", eqnum, 11L);
			}
			mbo.setValue("createdate", sysDate, 11L);
		}
		return mbo;
	}

	@Override
	public void execute() throws MXException, RemoteException {
		super.execute();
		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof Asset && !this.isNull("eqnum")) {
			String appName = owner.getThisMboSet().getApp();
			if ("UDASSETTS".equalsIgnoreCase(appName) || "UDTRAILER".equalsIgnoreCase(appName)) {
				assetTs(owner);
			} else if ("UDASSET".equalsIgnoreCase(appName)) {
				asset(owner);
			}
		}

		if (owner != null && owner instanceof UDWO && !this.isNull("eqnum")) {
			assetWo(owner);
		}
	}

	private void assetWo(MboRemote owner) throws RemoteException, MXException {

		String assetnum = owner.getString("assetnum");
		String udcostcenter = owner.getString("asset.udcostcenter");
		String eqnum = this.getString("eqnum");
		String type = this.getString("type");
		String failmech = this.getString("failmech");
		double workhour = this.getDouble("workhour");
		double boxunit = this.getDouble("boxunit");
		double boxteu = this.getDouble("boxteu");
		double electrickwh = this.getDouble("electrickwh");
		double oill = this.getDouble("oill");
		double worklock = this.getDouble("worklock");
		double control = this.getDouble("control");
		Date createdate = this.getDate("createdate");
		MboSetRemote assetSet = owner.getMboSet("$ASSET", "ASSET", "assetnum='" + eqnum + "'");
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			if ("UPPER".equals(type)) {// 换上
				asset.setValue("udeqnum", assetnum, 11L);
				asset.setValue("udcostcenter", udcostcenter, 11L);
			} else if ("LOWER".equals(type)) {// 换下
				asset.setValueNull("udeqnum", 11L);
			}

			MboSetRemote assetTstransSet = owner.getMboSet("UDASSETTSTRANS");
			MboRemote assetTstrans = assetTstransSet.add();
			assetTstrans.setValue("assetnum", eqnum, 11L);
			assetTstrans.setValue("eqnum", assetnum, 11L);
			if (!this.isNull("workhour")) {
				assetTstrans.setValue("workhour", workhour, 11L);
			}
			if (!this.isNull("boxunit")) {
				assetTstrans.setValue("boxunit", boxunit, 11L);
			}
			if (!this.isNull("boxteu")) {
				assetTstrans.setValue("boxteu", boxteu, 11L);
			}
			if (!this.isNull("electrickwh")) {
				assetTstrans.setValue("electrickwh", electrickwh, 11L);
			}
			if (!this.isNull("oill")) {
				assetTstrans.setValue("oill", oill, 11L);
			}
			if (!this.isNull("worklock")) {
				assetTstrans.setValue("worklock", worklock, 11L);
			}
			if (!this.isNull("control")) {
				assetTstrans.setValue("control", control, 11L);
			}
			assetTstrans.setValue("createdate", createdate, 11L);
			assetTstrans.setValue("type", type, 11L);
			assetTstrans.setValue("failmech", failmech, 11L);
		}
	}

	private void asset(MboRemote owner) throws RemoteException, MXException {

		MboSetRemote assetSet = this.getMbo().getMboSet("asset");
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			String assetnum = owner.getString("assetnum");
			String eqnum = this.getString("eqnum");
			String udcostcenter = owner.getString("udcostcenter");
			String type = this.getString("type");
			String failmech = this.getString("failmech");
			double workhour = this.getDouble("workhour");
			double boxunit = this.getDouble("boxunit");
			double boxteu = this.getDouble("boxteu");
			double electrickwh = this.getDouble("electrickwh");
			double oill = this.getDouble("oill");
			double worklock = this.getDouble("worklock");
			double control = this.getDouble("control");
			Date createdate = this.getDate("createdate");

			if ("UPPER".equals(type)) {// 换上
				asset.setValue("udeqnum", assetnum, 11L);
				asset.setValue("udcostcenter", udcostcenter, 11L);
			} else if ("LOWER".equals(type)) {// 换下
				asset.setValueNull("udeqnum", 11L);
			}

			MboSetRemote assetTstransSet = owner.getMboSet("UDASSETTSTRANS");
			MboRemote assetTstrans = assetTstransSet.add();
			assetTstrans.setValue("assetnum", eqnum, 11L);
			assetTstrans.setValue("eqnum", assetnum, 11L);
			if (!this.isNull("workhour")) {
				assetTstrans.setValue("workhour", workhour, 11L);
			}
			if (!this.isNull("boxunit")) {
				assetTstrans.setValue("boxunit", boxunit, 11L);
			}
			if (!this.isNull("boxteu")) {
				assetTstrans.setValue("boxteu", boxteu, 11L);
			}
			if (!this.isNull("electrickwh")) {
				assetTstrans.setValue("electrickwh", electrickwh, 11L);
			}
			if (!this.isNull("oill")) {
				assetTstrans.setValue("oill", oill, 11L);
			}
			if (!this.isNull("worklock")) {
				assetTstrans.setValue("worklock", worklock, 11L);
			}
			if (!this.isNull("control")) {
				assetTstrans.setValue("control", control, 11L);
			}
			assetTstrans.setValue("createdate", createdate, 11L);
			assetTstrans.setValue("type", type, 11L);
			assetTstrans.setValue("failmech", failmech, 11L);
		}
	}

	private void assetTs(MboRemote owner) throws RemoteException, MXException {
		String assetnum = owner.getString("assetnum");
		String eqnum = this.getString("eqnum");
		String udcostcenter = this.getString("asset.udcostcenter");
		String type = this.getString("type");
		String failmech = this.getString("failmech");
		double workhour = this.getDouble("workhour");
		double boxunit = this.getDouble("boxunit");
		double boxteu = this.getDouble("boxteu");
		double electrickwh = this.getDouble("electrickwh");
		double oill = this.getDouble("oill");
		double worklock = this.getDouble("worklock");
		double control = this.getDouble("control");
		Date createdate = this.getDate("createdate");
		if ("UPPER".equals(type)) {// 换上
			owner.setValue("udeqnum", eqnum, 11L);
			owner.setValue("udcostcenter", udcostcenter, 11L);
		} else if ("LOWER".equals(type)) {// 换下
			owner.setValueNull("udeqnum", 11L);
		}

		MboSetRemote assetTstransSet = owner.getMboSet("UDASSETTSTRANS");
		MboRemote assetTstrans = assetTstransSet.add();
		assetTstrans.setValue("assetnum", assetnum, 11L);
		assetTstrans.setValue("eqnum", eqnum, 11L);
		if (!this.isNull("workhour")) {
			assetTstrans.setValue("workhour", workhour, 11L);
		}
		if (!this.isNull("boxunit")) {
			assetTstrans.setValue("boxunit", boxunit, 11L);
		}
		if (!this.isNull("boxteu")) {
			assetTstrans.setValue("boxteu", boxteu, 11L);
		}
		if (!this.isNull("electrickwh")) {
			assetTstrans.setValue("electrickwh", electrickwh, 11L);
		}
		if (!this.isNull("oill")) {
			assetTstrans.setValue("oill", oill, 11L);
		}
		if (!this.isNull("worklock")) {
			assetTstrans.setValue("worklock", worklock, 11L);
		}
		if (!this.isNull("control")) {
			assetTstrans.setValue("control", control, 11L);
		}
		assetTstrans.setValue("createdate", createdate, 11L);
		assetTstrans.setValue("type", type, 11L);
		assetTstrans.setValue("failmech", failmech, 11L);
	}
}

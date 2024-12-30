package guide.webclient.beans.asset;

import java.rmi.RemoteException;

import psdi.app.asset.AssetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.asset.AssetAppBean;

public class UDAssetAppBean extends AssetAppBean {

	public void udupper() throws RemoteException, MXException {

		AssetRemote asset = (AssetRemote) this.getMbo();
		if (!asset.isNull("udeqnum")) {
			Object[] obj = { "温馨提示：已存在换上设备！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
	}

	public void udlower() throws RemoteException, MXException {

		AssetRemote asset = (AssetRemote) this.getMbo();
		if (asset.isNull("udeqnum")) {
			Object[] obj = { "温馨提示：不存在换下设备！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
	}
	
	public int UDAAA() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udzeetreesql = mbo.getString("udzeetreesql");
		if (udzeetreesql!=null && !udzeetreesql.equalsIgnoreCase("")) {
			MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
			assetSet.setWhere(udzeetreesql);
			assetSet.reset();
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				for (int i = 0; i < assetSet.count(); i++) {
					MboRemote asset = assetSet.getMbo(i);
					int udzeelevel = asset.getInt("udzeelevel"); //ASSETANCESTOR最高层级
					if (udzeelevel == 1) {
						MboSetRemote assetancestorSet = MXServer.getMXServer().getMboSet("ASSETANCESTOR", MXServer.getMXServer().getSystemUserInfo());
						assetancestorSet.setWhere("1=2");
						assetancestorSet.reset();
						MboRemote assetancestor = assetancestorSet.add();
						assetancestor.setValue("assetnum", asset.getString("assetnum"), 11L);
						assetancestor.setValue("ancestor", asset.getString("parent"), 11L);
						assetancestor.setValue("siteid", asset.getString("siteid"), 11L);
						assetancestor.setValue("orgid", asset.getString("orgid"), 11L);
						assetancestor.setValue("hierarchylevels", "1", 11L);
						assetancestorSet.save();
						assetancestorSet.close();
					} else if (udzeelevel == 2) {
						for (int j = 0; j < 2; j++) {
							MboSetRemote assetancestorSet = MXServer.getMXServer().getMboSet("ASSETANCESTOR", MXServer.getMXServer().getSystemUserInfo());
							assetancestorSet.setWhere("1=2");
							assetancestorSet.reset();
							MboRemote assetancestor = assetancestorSet.add();
							assetancestor.setValue("assetnum", asset.getString("assetnum"), 11L);
							if (j==0) {
								assetancestor.setValue("ancestor", asset.getString("parent"), 11L);
							} else if (j==1) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									assetancestor.setValue("ancestor", parent2, 11L);
								}
								assetSet2.close();
							}
							assetancestor.setValue("siteid", asset.getString("siteid"), 11L);
							assetancestor.setValue("orgid", asset.getString("orgid"), 11L);
							assetancestor.setValue("hierarchylevels", j+1, 11L);
							assetancestorSet.save();
							assetancestorSet.close();
						}
					} else if (udzeelevel == 3) {
						for (int j = 0; j < 3; j++) {
							MboSetRemote assetancestorSet = MXServer.getMXServer().getMboSet("ASSETANCESTOR", MXServer.getMXServer().getSystemUserInfo());
							assetancestorSet.setWhere("1=2");
							assetancestorSet.reset();
							MboRemote assetancestor = assetancestorSet.add();
							assetancestor.setValue("assetnum", asset.getString("assetnum"), 11L);
							if (j==0) {
								assetancestor.setValue("ancestor", asset.getString("parent"), 11L);
							} else if (j==1) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									assetancestor.setValue("ancestor", parent2, 11L);
								}
								assetSet2.close();
							} else if (j==2) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										assetancestor.setValue("ancestor", parent3, 11L);
									}
									assetSet3.close();
								}
								assetSet2.close();
							}
							assetancestor.setValue("siteid", asset.getString("siteid"), 11L);
							assetancestor.setValue("orgid", asset.getString("orgid"), 11L);
							assetancestor.setValue("hierarchylevels", j+1, 11L);
							assetancestorSet.save();
							assetancestorSet.close();
						}
					} else if (udzeelevel == 4) {
						for (int j = 0; j < 4; j++) {
							MboSetRemote assetancestorSet = MXServer.getMXServer().getMboSet("ASSETANCESTOR", MXServer.getMXServer().getSystemUserInfo());
							assetancestorSet.setWhere("1=2");
							assetancestorSet.reset();
							MboRemote assetancestor = assetancestorSet.add();
							assetancestor.setValue("assetnum", asset.getString("assetnum"), 11L);
							if (j==0) {
								assetancestor.setValue("ancestor", asset.getString("parent"), 11L);
							} else if (j==1) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									assetancestor.setValue("ancestor", parent2, 11L);
								}
								assetSet2.close();
							} else if (j==2) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										assetancestor.setValue("ancestor", parent3, 11L);
									}
									assetSet3.close();
								}
								assetSet2.close();
							} else if (j==3) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										MboSetRemote assetSet4 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
										assetSet4.setWhere("assetnum='"+parent3+"'");
										assetSet4.reset();
										if (!assetSet4.isEmpty() && assetSet4.count() > 0) {
											String parent4 = assetSet4.getMbo(0).getString("parent");
											assetancestor.setValue("ancestor", parent4, 11L);
										}
										assetSet4.close();
									}
									assetSet3.close();
								}
								assetSet2.close();
							}
							assetancestor.setValue("siteid", asset.getString("siteid"), 11L);
							assetancestor.setValue("orgid", asset.getString("orgid"), 11L);
							assetancestor.setValue("hierarchylevels", j+1, 11L);
							assetancestorSet.save();
							assetancestorSet.close();
						}
					} else if (udzeelevel == 5) {
						for (int j = 0; j < 5; j++) {
							MboSetRemote assetancestorSet = MXServer.getMXServer().getMboSet("ASSETANCESTOR", MXServer.getMXServer().getSystemUserInfo());
							assetancestorSet.setWhere("1=2");
							assetancestorSet.reset();
							MboRemote assetancestor = assetancestorSet.add();
							assetancestor.setValue("assetnum", asset.getString("assetnum"), 11L);
							if (j==0) {
								assetancestor.setValue("ancestor", asset.getString("parent"), 11L);
							} else if (j==1) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									assetancestor.setValue("ancestor", parent2, 11L);
								}
								assetSet2.close();
							} else if (j==2) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										assetancestor.setValue("ancestor", parent3, 11L);
									}
									assetSet3.close();
								}
								assetSet2.close();
							} else if (j==3) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										MboSetRemote assetSet4 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
										assetSet4.setWhere("assetnum='"+parent3+"'");
										assetSet4.reset();
										if (!assetSet4.isEmpty() && assetSet4.count() > 0) {
											String parent4 = assetSet4.getMbo(0).getString("parent");
											assetancestor.setValue("ancestor", parent4, 11L);
										}
										assetSet4.close();
									}
									assetSet3.close();
								}
								assetSet2.close();
							} else if (j==4) {
								MboSetRemote assetSet2 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
								assetSet2.setWhere("assetnum='"+asset.getString("parent")+"'");
								assetSet2.reset();
								if (!assetSet2.isEmpty() && assetSet2.count() > 0) {
									String parent2 = assetSet2.getMbo(0).getString("parent");
									MboSetRemote assetSet3 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
									assetSet3.setWhere("assetnum='"+parent2+"'");
									assetSet3.reset();
									if (!assetSet3.isEmpty() && assetSet3.count() > 0) {
										String parent3 = assetSet3.getMbo(0).getString("parent");
										MboSetRemote assetSet4 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
										assetSet4.setWhere("assetnum='"+parent3+"'");
										assetSet4.reset();
										if (!assetSet4.isEmpty() && assetSet4.count() > 0) {
											String parent4 = assetSet4.getMbo(0).getString("parent");
											MboSetRemote assetSet5 = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
											assetSet5.setWhere("assetnum='"+parent4+"'");
											assetSet5.reset();
											if (!assetSet5.isEmpty() && assetSet5.count() > 0) {
												String parent5 = assetSet5.getMbo(0).getString("parent");
												assetancestor.setValue("ancestor", parent5, 11L);
											}
											assetSet5.close();
										}
										assetSet4.close();
									}
									assetSet3.close();
								}
								assetSet2.close();
							}
							assetancestor.setValue("siteid", asset.getString("siteid"), 11L);
							assetancestor.setValue("orgid", asset.getString("orgid"), 11L);
							assetancestor.setValue("hierarchylevels", j+1, 11L);
							assetancestorSet.save();
							assetancestorSet.close();
						}
					}
				}
			}
			assetSet.close();
		}
		return 1;
	}
	
}

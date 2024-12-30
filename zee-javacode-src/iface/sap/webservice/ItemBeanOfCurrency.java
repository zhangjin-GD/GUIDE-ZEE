package guide.iface.sap.webservice;

public class ItemBeanOfCurrency {

	public String getITEMID() {
		return ITEMID;
	}

	public void setITEMID(String iTEMID) {
		ITEMID = iTEMID;
	}

	public String getKURST() {
		return KURST;
	}

	public void setKURST(String kURST) {
		KURST = kURST;
	}

	public String getGDATU() {
		return GDATU;
	}

	public void setGDATU(String gDATU) {
		GDATU = gDATU;
	}

	public String getFCURR() {
		return FCURR;
	}

	public void setFCURR(String fCURR) {
		FCURR = fCURR;
	}

	public String getTCURR() {
		return TCURR;
	}

	public void setTCURR(String tCURR) {
		TCURR = tCURR;
	}

	private String ITEMID;

    private String KURST;

    private String GDATU;

    private String FCURR;
    
    private String TCURR;

}

package guide.iface.sap.webservice;

public class ItemBean {

	private String ZSTOCKNO;
	
	private String ZSTOCKITEMNO;
	
	private String ZRETURN_CODE;
	
	private String ZITEMMSG;

	public String getZSTOCKNO() {
		return ZSTOCKNO;
	}

	public void setZSTOCKNO(String zSTOCKNO) {
		ZSTOCKNO = zSTOCKNO;
	}

	public String getZSTOCKITEMNO() {
		return ZSTOCKITEMNO;
	}

	public void setZSTOCKITEMNO(String zSTOCKITEMNO) {
		ZSTOCKITEMNO = zSTOCKITEMNO;
	}

	public String getZRETURN_CODE() {
		return ZRETURN_CODE;
	}

	public void setZRETURN_CODE(String zRETURN_CODE) {
		ZRETURN_CODE = zRETURN_CODE;
	}

	public String getZITEMMSG() {
		return ZITEMMSG;
	}

	public void setZITEMMSG(String zITEMMSG) {
		ZITEMMSG = zITEMMSG;
	}
}

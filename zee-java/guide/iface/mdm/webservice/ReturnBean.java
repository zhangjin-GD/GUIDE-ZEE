package guide.iface.mdm.webservice;

import java.util.List;
import java.util.Map;

public class ReturnBean {
	private Map<String,Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    private List<Map<String,Object>> list;
}

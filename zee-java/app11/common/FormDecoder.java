package guide.app.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FormDecoder {
	private Map<Object, Object> parameters;

	public FormDecoder(String str) {
		this.parameters = new HashMap<>();
		parse(this.parameters, str);
	}

	public String get(String key) {
		return this.get(key, null);
	}

	public String get(String key, String defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) ? defaultValue : value;
	}

	private int getInt32(String key) {
		return this.getInt32(key, 0);
	}

	private int getInt32(String key, int defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) || value.isEmpty() ? defaultValue : Integer.parseInt(value);
	}

	public long getInt64(String key) {
		return this.getInt64(key, 0L);
	}

	private long getInt64(String key, long defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) || value.isEmpty() ? defaultValue : Long.parseLong(value);
	}

	private float getFloat32(String key) {
		return this.getFloat32(key, 0F);
	}

	private float getFloat32(String key, float defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) || value.isEmpty() ? defaultValue : Float.parseFloat(value);
	}

	public double getFloat64(String key) {
		return this.getFloat64(key, 0D);
	}

	private double getFloat64(String key, double defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) || value.isEmpty() ? defaultValue : Double.parseDouble(value);
	}

	private boolean getBool(String key) {
		return this.getBool(key, false);
	}

	private boolean getBool(String key, boolean defaultValue) {
		String value = (String) this.parameters.get(key);
		return Objects.isNull(value) || value.isEmpty() ? defaultValue : Boolean.parseBoolean(value);
	}

	private boolean contains(String key) {
		return this.parameters.containsKey(key);
	}

	private static void parse(Map<Object, Object> map, String str) {
		if (Objects.isNull(str) || str.isEmpty())
			return;
		Arrays.stream(str.split("&")).filter(kv -> kv.contains("=")).map(kv -> kv.split("="))
				.forEach(array -> map.put(array[0], array[1]));
	}

	public static void main(String[] args) {
		String str = "userName=ZHANGYH&appName=UDPRMAT&recordId=1425";
		FormDecoder decoder = new FormDecoder(str);
//		System.out.println(decoder.contains("c"));
//		System.out.println(decoder.get("a"));
//		System.out.println(decoder.getInt32("a"));
//		System.out.println(decoder.getFloat32("b"));
//		System.out.println(decoder.getBool("c"));
		
		System.out.println(decoder.get("userName"));
		System.out.println(decoder.get("appName"));
		System.out.println(decoder.get("recordId"));
	}
}
package demo.helper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeixinParamsHelper {
	public static final Logger logger = LoggerFactory.getLogger(WeixinParamsHelper.class);

	public final static String SIGN_TYPE = "MD5";// 签名加密方式
	public final static String TRADE_TYPE = "APP";// 支付类型
	public final static String CHARSET = "UTF-8";

	/**
	 * 预支付交易单参数
	 * @param description
	 * @param price
	 * @param ip
	 * @param outTradeNo 
	 * @param APPID 
	 * @param MCH_ID 
	 * @param NOTIFY_URL 
	 * @return
	 */
	public static SortedMap<String, Object> prepareOrder(String description, long price, String ip, String outTradeNo, 
			String APPID, String MCH_ID, String NOTIFY_URL) {
		Map<String, Object> params = new HashMap<>();
		params.put("appid", APPID);			// 服务号的应用号
		params.put("body", description);		// 商品描述
		params.put("mch_id", MCH_ID);			// 商户号 
		params.put("nonce_str", RandomHelper.GenerateRandomStr(16));	// 16随机字符串(大小写字母加数字)
		params.put("out_trade_no", outTradeNo);// 商户订单号
		params.put("total_fee", price);		// 单位为分，不能传小数
		params.put("spbill_create_ip", ip);	// 客户端的真实IP地址
		params.put("notify_url", NOTIFY_URL); 	// 微信回调地址
		params.put("trade_type", TRADE_TYPE);	// 支付类型 app
		return sortMap(params);
	}

	/**
	 * 对map根据key进行ASCII排序 
	 * @param 无序的map
	 * @return
	 */
	public static SortedMap<String, Object> sortMap(Map<String, Object> map) {
		List<String> keys = new ArrayList<String>(map.keySet());
		keys.sort(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		// 排序后
		SortedMap<String, Object> sortmap = new TreeMap<String, Object>();
		for (String key: keys) {
			sortmap.put(key, map.get(key));
		}
		return sortmap;
	}
	
	/**
	 * APP端调起支付的参数列表
	 * @param prepayId
	 * @param APPID 
	 * @param MCH_ID 
	 * @param apiKey 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SortedMap<String, Object> clientOrder(String prepayId, String APPID, String MCH_ID, String apiKey) {
		// 获取微信返回的签名
		Map<String, Object> params = new HashMap<>();
		params.put("appid", APPID);
		params.put("noncestr", RandomHelper.GenerateRandomStr(16)); 	//可能必须要用prepareOrder生成的随机字符串
		params.put("package", "Sign=WXPay");
		params.put("partnerid", MCH_ID);
		params.put("prepayid", prepayId);
		params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));	 // 10 位时间戳
		SortedMap<String, Object> sortMap = sortMap(params);
		String paySign = createSign(sortMap, apiKey);
		sortMap.put("sign", paySign);
		return sortMap;
	}

	public static String returnXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code
				+ "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}

	/**
	 * 签名
	 * @param params
	 * @param API_KEY 
	 * @return
	 */
	public static String createSign(Map<String, Object> params, String API_KEY) {
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();//去掉带sign的项
			if (null != value && !"".equals(value) && !"sign".equals(key)
					&& !"key".equals(key)) {
				sb.append(key + "=" + value + "&");
			}
		}
		sb.append("key=" + API_KEY);
		try {
			return MD5Utils.getMD5String(sb.toString().getBytes(CHARSET)).toUpperCase();	//注意sign转为大写
		} catch (Exception e) {
			throw new RuntimeException("微信订单签名错误："+e.getMessage());
		}
	}

	public static boolean checkSign(String str, String apiKey) {
		Map<String, Object> map = XmlHelper.xmlToMap(str);
		Object sign = map.get("sign");
		if ("".equals(sign) || sign == null) {
			return false;
		}
		//清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
//		map.put("sign", "");
		map.remove("sign");
		SortedMap<String, Object> sortMap = sortMap(map);
		String newSign = createSign(sortMap, apiKey);
		if (!newSign.equals(sign)) {
			return false;
		}
		return true;
	}

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

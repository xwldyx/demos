package demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import demo.helper.HttpUtil;
import demo.helper.RandomHelper;
import demo.helper.WeixinConstant;
import demo.helper.WeixinParamsHelper;
import demo.helper.XmlHelper;
import demo.service.WeixinPayService;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
	private static final Logger logger = LoggerFactory.getLogger(WeixinPayServiceImpl.class);
	@Value("${wxpay.appid:wxc969123e7dtyf508}")
	public String APPID;														// 应用号
	@Value("${wxpay.app_secrect:4qfg712dg23318dfnh9f223f28459hj6}")
	public String APP_SECRECT;													// 应用密码
	@Value("${wxpay.mch_id:1945681231}")
	public String MCH_ID;														// 商户号 xxxx 公众号商户id
	@Value("${wxpay.app_key:b9e4330b317fe40284dbc518f70b9de6}")
	//	public final static String API_KEY = "sbyMJBo5370p7zbRP1ZN4hkLraLdmn8t";	// API密钥
	public String API_KEY;														// 沙箱API密钥
	@Value("${wxpay.notify_url:http://edu.bitby.cn/pay/weixin/asyncResult}")
	public String NOTIFY_URL;													// 异步通知地址

	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/unifiedorder";// 微信支付统一接口(POST)
	//	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 订单查询接口(POST)
	public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/orderquery";


	private Map<String, Object> getWeixinOrder(String description, long price, String ip, String outTradeNo) throws Exception{
		SortedMap<String, Object> orderParams = WeixinParamsHelper.prepareOrder(description, price, ip, outTradeNo, 
				this.APPID, this.MCH_ID, this.NOTIFY_URL);
		orderParams.put("sign",	WeixinParamsHelper.createSign(orderParams, this.API_KEY));// sign签名 key
		String requestXML = XmlHelper.getRequestXml(orderParams);// 生成xml格式字符串
		String responseStr = HttpUtil.httpsRequest(UNIFIED_ORDER_URL, "POST", requestXML);// 带上post
		// 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
		if (!WeixinParamsHelper.checkSign(responseStr, this.API_KEY)) {
			logger.error("微信统一下单失败, 签名可能被篡改：{} ", responseStr);
			throw new RuntimeException("下单失败，返回签名不正确");
		}
		Map<String, Object> resutlMap = XmlHelper.xmlToMap(responseStr);
		if (resutlMap != null && WeixinConstant.FAIL.equals(resutlMap.get("return_code"))) {
			logger.error("微信统一下单失败:{}", resutlMap.get("return_msg"));
			throw new RuntimeException("下单失败"+resutlMap.get("return_msg"));
		}
		// 获取到 prepayid
		// 商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再在APP里面调起支付。
		SortedMap<String, Object> map = WeixinParamsHelper.clientOrder(resutlMap.get("prepay_id").toString(), 
				this.APPID, this.MCH_ID, this.API_KEY);
		logger.info("统一下定单成功 "+map.toString());
		return map;
	}

	/**
	 * 微信回调告诉微信支付结果 注意：同样的通知可能会多次发送给此接口，注意处理重复的通知。
	 * 对于支付结果通知的内容做签名验证，防止数据泄漏导致出现“假通知”，造成资金损失。
	 * 
	 * @param params
	 * @return
	 */
	@Override
	public String asyncResult(HttpServletRequest request) {
		try {
			String responseStr = getContent(request);
			Map<String, Object> map = XmlHelper.xmlToMap(responseStr);
			logger.info("微信支付回调: "+map.toString());
			// 校验签名 防止数据泄漏导致出现“假通知”，造成资金损失
			if (WeixinParamsHelper.checkSign(responseStr, this.API_KEY)) {	//等重试
				logger.error("微信回调失败,签名可能被篡改 "+responseStr);
				return WeixinParamsHelper.returnXML(WeixinConstant.FAIL, "sign error");
			}
			String returnCode = String.valueOf(map.get("return_code"));
			if (WeixinConstant.SUCCESS.equalsIgnoreCase(returnCode)) {
				String resultCode = String.valueOf(map.get("result_code"));
				String tradeNo = String.valueOf(map.get("transaction_id"));
				String orderNumber = String.valueOf(map.get("out_trade_no"));
				if(WeixinConstant.SUCCESS.equalsIgnoreCase(resultCode)) {
					synchronized (this) {
					}
				}else if(WeixinConstant.FAIL.equalsIgnoreCase(resultCode)) {
					synchronized (this) {
					}
				}else {
					logger.error("微信回调result_code error：{}", resultCode);
					return WeixinParamsHelper.returnXML(WeixinConstant.FAIL, "result_code error");
				}
			}else {	//等重试
				logger.error("微信回调失败：{}", map.get("return_msg"));
				return WeixinParamsHelper.returnXML(WeixinConstant.FAIL, "callback fail");
			}
			return WeixinParamsHelper.returnXML(WeixinConstant.SUCCESS, "OK");	//成功
		} catch (Exception e) {
			logger.error("回调异常", e);
			return WeixinParamsHelper.returnXML(WeixinConstant.FAIL, "fail");
		}
	}

	/**
	 * 解析微信回调参数
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String getContent(HttpServletRequest request){
		String result = "";
		InputStream inStream = null;
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		try {
			inStream = request.getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			result = new String(outSteam.toByteArray(), "UTF-8");
		} catch (Exception e) {
			logger.error("获取微信回调内容异常：", e);
		}finally{
			try {
				if(outSteam != null){
					outSteam.close();
					outSteam = null; // help GC
				}
				if(inStream != null){
					inStream.close();
					inStream = null;// help GC
				}
			} catch (IOException e) {
				logger.error("微信回调 流关闭异常：{}", e.getMessage());
			}
		}
		return result;
	}

	@Override
	public String queryOrder(String transactionId, String out_trade_no) {
		Map<String, Object> params = new HashMap<>();
		params.put("appid", APPID);			// 服务号的应用号
		params.put("transaction_id", transactionId);		
		params.put("out_trade_no", out_trade_no);	
		params.put("mch_id", MCH_ID);			// 商户号 
		params.put("nonce_str", RandomHelper.GenerateRandomStr(16));	// 16随机字符串(大小写字母加数字)
		SortedMap<String, Object> sortMap = WeixinParamsHelper.sortMap(params);
		String paySign = WeixinParamsHelper.createSign(sortMap, this.API_KEY);
		sortMap.put("sign", paySign);
		String requestXML = XmlHelper.getRequestXml(sortMap);// 生成xml格式字符串
		String responseStr = null;
		try {
			responseStr = HttpUtil.httpsRequest(CHECK_ORDER_URL, "POST", requestXML);
		} catch (Exception e) {
			logger.error("微信查询订单请求异常", e);
		}
		// 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
		if (!WeixinParamsHelper.checkSign(responseStr, this.API_KEY)) {
			logger.error("微信查询订单失败, 签名可能被篡改：{} ", responseStr);
			throw new RuntimeException("微信查询订单失败，返回签名不正确");
		}
		Map<String, Object> resutlMap = XmlHelper.xmlToMap(responseStr);
		return resutlMap.toString();
	}


}

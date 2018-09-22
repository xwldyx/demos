package demo.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import demo.helper.AlipayConstant;
import demo.service.AlipayService;


@Service
public class AlipayServiceImpl implements AlipayService {
	private static Logger logger = LoggerFactory.getLogger(AlipayServiceImpl.class);
	private static final String FORMAT_JSON = AlipayConstants.FORMAT_JSON;
	private static final String CHARSET = AlipayConstants.CHARSET_UTF8;
	@Value("${alipay.sign_type}")
	private String RSA2 = AlipayConstants.SIGN_TYPE_RSA2;
	@Value("${alipay.app_id}")
	private String APP_ID = "";
	@Value("${alipay.app_private_key}")
	private String APP_PRIVATE_KEY = null;
	@Value("${alipay.alipay_public_key}")
	private String ALIPAY_PUBLIC_KEY = null;
	@Value("${alipay.alipay_gateway}")
	private String ALI_GATEWAY = "https://openapi.alipay.com/gateway.do";
	//获得初始化的AlipayClient
	private AlipayClient alipayClient = null; 


	@PostConstruct
	private void init() {
		alipayClient = new DefaultAlipayClient(ALI_GATEWAY, APP_ID, APP_PRIVATE_KEY, FORMAT_JSON, CHARSET, ALIPAY_PUBLIC_KEY, RSA2);
	}


	public String getAlipayOrder(String description, String outTradeNo, String totalAmout) throws AlipayApiException {
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody("不是必填的");				//交易具体描述
		model.setSubject(description);	//商品标题、订单关键字等
		model.setOutTradeNo(outTradeNo);
		model.setTimeoutExpress("30m");	//订单关闭时间。1m～15d。m-分钟，h-小时，d-天，1c-当天。若为空，则默认为15d
		model.setTotalAmount(totalAmout);
		model.setProductCode("QUICK_MSECURITY_PAY");	//商家和支付宝签约的产品码，为固定值
		request.setBizModel(model);
		request.setNotifyUrl("http://edu.bitby.cn/pay/ali/asyncResult");//商户外网可以访问的异步地址
		request.setReturnUrl("");
		//这里和普通的接口调用不同，使用的是sdkExecute
		AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
		logger.info(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		return response.getBody();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String asyncAlipayResult(ServletWebRequest request) {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			logger.info("阿里回调：name:{},value:{}", name, valueStr);
			//乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		String orderNumber = params.get("out_trade_no");
		String tradeNo = params.get("trade_no");
		logger.info("orderNumber={},tradeNo={}", orderNumber, tradeNo);
		try {
			boolean flag = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, RSA2);
			if(flag) {			//签名正确
				String tradeStatus = params.get("trade_status");
				if(AlipayConstant.TRADE_SUCCESS.equals(tradeStatus)) {	//交易成功
					synchronized (this) {
							//判断金额一致，完成订单
							logger.info("订单完成：orderNumber={},tradeNo={}", orderNumber, tradeNo);
					}
				}else if(AlipayConstant.TRADE_CLOSED.equals(tradeStatus)) {	//交易关闭
					synchronized (this) {
					}
				}else {
					logger.error("支付结果状态为：{}，orderNumber={}，tradeNo={}，请查明原因", tradeStatus, orderNumber, tradeNo);
					return "fail";
				}
			}else {
				logger.error("支付结果签名错误：orderNumber={}，tradeNo={}", orderNumber, tradeNo);
				return "fail";
			}
			return "success";
		} catch (AlipayApiException e) {
			logger.error("支付结果异常，orderId={}：", orderNumber, e);
			return "fail";
		}
	}

	//		out_trade_no	支付时传入的商户订单号，与trade_no必填一个
	//		trade_no	支付时返回的支付宝交易号，与out_trade_no必填一个
	@Override
	public String queryPayResult(String out_trade_no, String trade_no) {
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
		request.setBizContent("{" +
				"   \"out_trade_no\":\""+out_trade_no+"\"," +
				"   \"trade_no\":\""+trade_no+"\"" +
				"  }");//设置业务参数
		AlipayTradeQueryResponse response;
		try {
			response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
			logger.info("查询返回body={}", response.getBody());
		} catch (AlipayApiException e) {
			logger.error("支付宝查询请求异常，out_trade_no={}，trade_no={}：", out_trade_no, trade_no, e);
			throw new RuntimeException("支付宝查询请求异常");
		}
		//根据response中的结果继续业务逻辑处理
		return response.getBody();
	}
}

package demo.service;

import javax.servlet.http.HttpServletRequest;

public interface WeixinPayService {


	String asyncResult(HttpServletRequest request);
	
	String queryOrder(String transaction_id, String out_trade_no);

}

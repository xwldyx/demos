package demo.service;

import org.springframework.web.context.request.ServletWebRequest;

public interface AlipayService {

	String asyncAlipayResult(ServletWebRequest request);

	String queryPayResult(String out_trade_no, String trade_no);

}
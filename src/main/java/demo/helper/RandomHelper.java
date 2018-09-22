package demo.helper;

import java.util.Random;

public class RandomHelper {
	private static final Random rd = new Random();
	private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	/**
	 * 生成默认用户昵称nickName
	 * @return
	 */
	public static String GenerateNickName(){
		return "sf"+System.currentTimeMillis()+"R"+rd.nextInt(100);
	}
	/**
	 * 生成商户订单号
	 * @return
	 */
	public static String GenerateOrderNumber() {
		return System.currentTimeMillis()+"R"+rd.nextInt(100000);
	}
	
	/**
	 * n位长度随机字符串，取值为大小写字母和数字
	 * @param length
	 * @return
	 */
	public static String GenerateRandomStr(int length) {
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char achar = chars.charAt(rd.nextInt(chars.length() - 1));
			sb.append(achar);
		}
		return sb.toString();
	}
}

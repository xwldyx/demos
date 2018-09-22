package demo.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author lwx
 *
 */
public class MD5Utils {

	public static byte[] getMD5Bytes(byte[] content) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(content); 
		byte[] byteArr = md.digest(); 
		return byteArr;
	} 

	public static String getMD5String(byte[] content) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(content); 
		byte[] byteArr = md.digest(); 
		return byteToString(byteArr);
	} 

	public static String byteToString(byte[] byteArr) {
		int temp; 
		StringBuffer buf = new StringBuffer(""); 
		for (int offset = 0; offset < byteArr.length; offset++) { 
			temp = byteArr[offset]; 
			if (temp < 0) { 
				temp += 256; 
			} 
			if (temp < 16) { 
				buf.append("0"); 
			} 
			buf.append(Integer.toHexString(temp)); 
		} 
		return buf.toString(); 
	}
}

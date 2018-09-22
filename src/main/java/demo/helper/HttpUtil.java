package demo.helper;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {

	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @return
	 * @throws Exception
	 */
	public static String httpsRequest(String requestUrl, String requestMethod,
			String outputStr) throws Exception {
		HttpsURLConnection conn = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			URL url = new URL(requestUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("content-type",	"application/x-www-form-urlencoded");
			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (ConnectException ce) {
			log.error("连接超时：", ce);
			throw new RuntimeException("链接异常" + ce);
		} catch (Exception e) {
			log.error("https请求异常：", e);
			throw new RuntimeException("https请求异常" + e);
		}finally {
			if(bufferedReader != null)
				bufferedReader.close();
			if(inputStreamReader != null)
				inputStreamReader.close();
			if(inputStream != null)
				inputStream.close();
			if(conn != null)
				conn.disconnect();
		}
	}

}
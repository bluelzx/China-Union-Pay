package unionpayUtil.demo;


import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unionpayUtil.sdk.LogUtil;
import unionpayUtil.sdk.SDKConfig;
import unionpayUtil.sdk.SDKConstants;
import unionpayUtil.sdk.SDKUtil;

/**
 * 名称：商户前台�?知类 功能�?类属性： 方法调用 版本�?.0 日期�?014-07 作�?：中国银联ACP团队 版权：中国银�?
 * 说明：以下代码只是为了方便商户测试�?提供的样例代码，商户可以根据自己网站的需要，按照�?��文档编写,并非�?��要使用该代码。该代码仅供参�?�?
 * */

public class FrontRcvResponse extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4826029673018921502L;

	@Override
	public void init() throws ServletException {
		/**
		 * 参数初始�?
		 * 在java main 方式运行时必须每次都执行加载
		 * 如果是在web应用�?���?这个方写在可使用监听的方式写入缓�?无须在这出现
		 */
		SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		LogUtil.writeLog("FrontRcvResponse前台接收报文返回�?��");

		req.setCharacterEncoding("ISO-8859-1");
		String encoding = req.getParameter(SDKConstants.param_encoding);
		LogUtil.writeLog("返回报文中encoding=[" + encoding + "]");
		String pageResult = "";
		if ("UTF-8".equalsIgnoreCase(encoding)) {
			pageResult = "/utf8_result.jsp";
		} else {
			pageResult = "/gbk_result.jsp";
		}
		Map<String, String> respParam = getAllRequestParam(req);

		// 打印请求报文
		LogUtil.printRequestLog(respParam);

		Map<String, String> valideData = null;
		StringBuffer page = new StringBuffer();
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet()
					.iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				page.append("<tr><td width=\"30%\" align=\"right\">" + key
						+ "(" + key + ")</td><td>" + value + "</td></tr>");
				valideData.put(key, value);
			}
		}
		if (!SDKUtil.validate(valideData, encoding)) {
			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
			LogUtil.writeLog("验证签名结果[失败].");
		} else {
			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
			LogUtil.writeLog("验证签名结果[成功].");
			System.out.println(valideData.get("orderId")); //其他字段也可用类似方式获�?
		}
		req.setAttribute("result", page.toString());
		req.getRequestDispatcher(pageResult).forward(req, resp);

		LogUtil.writeLog("FrontRcvResponse前台接收报文返回结束");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上�?<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字�?
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}

}

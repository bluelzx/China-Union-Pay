package unionpayUtil.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import unionpayUtil.sdk.SDKConfig;

/**
 * 名称�?第一�?商户�?�?\5\6部分 跳转网关支付产品\手机控件支付产品\手机网页支付产品<br>
 * 功能�?6.6�?��件传输类交易<br>
 * 版本�?5.0<br>
 * 日期�?2014-07<br>
 * 作�?�?中国银联ACP团队<br>
 * 版权�?中国银联<br>
 * 说明：以下代码只是为了方便商户测试�?提供的样例代码，商户可以根据自己�?��，按照技术文档编写�?该代码仅供参考�?<br>
 */
public class Form_6_6_FileTransfer extends DemoBase {

	public static void main(String[] args) {


		/**
		 * 参数初始�?
		 * 在java main 方式运行时必须每次都执行加载
		 * 如果是在web应用�?���?这个方写在可使用监听的方式写入缓�?无须在这出现
		 */
		SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件

		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();
		// 版本�?
		data.put("version", "5.0.0");
		// 字符集编�?默认"UTF-8"
		data.put("encoding", "UTF-8");
		// 签名方法 01 RSA
		data.put("signMethod", "01");
		// 交易类型 
		data.put("txnType", "76");
		// 交易子类�?
		data.put("txnSubType", "01");
		// 业务类型
		data.put("bizType", "000000");
		// 接入类型，商户接入填0 0- 商户 �?1�?收单�?2：平台商�?
		data.put("accessType", "0");
		// 商户代码，请替换实际商户号测试，如使用的是自助化平台注册的商户号，该商户号没有权限测文件下载接口的，请使用测试参数里写的文件下载的商户号和日期测。如�?��实交易文件，请使用自助化平台下载文件�?
		data.put("merId", "700000000000001");
		// 清算日期
		data.put("settleDate", "0119");
		// 订单发�?时间，取系统时间
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 文件类型
		data.put("fileType", "00");

		data = signData(data);

		// 交易请求url 从配置文件读�?
		String url = SDKConfig.getConfig().getFileTransUrl();

		Map<String, String> resmap = submitUrl(data, url);

		System.out.println("请求报文=["+data.toString()+"]");
		System.out.println("应答报文=["+resmap.toString()+"]");

		// 解析返回报文的文件流
		deCodeFileContent(resmap);
	}

}

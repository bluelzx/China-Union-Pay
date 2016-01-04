package unionpayUtil.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import unionpayUtil.sdk.SDKConfig;

/**
 * 
 * 
 * 名称�?第一�?商户�?�?/6部分 手机支付 —�?跳转网关支付产品/手机网页支付产品<br>
 * 功能�?6.7.1�?��授权交易<br>
 * 前台交易�?br>
 * 版本�?5.0<br>
 * 日期�?2014-07<br>
 * 作�?�?中国银联ACP团队<br>
 * 版权�?中国银联<br>
 * 说明：以下代码只是为了方便商户测试�?提供的样例代码，商户可以根据自己�?��，按照技术文档编写�?该代码仅供参考�?<br>
 */
public class Form_6_7_1_AuthDeal_Front extends DemoBase {

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
		// 交易类型 01-消费
		data.put("txnType", "02");
		// 交易子类�?01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "01");
		// 业务类型
		data.put("bizType", "000201");
		// 渠道类型�?7-PC�?8-手机
		data.put("channelType", "08");
		// 前台通知地址 ，控件接入方式无作用
		data.put("frontUrl", "http://localhost:8080/ACPTest/acp_front_url.do");
		// 后台通知地址
		data.put("backUrl", "http://222.222.222.222:8080/ACPTest/acp_back_url.do");
		// 接入类型，商户接入填0 0- 商户 �?1�?收单�?2：平台商�?
		data.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		data.put("merId", "888888888888888");
		// 商户订单号，8-40位数字字�?
		data.put("orderId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 订单发�?时间，取系统时间
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 交易金额，单位分
		data.put("txnAmt", "1");
		// 交易币种
		data.put("currencyCode", "156");
		// 请求方保留域，�?传字段，查询、�?知�?对账文件中均会原样出�?
		// data.put("reqReserved", "透传信息");
		// 订单描述，可不上送，上�?时控件中会显示该信息
		// data.put("orderDesc", "订单描述");

		Map<String, String> submitFromData = signData(data);

		// 交易请求url 从配置文件读�?
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
		
		/**
		 * 创建表单
		 */
		String html = createHtml(requestFrontUrl, submitFromData);
		System.out.println(html);
	}

}

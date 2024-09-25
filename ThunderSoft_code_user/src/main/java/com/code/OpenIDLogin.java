package com.code;

import com.code.entity.OPenIdUserEntity;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.*;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cfn
 * @date: 2024/5/20 17:08
 * @description:
 */
public class OpenIDLogin {

	public static final ConsumerManager manager = new ConsumerManager();
	public static final String GOOGLE_ENDPOINT = "http://test.cn/accounts/openid";
	public static final String RETURN_TOURL = "http://www.test.cn:38080/rest/verifyOpenId";
	public static final String RETURN_TOURL_web = "http://www.test.cn:38080/openIdLogin";

	/**
	 * openId登录
	 *
	 * @return
	 * @throws DiscoveryException
	 * @throws MessageException
	 * @throws ConsumerException
	 * @throws IOException
	 */
	@NaslLogic
	public static String openIdLogin() throws DiscoveryException, MessageException, ConsumerException, IOException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		List discoveries = manager.discover(GOOGLE_ENDPOINT);
		DiscoveryInformation discovered = manager.associate(discoveries);
		request.getSession().setAttribute("openid-disc", discovered);
		AuthRequest authReq = manager.authenticate(discovered, RETURN_TOURL);
		// attribute Exchange
		FetchRequest fetch = FetchRequest.createFetchRequest();
		fetch.addAttribute("email", "http://test.net/test", true);
		fetch.addAttribute("fullname", "http://test.net/test", true);
		fetch.addAttribute("id", "http://test.net/test", true);
		// attach the extension to the authentication request
		authReq.addExtension(fetch);
		if (!discovered.isVersion2()) {
			response.sendRedirect(authReq.getDestinationUrl(true));
		} else {
			// Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)
			response.sendRedirect(authReq.getDestinationUrl(true));
		}
		return null;
	}

	/**
	 * 解析返回登录用户信息接口
	 *
	 * @return oPenIdUserEntity openid用户实例
	 */
	@NaslLogic
	public static OPenIdUserEntity verifyOpenIdResponse() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse responses =
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		try {
			ParameterList response = new ParameterList(request.getParameterMap());
			List<String> list = new ArrayList<>();
			list.add("openid.op_endpoint");
			list.add("openid.claimed_id");
			list.add("openid.identity");
			list.stream().forEach(x -> {
				String parameterValue = response.getParameterValue(x);
				String replace = parameterValue.replace("https", "http");
				Parameter parameter = new Parameter(x, replace);
				response.set(parameter);
			});
			DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute("openid-disc");
			StringBuffer receivingURL = request.getRequestURL();
			String queryString = request.getQueryString();
			if (queryString != null && queryString.length() > 0) {
				receivingURL.append("?").append(request.getQueryString());
			}
			VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);
			AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
			if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
				//返回的用户信息
				FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
				OPenIdUserEntity oPenIdUserEntity = new OPenIdUserEntity();
				List emails = fetchResp.getAttributeValues("email");
				oPenIdUserEntity.setEmail((String) emails.get(0));
				List id = fetchResp.getAttributeValues("id");
				oPenIdUserEntity.setId((String) id.get(0));
				List fullnames = fetchResp.getAttributeValues("fullname");
				oPenIdUserEntity.setFullname((String) fullnames.get(0));
				responses.sendRedirect(RETURN_TOURL_web + "?" + "id=" + id.get(0));
				return oPenIdUserEntity;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

}

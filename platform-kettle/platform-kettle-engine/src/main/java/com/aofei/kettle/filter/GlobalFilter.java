package com.aofei.kettle.filter;

import com.aofei.kettle.utils.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {

		if(res instanceof HttpServletResponse) {
			HttpServletResponse response = (HttpServletResponse) res;
			JsonUtils.put(response);

			HttpServletRequest request = (HttpServletRequest) req;
	        JsonUtils.put(request);

	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization");
	        response.setHeader("Access-Control-Allow-Credentials", "true");

		}

		fc.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {

	}
}

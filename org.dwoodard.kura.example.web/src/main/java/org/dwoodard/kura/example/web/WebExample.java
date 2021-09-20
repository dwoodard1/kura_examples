package org.dwoodard.kura.example.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kura.audit.AuditContext;
import org.eclipse.kura.system.SystemService;
import org.eclipse.kura.web.api.Console;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebExample extends HttpServlet {

	private static final long serialVersionUID = 8114997088279049102L;

	private static final Logger logger = LoggerFactory.getLogger(WebExample.class);

	private HttpService httpService;
	private SystemService systemService;
	
	private Console console;
	
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
	public void unsetHttpService(HttpService httpService) {
		this.httpService = null;
	}
	
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
	
	public void unsetSystemService(SystemService systemService) {
		this.systemService = null;
	}
	
	public void setConsole(final Console console) {
        this.console = console;
    }
	
	public void unsetConsole(final Console console) {
		this.console = null;
	}
	
	public void activate() {
		logger.info("Activating {}", this.getClass().getSimpleName());
		
		try {
			this.console.registerSecuredServlet("/web", this);

		} catch (NamespaceException | ServletException e) {
			logger.error("Activate error! {}", e);
		}
	}
	
	public void deactivate() {
		logger.info("Deactivating {}", this.getClass().getSimpleName());
		try {
			this.console.unregisterServlet("/web");
		} catch (NamespaceException | ServletException e) {
			logger.error("Activate error! {}", e);
		}
	}
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqPathInfo = req.getPathInfo();

        if (reqPathInfo == null) {
            logger.error("Request path info not found");
            throw new ServletException("Request path info not found");
        }

        logger.debug("req.getRequestURI(): {}", req.getRequestURI());
        logger.debug("req.getRequestURL(): {}", req.getRequestURL());
        logger.debug("req.getPathInfo(): {}", req.getPathInfo());

        if (reqPathInfo.startsWith("/app")) {
        		
        		AuditContext audit = this.console.initAuditContext(req);
        		audit.getProperties().forEach((key,value) -> {
        			logger.info("key:value => {}:{}", key, value);
        		});
        		
        		if (!"admin".equals(audit.getProperties().get("identity"))) {
        			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        			resp.sendRedirect("/admin/auth");
        			return;
        		}
        		resp.setContentType("text/html");
        		byte[] buf = new byte[8192];
        		ServletOutputStream out = resp.getOutputStream();
        		InputStream source = this.getClass().getResource("/www/index.html").openStream();
        		int length;
        		while ((length = source.read(buf)) > 0) {
        	        out.write(buf, 0, length);
        	    }
        		
        } else if (reqPathInfo.startsWith("/getInfo/status")) {
        	
            resp.getWriter().write(this.systemService.getKuraVersion());
            
        } else {
            logger.error("Unknown request path info: {}", reqPathInfo);
            throw new ServletException("Unknown request path info: " + reqPathInfo);
        }
    }
	
}

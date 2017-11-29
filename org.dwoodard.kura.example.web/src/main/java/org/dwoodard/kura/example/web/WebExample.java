package org.dwoodard.kura.example.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kura.system.SystemService;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebExample extends HttpServlet {

	private static final long serialVersionUID = 8114997088279049102L;

	private static final Logger logger = LoggerFactory.getLogger(WebExample.class);

	private HttpService httpService;
	private SystemService systemService;
	
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
	
	public void activate() {
		logger.info("Activating {}", this.getClass().getSimpleName());
		try {
			this.httpService.registerResources("/web", "/www", null);
			this.httpService.registerServlet("/web/getInfo", this, null, null);
		} catch (NamespaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deactivate() {
		logger.info("Deactivating {}", this.getClass().getSimpleName());
		this.httpService.unregister("/web/getInfo");
		this.httpService.unregister("/web");
	}
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        String reqPathInfo = req.getPathInfo();

        if (reqPathInfo == null) {
            logger.error("Request path info not found");
            throw new ServletException("Request path info not found");
        }

        logger.debug("req.getRequestURI(): {}", req.getRequestURI());
        logger.debug("req.getRequestURL(): {}", req.getRequestURL());
        logger.debug("req.getPathInfo(): {}", req.getPathInfo());

        if (reqPathInfo.startsWith("/status")) {
            resp.getWriter().write(this.systemService.getKuraVersion());
        } else {
            logger.error("Unknown request path info: " + reqPathInfo);
            throw new ServletException("Unknown request path info: " + reqPathInfo);
        }
    }
	
}

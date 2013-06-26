package eu.dime.ps.controllers.service.util;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

public class ApplicationContextCustomLoader implements ApplicationContextAware {

  private WebApplicationContext ctx;

  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    this.ctx = (WebApplicationContext)ctx;
  }
	
  public ServletContext getContext() {
    ServletContext servletContext = ctx.getServletContext();
    return servletContext;
  }
}

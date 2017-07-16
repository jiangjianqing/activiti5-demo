package common.web.utils;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.h2.util.New;
import org.mvel2.ast.NewObjectNode.NewObjectArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import common.web.utils.*;
import common.web.model.AuthenticationUser;

public class SystemIntegrationHelper extends AbstractHelperClass {

	//由AbstractHelperClass提供的静态类方法支持函数，必须放在子类中
	protected final static String getStaticClassName(){
			return new Object() {
				//静态方法中获取当前类名
				public String getClassName() {
					String className = this.getClass().getName();
					return className.substring(0, className.lastIndexOf('$'));
				}
			}.getClassName();
		}
		
	protected final static Logger logger = LoggerFactory
				.getLogger(getStaticClassName());
	//------------------static 方法模板定义结束---------------------	
	
	private static IdentityService getIdentityService(){
		return SpringContextHolder.getBean("identityService");
	};

	/**
	 * 与activiti集成，主要是设置当前线程上的注册用户，没错，就是当前线程：
	 * 在Activiti中,IdentityService中提供了SetAuthenticatedUserId方法用于将用户ID设置到当前的线程中,
	 * 最终调用ThreadLocal的set方法.具体的代码如下:
	 * identityService.setAuthenticatedUserId(userId);
	 */
	private static void integrateActiviti(){
		
		if (SessionHelper.isAuthenticated()) {
			AuthenticationUser user = SessionHelper.getAuthenticatedUser();
			logger.debug("用户已经注册，执行activiti integrate 动作！");
			getIdentityService().setAuthenticatedUserId(user.getId().toString());	
		}	
	}
	
	public static void whenAuthenticated(){
		integrateActiviti();
	}
	
	public static void whenServletRequest(){
		integrateActiviti();
	}
}
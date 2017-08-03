package common.web.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import common.db.base.exception.DaoException;
import common.db.model.log.SessionLog;
import common.db.repository.jpa.log.SessionLogDao;
import common.service.utils.AbstractHelperClass;
import common.service.utils.SpringContextHolder;
import common.web.service.SessionEvent;
import common.web.service.SessionLogger;
import common.web.utils.SessionHelper;

public class SessionLoggerImpl extends AbstractHelperClass implements SessionLogger {

	private final String sessionAttrName = "sessionLogInfo";
	
	private SessionLogDao sessionLogDao;


	public SessionLogDao getSessionLogDao() {
		return sessionLogDao;
	}

	public void setSessionLogDao(SessionLogDao sessionLogDao) {
		this.sessionLogDao = sessionLogDao;
	}

	@Override
	public void onLogin(HttpSession session) {
		if (sessionLogDao == null) {
			logger.error("没有发现sessionLogDao,无法记录会话日志");
			return;
		}
		SessionLog info = new SessionLog();
		info.setLoginTime(new Date());
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		info.setIpAddr(request.getRemoteAddr());
		info.setHostName(request.getRemoteHost());
		info.setCreateTime(new Date(session.getCreationTime()));
		info.setSessionId(session.getId());
		info.setUserId(SessionHelper.getAuthenticatedUser().getId());
		
		try {
			sessionLogDao.create(info);
			session.setAttribute(sessionAttrName, info);
			logger.warn(String.format("创建sessionlog,id=%d", info.getId()));
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onLogout(HttpSession session) {
		if (sessionLogDao == null) {
			logger.error("没有发现sessionLogDao,无法记录会话日志");
			return;
		}

		SessionLog info = (SessionLog) session.getAttribute(sessionAttrName);
		try {
			info.setLogoutTime(new Date());
			sessionLogDao.update(info);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
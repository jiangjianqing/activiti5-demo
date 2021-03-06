package identity.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.pagehelper.PageInfo;

import common.db.base.exception.*;
import common.db.base.jpa.AbstractJpaDao;
import common.db.base.test.AbstractJpaTestCase;
import common.db.model.identity.Role;
import common.db.model.identity.User;
import common.db.repository.identity.RoleDao;
import common.db.repository.identity.UserDAO;
import common.db.repository.identity.jpa.impl.RoleDaoImpl;
import common.db.repository.identity.jpa.impl.UserDaoImpl;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdentityJpaTest  extends AbstractJpaTestCase {
	
	protected static UserDAO userDao;
	protected static RoleDao roleService;
	
	private static String newUserName="测试人员123456";
	private static String newRoleName="经理";
	private static String newPermissionName="user:delete";
	
	@BeforeClass
	public static void initServices() {
		setPersistenceUnitName("identity");
		
		userDao=new UserDaoImpl();
		((AbstractJpaDao)userDao).setEntityManager(em);
		//userDao.setEntityManager(em);

		roleService=new RoleDaoImpl();
		((AbstractJpaDao)roleService).setEntityManager(em);
		//roleService.setEntityManager(em);
	}
	
	
	@Test
	public void A1_testSysUserAdd() throws DaoException, OutOfPageRangeException
	{
		PageInfo<User> page=userDao.getPageList(1);
		System.out.println("测试用户新增");
		User newUser=userDao.selectByPrimaryKey(new Long(1));

		newUser=new User();
		newUser.setUserName(newUserName);

		newUser.setPassword("123456");
		newUser.setSalt("tt");
		userDao.insert(newUser);
		System.out.println("newUser.id="+newUser.getId());

		newUser=userDao.findByUserName(newUserName);
		assertTrue(newUser!=null);
	}
	
	@Test
	public void A1_testSysRoleAdd() throws DaoException
	{
		System.out.println("测试角色新增");
		Role newRole=new Role();
		newRole.setName(newRoleName);
		roleService.insert(newRole);		
		System.out.println("newRole.id="+newRole.getId());
	}

	@Test
	public void A2_testUserRoleAssociate() throws DaoException, CloneNotSupportedException
	{

		System.out.println("测试用户和角色关联");

		User newUser=userDao.findByUserName(newUserName);
		
		System.out.println("用户拥有的角色数量:"+newUser.getRoles().size());
		
		Role newRole=roleService.selectByPrimaryKey(new Long(1));
		
		System.out.println("newUser="+newUser);
		if (newUser.getRoles() == null){
			newUser.setRoles(new ArrayList<Role>());
		}
		newUser.getRoles().add(newRole);

	}
	
	@Test
	public void A3_testRolePermissionAssociate() throws DaoException {
		
		User newUser=userDao.findByUserName(newUserName);		
		System.out.println("用户拥有的角色数量:"+newUser.getRoles().size());
		assertTrue(newUser.getRoles().size() == 1);
	}

	@Ignore
	@Test
	public void ignoreTest()
	{

	}

}


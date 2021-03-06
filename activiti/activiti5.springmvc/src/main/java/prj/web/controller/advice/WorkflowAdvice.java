package prj.web.controller.advice;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import common.security.AuthenticationUser;
import common.service.utils.AbstractHelperClass;
import common.web.controller.AbstractViewController;
import common.web.utils.SessionHelper;
import workflow.activiti.utils.ActivitiUtil;

/**
 * 处理所有视图controller异常
 * 注意：
 * 	以下@ExceptionHandler方法处理都禁止标注@ResponseBody  //重要：如果不使用@ResponseBody标注，将返回一个逻辑视图名  
 * @author jjq
 *
 *basePackages用于指定对哪些包里的Controller起作用,无法使用通配符
 *所以使用assignableTypes来代替（也可以使用annotations 实现同样功能）
 */
@ControllerAdvice(assignableTypes = {AbstractViewController.class}) 
public class WorkflowAdvice extends AbstractHelperClass {
	

	@Autowired
	private TaskService taskService;

	//用于自动绑定前台请求参数到Model中。
    @InitBinder  
    public void initBinder(WebDataBinder binder) {  
    	
    	//// 此处仅演示忽略request中的参数id
    	//binder.setDisallowedFields("id");
    	//用到的场景非常少,目前没有测试过
    	//System.out.println("============应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器"); 
    }
    
    /**
     * 
     */
  	@ModelAttribute("tasks")
    public List<Task> addTasks() {  
  		AuthenticationUser user=(AuthenticationUser)SessionHelper.getAuthenticatedUser();

		//activiti5.16以后提供的方法，一步就可以获取上述两种任务
  		if(user!=null) {
  			//2017.08.27 注意StartUserId为空的情况，会导致任务找不到
  			//List<Task> allTasks=taskService.createTaskQuery().taskCandidateOrAssigned(user.getUsername()).list();
  			//读取直接分配给当前用户或已经签收的任务
  			List<Task> doingTasks=taskService.createTaskQuery().taskAssignee(user.getUsername()).list();
  			//等待签收的任务
  			List<Task> wattingClaimTasks=taskService.createTaskQuery().taskCandidateUser(user.getUsername()).list();
  			//除了包含taskAssignee，还包含owner 
  			List<Task> involvedTasks = taskService.createTaskQuery().taskInvolvedUser(user.getUsername()).list();
  			//合并两种任务
  			List<Task> allTasks=new ArrayList<Task>();
  			//allTasks.addAll(doingTasks);
  			allTasks.addAll(wattingClaimTasks);
  			allTasks.addAll(involvedTasks);
  			System.out.println("allTasks.size="+allTasks.size());
  			return allTasks; 
  		}else {
  			return null;
  		}
		

    }
  	
  	@ModelAttribute("executions")
  	public List<Execution> addExecutions() {  
  		AuthenticationUser user=(AuthenticationUser)SessionHelper.getAuthenticatedUser();
  		if(user!=null) {
  			return ActivitiUtil.getExecutionListByUserId(user.getUserName());
  		}else {
  			return null;
  		}
  	}

}


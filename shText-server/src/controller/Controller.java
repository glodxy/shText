package controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Controller {
    ApplicationContext context=new ClassPathXmlApplicationContext("dbcontroller/applicationContext.xml");
    static final String ERROR_SESSIOON_TIMEOUT="user is offline";
    static final String ERROR_LOGIN="account or password error";
    static final String ERROR_ACCOUNT_EXIST="account exists";
    static final String ERROR_DEFAULT="system error";
    static final String ERROR_USER_PERMISSION="user has no permission";
    static final String ERROR_USER_NOTEXIST="user not exist";
}

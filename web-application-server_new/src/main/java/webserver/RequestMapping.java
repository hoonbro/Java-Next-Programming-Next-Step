package webserver;

import Controller.Controller;
import Controller.CreateUserController;
import Controller.LoginController;
import Controller.ListUserController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * RequestMapping
 *
 * @author : jihoon
 * @date : 2023/07/29
 * @version 1.0.0
 * @description : 요청 URL에 맞는 Ctonrller를 연결하는 클래스
 *                웹 애플리케이션에서 서비스하는 모든 URL과 Controller를 관리하며, URL에 해당하는 Controller를 반환
 *
**/
public class RequestMapping {
    private static Map<String, Controller> controllerMap = new HashMap<>();

    static{
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl){
        return controllerMap.get(requestUrl);
    }
}

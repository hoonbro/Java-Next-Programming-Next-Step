package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * LoginController
 *
 * @author : jihoon
 * @date : 2023/07/29
 * @version 1.0.0
 * @description : HttpRequest의 login 메소드 Controller로 분리
 *
**/
public class LoginController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/login_failed.html");
            }
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}

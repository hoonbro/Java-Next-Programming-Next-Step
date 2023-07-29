package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * CreateUserController
 *
 * @author : jihoon
 * @date : 2023/07/29
 * @version 1.0.0
 * @description : HttpRequest의 createUser 메소드 Controller로 분리
 *
**/
public class CreateUserController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);


    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"),
                request.getParameter("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }
}

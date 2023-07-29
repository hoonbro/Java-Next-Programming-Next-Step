package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 *
 * ListUserController
 *
 * @author : jihoon
 * @date : 2023/07/29
 * @version 1.0.0
 * @description : HttpRequest의 userList 메소드 Controller로 분리
 *
**/
public class ListUserController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);


    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.isLogin(request.getHeader("Cookie"))) {
            response.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        response.forwardBody(sb.toString());
    }
}

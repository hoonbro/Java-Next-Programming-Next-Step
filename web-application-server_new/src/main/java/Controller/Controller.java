package Controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 *
 * Controller
 *
 * @author : jihoon
 * @date : 2023/07/29
 * @version 1.0.0
 * @description : RequstHandler의 run메서드의 복잡도를 해소하기 위한 인터페이스
 *                기존에는 새로운 기능이 추가될 때마다 새로운 else if절이 추가되어 OCP(개방폐쇄원칙)에 위배됨
 *                새로운 기능이 추가되거나 수정사항이 발생하더라도 변환의 범위를 최소화 하기 위한 Controller
 *
**/
public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}

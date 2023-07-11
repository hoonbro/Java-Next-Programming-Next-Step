package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

/*
 * HTTP Request를 분석하는 클래스
 * HttpRequest 클래스의 processRequestLine 메서드를 클래스로 분리해 책임을 분리했지만
 * HttpRequest 클래스의 변경없이 테스트가 가능하다.
 */
class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;

    private String path;

    private Map<String, String> params = new HashMap<>();

    RequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");

        if(tokens.length != 3){
            throw new IllegalArgumentException(requestLine + "이 request 형식에 맞지 않습니다.");
        }

        method = HttpMethod.valueOf(tokens[0]);
        if(method.isPost()){
            path = tokens[1];
            return;
        }

        int idx = tokens[1].indexOf("?");
        if(idx == -1){
            path = tokens[1];
        }else{
            path = tokens[1].substring(0, idx);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(idx+1));
        }
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    Map<String, String> getParams() {
        return params;
    }
}

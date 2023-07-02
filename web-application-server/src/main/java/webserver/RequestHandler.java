package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {

    private final String HOME = "/index.html";
    private final String LOGIN = "/login.html";
    private final String LOGINFAILED = "/login_failed.html";
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = br.readLine();

            if(isLineNull(line)){
                return;
            }

            String url  = getUrl(line);

            int idx = url.indexOf("?");
            String httpMethod = getHttpMethod(line);
            String requestTarget = getRequestTarget(url, idx);

            log.debug("Client Request : {}", line);
            log.debug("httpMethod : {}", httpMethod);
            log.debug("requestTarget : {}", requestTarget);

            Map<String, String> header = new HashMap<>();
            while(!"".equals(line)){
                line=br.readLine();
                String []tokens=line.split(": ");

                if(tokens.length==2){
                    header.put(tokens[0],tokens[1]);
                }
            }

            DataOutputStream dos = new DataOutputStream(out);

            if(httpMethod.equals("GET")) {
                Map<String, String> queryString = getQueryString(url, idx);
                Collection<User> userList;

                if (requestTarget.equals("/user/create")) {
                    User newUser = new User(queryString);

                    log.debug("Create User : {}", newUser);

                    DataBase.addUser(newUser);
                    response302Header(dos, HOME);
                }else if(requestTarget.equals("/user/list")){
                    Map<String, String> cookies = HttpRequestUtils.parseCookies(header.get("Cookie"));

                    if(!Boolean.parseBoolean(cookies.get("logined"))){
                        response302Header(dos, "/user" + LOGIN);
                    }

                    userList = DataBase.findAll();
                    StringBuilder sb = new StringBuilder();

                    int i = 3;

                    System.out.println("size = " + userList.size());

                    sb.append("<tr>");
                    for(User user : userList) {
                        System.out.println(user.toString());
                        sb.append("<th scope=\"row\">"+ i++ +"</th><td>"+user.getUserId()+"</td> <td>"+user.getName()+"</td> <td>"+user.getEmail()+"</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td></tr>");
                    }

                    System.out.println(sb.toString());

                    url = "/user/list.html";

                    String fileData = new String(Files.readAllBytes(new File("./webapp" + url).toPath()) );
                    fileData = fileData.replace("%userList%", URLDecoder.decode(sb.toString(), "UTF-8"));

                    byte[] body = fileData.getBytes();
                    response200Header(dos, "/user/list.html");
                    responseBody(dos, body);
                    return;
                }
                else{
                    response200Header(dos, url);
                }

                //요청 URL에 해당하는 파일을 webapp 디렉토리에서읽어전달하면된다.
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                responseBody(dos, body);
            }

            else if(httpMethod.equals("POST")){
                if (requestTarget.equals("/user/create")) {
                    String requestBody = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));

                    Map<String, String> param = getParams(requestBody);

                    User newUser = new User(param);

                    log.debug("Create User : {}", newUser);

                    DataBase.addUser(newUser);
                    response302Header(dos, HOME);
                }else if(requestTarget.equals("/user/login")){
                    String requestBody = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                    log.debug("requestBody : {}", requestBody);

                    Map<String, String> params = getParams(requestBody);
                    User user = DataBase.findUserById(params.get("userId"));

                    if(user == null){
                        log.debug("존재하지 않는 사용자 아이디 : {}", params.get("userId"));
                        response302Header(dos, "logined=false", LOGINFAILED);
                    }else if(user.getPassword().equals(params.get("password"))){
                        log.debug("사용자 로그인 : {}", params.get("userId"));
                        response302Header(dos, "logined=true", HOME);
                    }else{
                        log.debug("잘못된 비밀번호 입력");
                        response302Header(dos, "logined=false", LOGINFAILED);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location : "+ redirectUrl +"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl){
        try{
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location : "+ redirectUrl +"\r\n");
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String cookie, String redirectUrl){
        try{
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location : "+ redirectUrl +"\r\n");
            dos.writeBytes("Set-Cookie : " + cookie + "\r\n");
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getHttpMethod(String line){
        return line.split(" ")[0];
    }

    private String getUrl(String line){
        return line.split(" ")[1];
    }

    private String getRequestTarget(String url, int idx){
        return idx < 0 ? url : url.substring(0, idx);
    }

    private Map<String, String> getQueryString(String url, int idx){
        return HttpRequestUtils.parseQueryString(url.substring(idx+1));
    }

    private Map<String, String> getParams(String requestBody){
        return HttpRequestUtils.parseQueryString(requestBody);
    }

    private boolean isLineNull(String line){
        return line == null;
    }

}

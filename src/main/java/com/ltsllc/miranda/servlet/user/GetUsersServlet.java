package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/11/2017.
 */
public class GetUsersServlet extends UserServlet {
    public ResultObject createResultObject() {
        return new UserListResultObject();
    }

    public ResultObject basicService(HttpServletRequest req, HttpServletResponse resp, UserRequestObject requestObject)
            throws IOException, ServletException, TimeoutException {
        UserListResultObject userListResultObject = new UserListResultObject();

        List<User> users = UserHolder.getInstance().getUsers();
        userListResultObject.setResult(Results.Success);
        userListResultObject.setUserList(users);

        return userListResultObject;
    }
}

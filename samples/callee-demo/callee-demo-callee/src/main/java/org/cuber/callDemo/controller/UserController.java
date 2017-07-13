package org.cuber.callDemo.controller;

import org.cuber.callDemo.caller.UserCaller;
import org.cuber.callDemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by cuber on 2017/7/12.
 */
@Controller
public class UserController {

    @Autowired
    private UserCaller userCaller;

    @RequestMapping("/getBro.json")
    public @ResponseBody User getBro(User user){
        return userCaller.getUserBro(user);
    }
}

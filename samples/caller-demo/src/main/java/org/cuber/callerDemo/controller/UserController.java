package org.cuber.callerDemo.controller;

import org.cuber.callDemo.caller.UserCaller;
import org.cuber.callDemo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by cuber on 2017/7/13.
 */
@Controller
public class UserController {

    @Resource(name = "userCaller")
    private UserCaller userCaller;

    @RequestMapping("/getBro.json")
    public @ResponseBody User getBro(){
        User user  = new User();
        user.setAge(4);
        user.setName("cuber");
        return userCaller.getUserBro(user);
    }
}

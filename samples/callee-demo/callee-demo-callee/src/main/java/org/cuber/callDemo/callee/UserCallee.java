package org.cuber.callDemo.callee;

import org.cuber.call.callee.annotation.Callee;
import org.cuber.callDemo.caller.UserCaller;
import org.cuber.callDemo.model.User;

/**
 * Created by cuber on 2017/7/12.
 */
@Callee
public class UserCallee implements UserCaller{

    @Override
    public User getUserBro(User user) {

        user.setName(user.getName() + "'" + "s bro");
        user.setAge(user.getAge() + 1);
        return user;
    }
}

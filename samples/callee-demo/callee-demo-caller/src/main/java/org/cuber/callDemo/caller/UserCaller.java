package org.cuber.callDemo.caller;

import org.cuber.call.caller.annotation.Caller;
import org.cuber.callDemo.model.User;

/**
 * Created by cuber on 2017/7/12.
 */
@Caller
public interface UserCaller {
    User getUserBro(User user);
}

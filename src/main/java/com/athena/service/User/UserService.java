package com.athena.service.User;

import com.athena.datasource.jdbc.dao.UserDao;
import com.athena.datasource.jdbc.po.User;
import com.athena.vo.AddUserReq;
import com.athena.vo.AddUserResp;
import com.athena.vo.GetUserResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public AddUserResp addUser(AddUserReq addUserReq) {
        User user = new User();
        user.setUsername(addUserReq.getUsername());
        user.setPassword(addUserReq.getPassword());
        user.setEmail(addUserReq.getEmail());
        user.setPhone(addUserReq.getPhone());
        user.setEnabled(addUserReq.isEnabled());
        User userSaved = userDao.save(user);

        AddUserResp addUserResp = new AddUserResp();

        addUserResp.setId(userSaved.getId());
        addUserResp.setUsername(userSaved.getUsername());
        addUserResp.setPassword(userSaved.getPassword());
        addUserResp.setEmail(userSaved.getEmail());
        addUserResp.setPhone(userSaved.getPhone());
        addUserResp.setEnabled(userSaved.isEnabled());
        addUserResp.setTest("test^_____^");
        return addUserResp;

    }

    public GetUserResp getUser(Integer id) {
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isPresent()) {
            GetUserResp getUserResp = new GetUserResp();
            getUserResp.setId(userOpt.get().getId());
            getUserResp.setUsername(userOpt.get().getUsername());
            getUserResp.setPassword(userOpt.get().getPassword());
            getUserResp.setEmail(userOpt.get().getEmail());
            getUserResp.setPhone(userOpt.get().getPhone());
            getUserResp.setEnabled(userOpt.get().isEnabled());
            return getUserResp;
        }
        return null;
    }
}

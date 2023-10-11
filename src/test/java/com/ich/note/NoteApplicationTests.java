package com.ich.note;

import com.ich.note.dao.IUserDao;
import com.ich.note.pojo.User;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.ich.note.pojo.table.Tables.USER;

@SpringBootTest(classes = NoteApplication.class)
class NoteApplicationTests {

    @Autowired
    private IUserDao userDao; // 关于用户的数据库接口

    @Test
    void contextLoads() {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(USER.EMAIL.eq("315105654@qq.com"));

        User user = userDao.selectOneByQuery(wrapper);

        System.out.println(user);
    }

}

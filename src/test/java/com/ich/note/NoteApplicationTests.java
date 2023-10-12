package com.ich.note;

import cn.hutool.extra.mail.MailUtil;
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
        MailUtil.send(
                "*****@qq.com", // 收件人
                "测试邮箱发送标题", // 标题
                "测试邮箱发送<b>内容</b>", // 内容
                true // 是否为 html 格式解析
        );
    }

}

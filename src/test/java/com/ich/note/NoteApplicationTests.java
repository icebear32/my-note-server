package com.ich.note;

import cn.hutool.extra.mail.MailUtil;
import com.ich.note.dao.IUserDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.User;
import com.ich.note.service.IMailService;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.ich.note.pojo.table.Tables.USER;

@SpringBootTest(classes = NoteApplication.class)
class NoteApplicationTests {

    @Autowired
    private IMailService mailService;

    @Test
    void contextLoads() throws ServiceException {
        String emailRegisterVC =  mailService.getEmailRegisterVC("315105654@qq.com");
        System.out.println();
    }

}

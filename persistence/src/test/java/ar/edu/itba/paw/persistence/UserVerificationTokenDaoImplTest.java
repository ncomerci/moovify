package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserVerificationTokenDaoImplTest {

        private static final long USER_ID = 1L;
        @Autowired
        private UserVerificationTokenDaoImpl userVerificationTokenDao;

        @Autowired
        private DataSource ds;

        private JdbcTemplate jdbcTemplate;

        private SimpleJdbcInsert tokenInsert;

        @Before
        public void testSetUp() {
            this.jdbcTemplate = new JdbcTemplate(ds);
            this.tokenInsert = new SimpleJdbcInsert(ds)
                    .withTableName(UserVerificationToken.TABLE_NAME)
                    .usingGeneratedKeyColumns("token_id");
        }

        @Test
        public void createPasswordResetToken() {

            JdbcTestUtils.deleteFromTables(jdbcTemplate, UserVerificationToken.TABLE_NAME);

            userVerificationTokenDao.createVerificationToken(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now().plusDays(1),
                    Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock());

            final int count = JdbcTestUtils.countRowsInTableWhere(
                    jdbcTemplate,
                    UserVerificationToken.TABLE_NAME,
                    String.format("user_id = %d", USER_ID));

            Assert.assertEquals(1, count);
        }


        @Test
        public void testDeletePasswordResetToken() {

            Map<String, Object> map = new HashMap<>();
            map.put("user_id", USER_ID);
            map.put("token", UUID.randomUUID());
            map.put("expiry", Timestamp.valueOf(LocalDateTime.now().plusDays(1)));

            tokenInsert.execute(map);

            userVerificationTokenDao.deleteVerificationToken(
                    Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock());

            final int count = JdbcTestUtils.countRowsInTableWhere(
                    jdbcTemplate,
                    UserVerificationToken.TABLE_NAME,
                    String.format("user_id = %d", USER_ID));

            Assert.assertEquals(0, count);
        }
    }
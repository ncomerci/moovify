//package ar.edu.itba.paw.persistence;
//
//import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
//import ar.edu.itba.paw.models.PostCategory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Optional;
//
//
//public class PostCategoryJdbcDaoImpl implements PostCategoryDao {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(PostCategoryDaoImpl.class);
//
//    private static final String POST_CATEGORY = PostCategory.TABLE_NAME;
//
//    public static final RowMapper<PostCategory> POST_CATEGORY_ROW_MAPPER = (rs, rowNum) ->
//            new PostCategory(rs.getLong("category_id"), rs.getObject("creation_date", LocalDateTime.class),
//                    rs.getString("name"));
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public PostCategoryDaoImpl(final DataSource ds){
//        jdbcTemplate = new JdbcTemplate(ds);
//    }
//
//    @Override
//    public Optional<PostCategory> findPostCategoryById(long id) {
//
//        LOGGER.info("Find Post Category By ID {}", id);
//        return jdbcTemplate.query("SELECT * FROM " + POST_CATEGORY + " WHERE " + POST_CATEGORY + ".category_id = ?",
//                new Object[]{ id }, POST_CATEGORY_ROW_MAPPER).stream().findFirst();
//    }
//
//    @Override
//    public Collection<PostCategory> getAllPostCategories() {
//
//        LOGGER.info("Get All Post Categories");
//        return jdbcTemplate.query(
//                "SELECT * FROM " + POST_CATEGORY, POST_CATEGORY_ROW_MAPPER);
//    }
//}

package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.models.MovieCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;

@Repository
public class MovieCategoryDaoImp implements MovieCategoryDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieCategoryDaoImp.class);

    private static final String MOVIE_CATEGORIES = TableNames.MOVIE_CATEGORIES.getTableName();


    private static final RowMapper<MovieCategory> ROW_MAPPER = (rs, rowNum) -> new MovieCategory(
            rs.getLong("category_id"),
            rs.getLong("tmdb_category_id"),
            rs.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovieCategoryDaoImp(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Collection<MovieCategory> findCategoriesByTmdbId(Collection<Long> categories) {

        final StringBuilder whereBuilder = new StringBuilder().append(" WHERE ").append(MOVIE_CATEGORIES).append(".tmdb_category_id IN (");
        final Object[] categoryArray = categories.toArray();

        for (int i = 0; i < categoryArray.length - 1; i++)
            whereBuilder.append("?, ");

        whereBuilder.append("?)");

        LOGGER.info("Find Movie Categories by Tmdb Id {}", categories);
        LOGGER.debug("Find Movie Categories Where: {}", whereBuilder.toString());

        return jdbcTemplate.query("SELECT * FROM " + MOVIE_CATEGORIES + whereBuilder.toString(),
                categoryArray, ROW_MAPPER);
    }

    @Override
    public Collection<MovieCategory> getAllCategories() {

        LOGGER.info("Get All Movie Categories");
        return jdbcTemplate.query("SELECT * FROM " + MOVIE_CATEGORIES, ROW_MAPPER);
    }
}

package ar.edu.itba.paw.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@ComponentScan({
        "ar.edu.itba.paw.persistence",
})
@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:/test-config.properties" })
public class TestConfig {

    @Autowired
    private Environment env;

    @Value("classpath:clean-up.sql")
    private Resource cleanUp;

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Value("classpath:test_inserts.sql")
    private Resource testInserts;

    @Bean
    public DataSource dataSource(){
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.postgresql.Driver.class);
        ds.setUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));

        return ds;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {

        final DataSourceInitializer dsi = new DataSourceInitializer();

        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(databasePopulator());
        dsi.setDatabaseCleaner(databaseCleaner());

        return dsi;
    }

    private DatabasePopulator databaseCleaner() {

        final ResourceDatabasePopulator dbp = new ResourceDatabasePopulator();
        dbp.addScripts(cleanUp);
        return dbp;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final DataSource ds) {

        return new DataSourceTransactionManager(ds);
    }

    private DatabasePopulator databasePopulator() {

        final ResourceDatabasePopulator dbp = new ResourceDatabasePopulator();
        dbp.addScripts(schemaSql, testInserts);
        return dbp;
    }
}

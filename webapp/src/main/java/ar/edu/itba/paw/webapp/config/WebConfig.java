package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@EnableWebMvc
@ComponentScan({
        "ar.edu.itba.paw.webapp.controller",
        "ar.edu.itba.paw.services",
        "ar.edu.itba.paw.persistence",
    })
@Configuration
// TODO: Set Up @PropertySource
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Value("classpath:data.sql")
    private Resource dataSql;

    @Bean
    public ViewResolver viewResolver(){
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setViewClass(JstlView.class); // Es el default, pero lo aclaramos
        viewResolver.setSuffix(".jsp");
        viewResolver.setPrefix("/WEB-INF/jsp/");

        return viewResolver;
    }

    @Bean(name = "applicationBasePath")
    public String applicationBasePath() {
        return "localhost:8080";
        // return "http://pawserver.it.itba.edu.ar/paw-2020b-3";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @Bean
    public DataSource dataSource(){
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.postgresql.Driver.class);
        ds.setUrl("jdbc:postgresql://localhost/moovify");
        ds.setUsername("admin");
        ds.setPassword("papanata");

        // FOR DEPLOYMENT
//        ds.setUrl("jdbc:postgresql://10.16.1.110/paw-2020b-3");
//        ds.setUsername("paw-2020b-3");
//        ds.setPassword("ce3Xh7mfS");

        return ds;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {
        final DataSourceInitializer dsi = new DataSourceInitializer();

        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(databasePopulator());

        return dsi;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator dbp = new ResourceDatabasePopulator();

        dbp.addScripts(schemaSql, dataSql);

        return dbp;
    }

    @Bean
    public MessageSource messageSource(){
        final ReloadableResourceBundleMessageSource msgSource = new ReloadableResourceBundleMessageSource();

        msgSource.setBasename("classpath:i18n/messages");
        msgSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        msgSource.setCacheSeconds(10);

        return msgSource;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();

        multipartResolver.setMaxUploadSize(1000000);
        multipartResolver.setMaxUploadSizePerFile(1000000);

        return multipartResolver;
    }

    @Bean
    public JavaMailSender mailSender() {

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Deploy Mail Server
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("MoovifyCo@gmail.com");
//        mailSender.setPassword("#Papanata");

        // Development Mail Server
        mailSender.setHost("smtp.mailtrap.io");
        mailSender.setPort(587);
        mailSender.setUsername("e6c828d95e9959");
        mailSender.setPassword("bbfccd607177ac");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
package cz.muni.pb138.annotationsystem.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Configuration
@PropertySource("classpath:dataSource.properties")
@ComponentScan(basePackages = {
        "cz.muni.pb138.annotationsystem.backend.api",
        "cz.muni.pb138.annotationsystem.backend.business",
        "cz.muni.pb138.annotationsystem.backend.dao"})
public class BackendConfig {

    @Inject
    private Environment env;

    @Bean
    public DataSource dataSource() {
        /*
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
        dataSource.setUsername(env.getRequiredProperty("dataSource.username"));
        dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
        */
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.DERBY).build();
        return db;
    }

}

package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${VCAP_SERVICES}") String vcapServicesJson)  {
        return new DatabaseServiceCredentials(vcapServicesJson);
    }

    @Bean
    public HikariDataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource hkdataSource = new HikariDataSource();
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        hkdataSource.setDataSource(dataSource);
        return hkdataSource;
    }

    @Bean
    public HikariDataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource hkdataSource = new HikariDataSource();
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        hkdataSource.setDataSource(dataSource);
        return hkdataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter () {

//    Set it up with MYSQL database type.
//    Set the database platform to "org.hibernate.dialect.MySQL5Dialect".
//    Enable DDL Generation.

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);

        return adapter;

    }


    @Bean
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(HikariDataSource moviesDataSource, HibernateJpaVendorAdapter moviesHibernate){

//        sets the data source
//        sets the Jpa Vendor adapter
//        sets the packages to scan to the package of the repository (using setPackagesToScan)
//        sets a persistence unit name unique to each database

        LocalContainerEntityManagerFactoryBean moviesLCEMFB = new LocalContainerEntityManagerFactoryBean();

        moviesLCEMFB.setDataSource(moviesDataSource);
        moviesLCEMFB.setJpaVendorAdapter(moviesHibernate);
        moviesLCEMFB.setPackagesToScan("org.superbiz.moviefun");
        moviesLCEMFB.setPersistenceUnitName("moviesPersistance");

        return moviesLCEMFB;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(HikariDataSource albumsDataSource, HibernateJpaVendorAdapter albumsHibernate){

        LocalContainerEntityManagerFactoryBean albumsLCEMFB = new LocalContainerEntityManagerFactoryBean();

        albumsLCEMFB.setDataSource(albumsDataSource);
        albumsLCEMFB.setJpaVendorAdapter(albumsHibernate);
        albumsLCEMFB.setPackagesToScan("org.superbiz.moviefun");
        albumsLCEMFB.setPersistenceUnitName("albumsPersistance");

        return albumsLCEMFB;

    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory moviesLocalContainerEntityManagerFactoryBean) {

        JpaTransactionManager moviesJpaTransactionManager = new JpaTransactionManager(moviesLocalContainerEntityManagerFactoryBean);
        return moviesJpaTransactionManager;

    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumsLocalContainerEntityManagerFactoryBean) {

        JpaTransactionManager albumsJpaTransactionManager = new JpaTransactionManager(albumsLocalContainerEntityManagerFactoryBean);
        return albumsJpaTransactionManager;

    }

    @Bean
    public TransactionOperations moviesTransactionOperations(PlatformTransactionManager moviesPlatformTransactionManager) {
       return new TransactionTemplate(moviesPlatformTransactionManager);

    }

    @Bean
    public TransactionOperations albumsTransactionOperations(PlatformTransactionManager albumsPlatformTransactionManager) {
        return new TransactionTemplate(albumsPlatformTransactionManager);

    }


}

package ca.mcgill.ecse321.treeple;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.*;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

@SpringBootApplication
public class TreePLESpringApplication extends SpringBootServletInitializer {

    @Autowired
    private Environment environment;
    public static Environment env;
    private SQLiteJDBC sql;

    public static void main(String[] args) {
        SpringApplication.run(TreePLESpringApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        // Let the model matcher map corresponding fields by name
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
        modelMapper.getConfiguration().setSourceNamingConvention(NamingConventions.NONE).setDestinationNamingConvention(NamingConventions.NONE);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    // Enable CORS globally
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // For debug purposes, allow connecting from localhost as well
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowedOrigins(
                            "http://treeple-web.herokuapp.com",
                            "https://treeple-web.herokuapp.com",
                            "http://localhost:5000",
                            "https://localhost:5000",
                            "http://127.0.0.1:5000",
                            "https://127.0.0.1:5000");
            }
        };
    }

    @Bean
    @EventListener(ApplicationEnvironmentPreparedEvent.class)
    public SQLiteJDBC modelIdCountInitializer() {
        sql = new SQLiteJDBC();
        sql.connect();
        Tree.setNextTreeId(sql.getMaxTreeId() + 1);
        Location.setNextLocationId(sql.getMaxLocationId() + 1);
        SurveyReport.setNextReportId(sql.getMaxReportId() + 1);
        Forecast.setNextForecastId(sql.getMaxForecastId() + 1);
        return sql;
    }

    @PostConstruct
    public void initEnv() {
        env = environment;
    }

    @PreDestroy
    public void closeSQLite() {
        if (sql != null)
            sql.closeConnection();
    }
}

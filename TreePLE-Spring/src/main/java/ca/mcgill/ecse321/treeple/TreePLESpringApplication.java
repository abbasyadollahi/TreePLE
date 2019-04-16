package ca.mcgill.ecse321.treeple;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.persistence.TreePLEPSQL;

@SpringBootApplication
public class TreePLESpringApplication {

    @Autowired
    private TreePLEPSQL sql;

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

    @PostConstruct
    public void initializeSQL() {
        Tree.setNextTreeId(sql.getMaxTreeId() + 1);
        Location.setNextLocationId(sql.getMaxLocationId() + 1);
        SurveyReport.setNextReportId(sql.getMaxReportId() + 1);
        Forecast.setNextForecastId(sql.getMaxForecastId() + 1);
    }

    @PreDestroy
    public void closeTreePLEPSQL() {
        if (sql != null)
            sql.closeConnection();
    }
}

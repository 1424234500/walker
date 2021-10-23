package com.walker;

import com.walker.core.annotation.WalkerJdbcScan;
import com.walker.spring.ImportBeanConfig;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ServletComponentScan
@Import(ImportBeanConfig.class)
@WalkerJdbcScan("com.walker.mapper")
//@MapperScan("com.walker.mapper")
public class ApplicationWeb {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ApplicationWeb.class, args);
        }catch (UnsatisfiedDependencyException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}

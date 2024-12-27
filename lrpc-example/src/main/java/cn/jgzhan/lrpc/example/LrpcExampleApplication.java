package cn.jgzhan.lrpc.example;

import cn.jgzhan.lrpc.springboot.annotation.LrpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@LrpcScan
@AutoConfiguration
public class LrpcExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LrpcExampleApplication.class, args);
	}

}

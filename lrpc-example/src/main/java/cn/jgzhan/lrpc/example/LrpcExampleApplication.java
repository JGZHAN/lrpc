package cn.jgzhan.lrpc;

import cn.jgzhan.lrpc.annotation.LrpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@LrpcScan
public class LrpcExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LrpcExampleApplication.class, args);
	}

}

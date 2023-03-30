package davis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1. Act as main class for spring boot application
 * 2. Also implements CommandLineRunner, so that code within run method
 * is executed before application startup but after all beans are effectively created
 * @author hemant
 *
 */
@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringBootConsoleApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("EXECUTING : command line runner");
        String[] files = {
            // "/home/ilya/davis_sender/2.txt"
            "/home/ilya/davis_sender/02_06_2022.txt",
            "/home/ilya/davis_sender/05_07_2022.txt",
            "/home/ilya/davis_sender/12_11_2022.txt"
        };
            
        for (int i = 0; i < files.length; ++i) {
        
            try {
                Path path = Paths.get(files[i]);
                try (Stream<String> lines = Files.lines(path)){
                    lines.forEach(r->{
                        try {
                            CloseableHttpClient httpclient = HttpClients.createDefault();
                            HttpPost httppost = new HttpPost("http://62.84.112.40:8082/davis/raw");
                            HttpClientContext context = HttpClientContext.create();
                            LOG.info("loading: " + r);
                            StringEntity entity = new StringEntity(r, ContentType.create("text/plain", "UTF-8"));  
                            httppost.setEntity(entity);
                            CloseableHttpResponse response = httpclient.execute(httppost, context);
                            try {
                                HttpEntity rentity = response.getEntity();
                                if (rentity != null) {
                                    InputStream inputStream = rentity.getContent();
                                    try {
                                        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                                        LOG.info("result: " + result);
                                    } finally {
                                        inputStream.close();
                                    }
                                }
                            } finally {
                                response.close();
                            }
                            httpclient.close();
                        } catch (IOException ioe){
                            LOG.error(ioe.getMessage());
                        }
                        });
                    
                    LOG.info("files[{}]: {}", i, files[i]);
                }
            } catch (Exception e){
                LOG.error(e.getMessage());
            }
        }
	}
}
    // @Bean
    // CommandLineRunner initDatabase(DavisRepository repository) {

    //   return args -> {

    //     try {
    //         Path path = Paths.get(getClass().getClassLoader().getResource("/home/ilya/davis_sender/test.data").toURI());
    //         try (Stream<String> lines = Files.lines(path)){
    //             lines.forEach(r->log.info("Preloading " + repository.save(new Davis(r))));
    //         }
    //     } catch (Exception e){
    //         log.info(e.getMessage());
    //     }
    //   };
    // }
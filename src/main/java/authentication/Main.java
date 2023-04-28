package authentication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.bson.types.Binary;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.web.SecurityFilterChain;

import com.bebracore.productswatching.model.Product;
import com.bebracore.productswatching.service.FetchingProductsService;
import com.bebracore.productswatching.service.InfoType;
import com.bebracore.productswatching.service.ProductService;
import com.bebracore.productswatching.service.SortType;

import model.Review;
import parser.MvideoParser;
import parser.Type;

@SpringBootApplication(scanBasePackages = { "com.bebracore.mongoconfig", "authentication", "com.bebracore.cabinet",
		"com.bebracore.productswatching", "com.bebracore.review", "com.bebracore.chat" })
@EnableMongoRepositories(basePackages = { "com.bebracore.productswatching.dao", "com.bebracore.review.repository",
		"com.bebracore.chat.repository", "com.bebracore.cabinet.repository", "authentication.repository" })
public class Main {
	public static void main(String[] args) throws MalformedURLException, IOException {
		ApplicationContext ac = SpringApplication.run(Main.class, args);
		String[] names = ac.getBeanDefinitionNames();
		System.out.println();
//		String hex = Hex.encodeHexString(new Binary(
//				new URL("https://static.eldorado.ru/photos/mv/Big/30059399bb5.jpg").openStream().readAllBytes())
//				.getData());
//		System.out.println(hex);
//		FetchingProductsService serv = ac.getBean(FetchingProductsService.class);
//		serv.fetchResources("https://www.mvideo.ru/bff/products/listing", Type.MVIDEO, InfoType.LONG);
//		serv.fetchResources("https://www.eldorado.ru/c/smartfony", Type.ELDORADO, InfoType.SHORT);

//		ProductService serv = ac.getBean(ProductService.class);
//		List<Product> pr = serv.getSmartphones(null, null, 1500, null, null, null, null, null, null, SortType.CHEAP,
//				null);
//		FetchingProductsService f = ac.getBean(FetchingProductsService.class);
//
//		MvideoParser parser = new MvideoParser();
////
//		for (int i = 0; i < pr.size(); i++) {
//			pr.get(i).getResources().forEach(res -> {
//				List<Review> reviews = f.fetchReviews(res);
//				if (reviews != null && res.getType().equals(Type.MVIDEO)) {
//					res.getReviews().clear();
//					res.addReviews(reviews);
//					System.out.println(reviews);
//				}
//			});
//			System.out.println("page is " + i);
//		}
//		serv.save(pr);
//
//		parser.finishWebDriver();

//		System.out.println(pr);

		SecurityFilterChain sfc = ac.getBean(SecurityFilterChain.class);
		Object p = sfc.getFilters();
		System.out.println();
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

	public static String[] getUniqueInfo(String text) {
		Pattern p = Pattern.compile(
				"(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*NFC)) ([^\\s]*) (\\w*[/+]?\\w*(GB|Gb|)?) (?<color>.*$)");
		Matcher m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group("color");

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*GB)) ([^\\s]*) (.+?(?= \\()) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);
			if (part6.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part6 = m.group(5);
			}

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*TB)) ([^\\s]*) (.+?(?= \\()) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);
			if (part6.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part6 = m.group(5);
			}

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*GB)) (.+?(?= \\()) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);
			if (part6.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part6 = m.group(5);
			}

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*TB)) (.+?(?= \\()) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);
			if (part6.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part6 = m.group(5);
			}

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (\\w*[/+]\\w*Gb) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (\\w*[/+]\\w*Tb) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*Gb)) (.+?(?= \\()) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);
			if (part5.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part5 = m.group(6);
			}

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*Tb)) (.+?(?= \\()) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);
			if (part5.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part5 = m.group(6);
			}

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*Gb)) ([^\\s]*) (.+?(?= \\()) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);
			if (part5.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part5 = m.group(6);
			}

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*Tb)) ([^\\s]*) (.+?(?= \\()) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);
			if (part5.matches("^\\([\\w-/ ]*\\)[ а-я]*$")) {
				part5 = m.group(6);
			}

			return new String[] { part2, part3, part5 };
		}

		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*GB)) ([^\\s]*) (\\d+\\w*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*/?\\w*TB)) ([^\\s]*) (\\d+\\w*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part6 = m.group(6);

			return new String[] { part2, part3, part6 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*GB)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*TB)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*Gb)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*Tb)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\(\\d+[/+]?\\d+\\))) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\d+[/+]?\\d+)) ([^\\s]*) (.+?(?= \\()) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\w*[/+]?\\w*G)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\()) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		p = Pattern.compile("(^Смартфон) ([^\\s]*) (.+?(?= \\d+[/+]?\\d+)) ([^\\s]*) (.*$)");
		m = p.matcher(text);
		if (m.matches()) {
			String part2 = m.group(2);
			String part3 = m.group(3);
			String part5 = m.group(5);

			return new String[] { part2, part3, part5 };
		}
		System.out.println("Couldn't parse " + text);
		return null;
	}
}

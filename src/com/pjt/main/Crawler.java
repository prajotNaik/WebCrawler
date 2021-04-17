package com.pjt.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	static String baseURL = "https://www.magicbricks.com/project-dosti-imperia-for-sale-in-thane-pppfs";
	static Map<String, String> records = new ConcurrentHashMap<>();
	static BlockingQueue<String> processingList = new ArrayBlockingQueue<String>(8000);
	static List<String> ignoreURLs = new ArrayList<>();
	static FileWriter fw;
	public static void main(String[] args) throws IOException, InterruptedException {
		fw = new FileWriter(new File("Out.txt"));
		Crawler c = new Crawler();
		ignoreURLs.add("twitter".toLowerCase());
		ignoreURLs.add("facebook".toLowerCase());
		ignoreURLs.add("google".toLowerCase());
		ignoreURLs.add("mailto");
		
		ThreadPoolExecutor tpe = (ThreadPoolExecutor)Executors.newFixedThreadPool(20);
		processingList.offer(baseURL, 60, TimeUnit.SECONDS);
		//c.processURL("https://www.wikipedia.org/");
		//"https://www.msci.com/"
		
		while(!stop) {
			String url = null;
			if((url=processingList.poll(60, TimeUnit.SECONDS)) == null) {
				stop = true;
			}
			tpe.execute(c.new CrawlerThread(url));
		}
		
		System.out.println("Pending processing ....");
		System.out.println(processingList.size());
		
		//tpe.awaitTermination(1, TimeUnit.HOURS);
		tpe.shutdown();
		System.out.println(records);
	}
	static volatile boolean stop = false;
	public class CrawlerThread implements Runnable{
		
		public CrawlerThread(String uRL) {
			super();
			URL = uRL;
		}
		private String URL;
		@Override
		public void run() {
			processURL(URL);
		}
	}
	public void processURL(String URL) {
		if(checkToIgnoreURL(URL)) {
			return;
		}
		Document doc = null;
		try {
			System.out.println(Thread.currentThread() + ", Processing ... " + URL);
			doc = Jsoup.connect(URL).get();
			//System.out.println(doc.text());
		} catch (Exception e) {
			records.put(URL.toLowerCase(), "ERROR PARSING");
			System.err.println("Error Parsin .... " + URL + ", error : " + e.getMessage());
			return;
		}
		if(doc.text().toLowerCase().contains("dosti")) {
			records.put(URL.toLowerCase(), "YES");
			Elements allElements = doc.getAllElements();
			allElements.stream().filter(a -> a.text().toLowerCase().contains("dosti")).forEach(a -> {
				try {
					fw.write(a.text());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					try {
						fw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				
			});
			//int mallya = doc.text().toLowerCase().indexOf("dosti");
			//System.out.println(doc.text().substring((mallya - 50)<0 ? 0 : (mallya - 50), 400+mallya));
			//System.out.println(URL);
			//stop = true;
			//return;
		}else {
			records.put(URL.toLowerCase(), "NO");
			//System.out.println("NO " + URL);
		}
		//doc.getElementsByTag("a");
		Elements questions = doc.select("a[href]");
		for(Element link: questions){
			String attr = link.attr("abs:href");
			if(!records.containsKey(attr.toLowerCase())) {
				processURL(attr);
				try {
					processingList.offer(attr, 1, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					System.err.println("Queue full ... " + URL);
					records.put(URL.toLowerCase(), "Queue Full");
				}
				//System.out.println("processed " + attr + ", under : " + URL);
			}
		}
	}

	private boolean checkToIgnoreURL(String URL) {
		if(URL == null) {
			return true;
		}
		if(!URL.contains("magicbricks")) {
			return true;
		}
		for(String ignore: ignoreURLs) {
		if(URL.toLowerCase().contains(ignore)) {
			records.put(URL.toLowerCase(), "Ignored");
			System.err.println("Ignoring ... " + URL.toLowerCase());
			return true;
		}
		}
		
		return false;
	}

}

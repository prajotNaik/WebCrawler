package com.pjt.main;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.appbits.appinfo.App;
import com.appbits.appinfo.model.AppInfo;

public class Test {

	public static void main(String[] args) throws IOException {
		String URL = "https://play.google.com/store/apps/details?id=com.fitness22.running";
			Document document = Jsoup.connect(URL).get();
			String text = document.text();
			System.out.println(text);
		 URL = "https://www.whois.com/whois/tiktok.com"; 
		 String append = "";
		 //getWHOISDetails(URL, append, true);
		 
		App app = new App();
		AppInfo appInfo = app.getAppInfo("com.fitness22.running");
	}

	private static void getWHOISDetails(String URL, String append, boolean cont) throws IOException {
		Document document = Jsoup.connect(URL).get();
		String text = document.text();
		//System.out.println(text);

		String search = "Registrar URL:";
		String registerURL = text.substring(text.indexOf(search) + search.length(), text.indexOf("\n", text.indexOf(search))).trim();
		System.out.println(append + " " + search + " : " +registerURL);
		search = "Registrant Country:";
		System.out.println(append + " " + search + " : " +text.substring(text.indexOf(search) + search.length(), text.indexOf("\n", text.indexOf(search))).trim());
		
		if(!cont) {
			return;
		}
		if(null != registerURL && !registerURL.isEmpty() && !registerURL.contains("whois")) {
			getWHOISDetails("https://www.whois.com/whois/" + registerURL.substring(registerURL.indexOf("www.")+"www.".length(), registerURL.length()), "RegisteredURL", false);
		}
		
	}

}

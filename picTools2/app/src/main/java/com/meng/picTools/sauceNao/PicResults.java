package com.meng.picTools.sauceNao;

import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PicResults {

	private final String CLASS_RESULT_CONTENT_COLUMN = "resultcontentcolumn";
	private final String CLASS_RESULT_IMAGE = "resultimage";
	private final String CLASS_RESULT_MATCH_INFO = "resultmatchinfo";
	private final String CLASS_RESULT_SIMILARITY_INFO = "resultsimilarityinfo";
	private final String CLASS_RESULT_TABLE = "resulttable";
	private final String CLASS_RESULT_TITLE = "resulttitle";

	private final String URL_LOOKUP_SUBSTRING = "https://saucenao.com/info.php?lookup_type=";

	private ArrayList<Result> mResults = new ArrayList<>();

	public PicResults(Document document) {
		for (Element result : document.getElementsByClass(CLASS_RESULT_TABLE)) {
			Element resultImage = result.getElementsByClass(CLASS_RESULT_IMAGE).first();
			Element resultMatchInfo = result.getElementsByClass(CLASS_RESULT_MATCH_INFO).first();
			Element resultTitle = result.getElementsByClass(CLASS_RESULT_TITLE).first();
			Elements resultContentColumns = result.getElementsByClass(CLASS_RESULT_CONTENT_COLUMN);
			Result newResult = new Result();
			newResult.loadSimilarityInfo(resultMatchInfo);
			newResult.loadThumbnail(resultImage);
			newResult.loadTitle(resultTitle);
			newResult.loadExtUrls(resultMatchInfo, resultContentColumns);
			newResult.loadColumns(resultContentColumns);
			mResults.add(newResult);
		  }
	  }

	public ArrayList<Result> getResults() {
		return mResults;
	  }

	class Result {
		String mSimilarity;
		String mThumbnail;
		String mTitle;
		ArrayList<String> mExtUrls = new ArrayList<>();
		ArrayList<String> mColumns = new ArrayList<>();

		private void loadSimilarityInfo(Element resultMatchInfo) {
			try {
				mSimilarity = resultMatchInfo.getElementsByClass(CLASS_RESULT_SIMILARITY_INFO).first().text();
			  } catch (NullPointerException e) {
				System.out.println("Unable to load similarity info");
			  }
		  }

		private void loadThumbnail(Element resultImage) {
			try {
				Element img = resultImage.getElementsByTag("img").first();

				if (img.hasAttr("data-src")) {
					mThumbnail = img.attr("data-src");
				  } else if (img.hasAttr("src")) {
					mThumbnail = img.attr("src");
				  }
			  } catch (NullPointerException e) {
				System.out.println("Unable to load thumbnail");
			  }
		  }

		private void loadTitle(Element resultTitle) {
			try {
				mTitle = new HtmlToPlainText().getPlainText(resultTitle);
			  } catch (NullPointerException e) {
				System.out.println("Unable to load title");
			  }
		  }

		private void loadExtUrls(Element resultMatchInfo, Elements resultContentColumns) {
			try {
				for (Element a : resultMatchInfo.getElementsByTag("a")) {
					String href = a.attr("href");

					if (!href.isEmpty() && !href.startsWith(URL_LOOKUP_SUBSTRING)) {
						mExtUrls.add(href);
					  }
				  }

				for (Element resultContentColumn : resultContentColumns) {
					for (Element a : resultContentColumn.getElementsByTag("a")) {
						String href = a.attr("href");
						if (!href.isEmpty() && !href.startsWith(URL_LOOKUP_SUBSTRING)) {
							mExtUrls.add(href);
						  }
					  }
				  }
			  } catch (NullPointerException e) {
				System.out.println("Unable to load external URLs");
			  }
			Collections.sort(mExtUrls);
		  }

		private void loadColumns(Elements resultContentColumns) {
			try {
				for (Element resultContentColumn : resultContentColumns) {
					mColumns.add(new HtmlToPlainText().getPlainText(resultContentColumn));
				  }
			  } catch (NullPointerException e) {
				System.out.println("Unable to load columns");
			  }
		  }
	  }
  }

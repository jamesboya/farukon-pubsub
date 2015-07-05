package com.farukon.resource.service;

import com.farukon.resource.model.Resource;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jamesyan on 7/4/15.
 */
class CosmedResourceService implements ResourceService {
    public static final String META_TAG_PRICE = "price";
    public static final String COSMED_HOST = "https://www.cosmed.com.tw";

    @Override
    public List<Resource> fetchUrl(String url) throws Exception {
        return fetchUrl(url, 1000);
    }

    public List<Resource> fetchUrl(String url, long fetchPeriod) throws Exception {
        CosmedPageType pageType = judgePageType(url);

        List<Resource> resources = new ArrayList<>();

        switch (pageType) {
            case CatalogPage:
                List<String> urls = parseCatalogPage(url);
                if (urls != null) {
                    urls.forEach((String u) -> {
                        try {
                            Thread.sleep(fetchPeriod);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Optional<Resource> resource = parseItemPage(u);
                        if (resource.isPresent()) {
                            resources.add(resource.get());
                        }
                    });
                }
                break;
            case ItemPage:
                Optional<Resource> resource = parseItemPage(url);
                if (resource.isPresent()) {
                    resources.add(resource.get());
                }
                break;
            default:
                throw new Exception("can not judge which page type: " + url);
        }

        return resources;
    }

    private String doHttpGet(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        StringBuilder pageContent = new StringBuilder();
        try {
            HttpResponse response = client.execute(request);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            reader.lines().forEach(
                    (String data) -> {
                        pageContent.append(data);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pageContent.toString();
    }

    public List<String> parseCatalogPage(String url) {
        List<String> navLinks = new ArrayList<>();
        List<String> itemPageLinks = new ArrayList<>();

        String pageContent = doHttpGet(url);
        Matcher matcher = Pattern.compile("(/Products/Index\\.aspx\\?pg=[^\"]+?)\"").matcher(pageContent);
        while (matcher.find()) {
            String navLink = matcher.group(1);
            if (!navLink.contains("&amp;")) {
                navLinks.add(CosmedResourceService.COSMED_HOST + navLink);
            }
        }

        navLinks.forEach((String s) -> {
            String itemPageContent = doHttpGet(s);
            itemPageLinks.addAll(getItemPageLinks(itemPageContent));
        });

        return itemPageLinks;
    }

    private List<String> getItemPageLinks(String pageContent) {
        List<String> links = new ArrayList<>();

        Matcher matcher = Pattern.compile("<div class=\"btnGray\">[^\"]+href=\"([^\"]+)\"").matcher(pageContent);
        while (matcher.find()) {
            links.add(CosmedResourceService.COSMED_HOST + "/Products/" + matcher.group(1));
        }

        return links;
    }

    private Optional<Resource> parseItemPage(String url) {
        String pageContent = doHttpGet(url);
        return parseItemPageContent(pageContent, url);
    }

    private Optional<Resource> parseItemPageContent(String pageContent, String url) {
        Resource resource = null;

        String title = "";
        Matcher matcher = Pattern.compile("<div class=\"product-titleBIG\">([^<]+)</div>").matcher(pageContent);
        if (matcher.find()) {
            title = matcher.group(1);
        }

        String price = "";
        matcher = Pattern.compile("<div class=\"product-listBIG\">[^<]*?([0-9]+)[^<]*?</div>").matcher(pageContent);
        if (matcher.find()) {
            price = matcher.group(1);
        }

        String imageUrl = "";
        matcher = Pattern.compile("<div class=\"product-photoBIG\">.*?src=\"([^\"]+)[^<]*?</div>").matcher(pageContent);
        if (matcher.find()) {
            imageUrl = matcher.group(1);
        }

        if (!title.isEmpty() && !price.isEmpty()) {
            resource = new Resource();

            resource.setTitle(title);
            resource.setUrl(url);

            if (!imageUrl.isEmpty()) {
                resource.setImages(Arrays.asList(CosmedResourceService.COSMED_HOST + imageUrl));
            }

            Map<String, String> meta = new HashMap<>();
            meta.put(CosmedResourceService.META_TAG_PRICE, price);
            resource.setMeta(meta);
        }

        return Optional.ofNullable(resource);
    }

    public CosmedPageType judgePageType(String url) {
        if (url.contains("Index.aspx")) {
            return CosmedPageType.CatalogPage;
        } else if (url.contains("Index_intro.aspx")) {
            return CosmedPageType.ItemPage;
        }

        return CosmedPageType.UnknownPage;
    }
}

package com.farukon.resource.service;

import com.farukon.resource.model.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CosmedResourceServiceTest {
    @Test
    public void pageType_CatalogType() {
        CosmedResourceService service = new CosmedResourceService();
        String url = "https://www.cosmed.com.tw/Products/Index.aspx?Cat01=11";

        Assert.assertEquals(service.judgePageType(url), CosmedPageType.CatalogPage);
    }

    @Test
    public void pageType_ItemType() {
        CosmedResourceService service = new CosmedResourceService();
        String url = "https://www.cosmed.com.tw/Products/Index_intro.aspx?ID=077079&Cat01=11&Cat02=";

        Assert.assertEquals(service.judgePageType(url), CosmedPageType.ItemPage);
    }

    @Test
    public void getPageContent_ItemPage() {
        CosmedResourceService service = new CosmedResourceService();
        String url = "https://www.cosmed.com.tw/Products/Index_intro.aspx?ID=077079&Cat01=11&Cat02=";

        try {
            List<Resource> resources = service.fetchUrl(url);
            Resource resource = resources.get(0);
            Assert.assertEquals(resource.getTitle(), "日絆彈性OK繃-腳後跟型12片");
            Assert.assertEquals(resource.getMeta().get(CosmedResourceService.META_TAG_PRICE), "120");
            Assert.assertEquals(resource.getUrl(), url);
            Assert.assertEquals(resource.getImages().get(0), CosmedResourceService.COSMED_HOST + "/_Files/HotProduct/077079.jpg");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void getPageContent_CatalogPage() {
        CosmedResourceService service = new CosmedResourceService();
        String url = "https://www.cosmed.com.tw/Products/Index.aspx?Cat01=11";

        try {
            List<String> itemPagesLinks = service.parseCatalogPage(url);
            itemPagesLinks.forEach(System.out::println);

            Assert.assertEquals(itemPagesLinks.get(0), CosmedResourceService.COSMED_HOST + "/Products/index_intro.aspx?ID=077079&Cat01=11&Cat02=");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
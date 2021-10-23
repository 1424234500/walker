package com.walker.common.util;

import com.walker.util.FileUtil;
import com.walker.util.HttpBuilder;
import com.walker.util.Tools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class HtmlUtilTest {



   /*
<html>
<div id="blog_list">
<div class="blog_title">
<a href="url1">第一篇博客</a>
</div>
<div class="blog_title">
<a href="url2">第二篇博客</a>
</div>
<div class="blog_title">
<a href="url3">第三篇博客</a>
</div>
</div>
</html>

*/
    @Test
    public void test() {
        String html = "<html><div id=\"blog_list\"><div class=\"blog_title\"><a href=\"url1\">第一篇博客</a></div><div class=\"blog_title\"><a href=\"url2\">第二篇博客</a></div><div class=\"blog_title\"><a href=\"url3\">第三篇博客</a></div></div></html>";
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div[id=blog_list]").select("div[class=blog_title]");
        for( Element element : elements ) {
            String title = element.text();
            String url = element.select("a").attr("href");
            Tools.out(title, url);
        }
    }

    @Test
    public void match() throws Exception {
        String html1 = new HttpBuilder("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html", HttpBuilder.Type.GET)
                .setConnectTimeout(3000).setRequestTimeout(3000).setSocketTimeout(5000)

                .setEncode("utf-8").setDecode("gbk").buildString();
/*

        <tr class="provincetr">
            <td><a href="11.html">北京市<br></a></td>
            <td><a href="12.html">天津市<br></a></td><td><a href="13.html">河北省<br></a></td><td><a href="14.html">山西省<br></a></td><td><a href="15.html">内蒙古自治区<br></a></td><td><a href="21.html">辽宁省<br></a></td><td><a href="22.html">吉林省<br></a></td>
            <td><a href="23.html">黑龙江省<br></a></td>
        </tr>
*/

        Document doc = Jsoup.parse(html1);
        Elements elements = doc.select("tr[class=provincetr]").select("td").select("a");
        for( Element element : elements ) {
            String url = element.attr("href");
            String name = element.text();
            String code = FileUtil.getFileName(name);

            Tools.out(url, name);
        }



    }

    @Test
    public void getTagContent() {
    }
}
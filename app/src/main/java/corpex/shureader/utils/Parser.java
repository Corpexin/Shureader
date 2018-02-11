package corpex.shureader.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import corpex.shureader.dataModels.ContentItem;
import corpex.shureader.dataModels.ThreadItem;
import corpex.shureader.dataModels.ThreadPage;

/**
 * Created with love by Corpex on 02/04/2017.
 */

public class Parser {

    public Parser(){}


    public  ArrayList<ContentItem> parseCategory(String html) {
        String content = "N/A";
        String titulo = "N/A";
        String usuario = "N/A";
        String respVisitas = "N/A";
        String link = "N/A";

        ArrayList<ContentItem> result = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        //encontrar todos los TDs que tengan id que empiecen por td_threadtitle_
        //Elements threads = doc.select("td[title][class=alt1],  td[title][class=alt2]");
        Elements threads = doc.select("tbody[id=threadbits_forum_2] > tr"); //TODO: COMPROBAR QUE PARA EL RESTO DE FOROS ES EL 2 TAMBIEN
        for (Element row : threads) {
            System.out.println("#####################################################");
            System.out.println(row);
            //Dentro  de cada td, tenemos un <a> con el titulo con id = thread_title_
            content = row.select("td[title][class=alt1]").attr("title").replaceAll("\n", " ");
            System.out.println("CONTENIDO: "+content);
            titulo = row.select("td[title][class=alt1] > div > a[id^=thread_title_]").first().text();
            usuario = row.select("td[title][class=alt1] > div > span[onclick]").text();
            respVisitas = row.select("td[title][class=alt2]").attr("title");
            link =  row.select("td[title][class=alt1] > div > a[id^=thread_title_]").attr("href");
            System.out.println("#####################################################");
            result.add(new ContentItem("http://www.forocoches.com/foro/" + link, "General", 3, new ArrayList<String>(), usuario, "i will find", titulo , content, "10/12/2016", "fighting ", "8", respVisitas, "888"));

            //Reseteo variables
            content = "N/A";
            titulo = "N/A";
            usuario = "N/A";
            respVisitas = "N/A";
        }


        return result;
    }

    public ThreadPage parseThreadPage(String html) {
        String fecha;
        String nombreUsuario;
        String subNick;
        String perfilUrl;
        String contentHTML;
        ThreadPage result = new ThreadPage();

        //--Obtencion de los posts
        result.setPosts(new ArrayList<ThreadItem>());
        result.getPosts().add(new ThreadItem("username","avatarURL", "userDescrip", "postDate", "postContent")); //nececsitado para paginacion
        Document doc = Jsoup.parse(html);
        Elements threads = doc.select("table[id^=post]");
        for (Element row : threads) {
            System.out.println("#####################################################");
            System.out.println(row);
            fecha = row.select("tr:first-child > td:first-child").get(0).text();
            nombreUsuario = row.select("tr:eq(1) > td:eq(0) > div:eq(0)").text();
            subNick = row.select("tr:eq(1) > td:eq(0) > div:eq(1)").text();
            perfilUrl = "https:" + row.select("tr:eq(1) > td:eq(0) > div:eq(2) > a > img").attr("src");//.substring(2);
            contentHTML = row.select("td[id^=td_post_]").html();
            contentHTML = contentHTML.replaceAll("div", "BlockQuote").replaceAll("googletag\\.cmd\\.push\\(function\\(\\) \\{ googletag\\.display\\(\'BlockQuote-300x250\'\\); \\}\\);", "").replaceAll("googletag\\.cmd\\.push\\(function\\(\\) \\{ googletag\\.display\\(\'div-300x250\'\\); \\}\\);", "").replaceAll("<script language=\"javascript\"> \\r\\n<!--\\r\\nverVideo\\('", "<img class=\\\"imgpost\\\" src=\\\"http://img.youtube.com/vi/").replaceAll("','\\d+'\\);\\r\\n-->\\r\\n</script>", "/0.jpg\\\" border=\\\"0\\\" alt=\\\"\\\">");
            result.getPosts().add(new ThreadItem(nombreUsuario, perfilUrl, subNick, fecha, contentHTML));
            System.out.println("#####################################################");
        }
        result.getPosts().add(new ThreadItem("username","avatarURL", "userDescrip", "postDate", "postContent")); //nececsitado para paginacion

        //Obtencion de la paginacion
        Elements pag = doc.select("div[class=pagenav] > table > tbody > tr > td[class=vbmenu_control]");
        if(pag.size() > 0){
            String resString = doc.select("div[class=pagenav] > table > tbody > tr > td").first().text(); //String con el resultado de la paginacion con formato 'Pág x de y'
            String[] resArray = resString.split("Pág ")[1].trim().split(" de ");
            result.setCurrentPageNumber(Integer.parseInt(resArray[0]));
            result.setTotalPageNumber(Integer.parseInt(resArray[1]));
        }else {
            result.setCurrentPageNumber(1);
            result.setTotalPageNumber(1);
        }
        return result;
    }
}

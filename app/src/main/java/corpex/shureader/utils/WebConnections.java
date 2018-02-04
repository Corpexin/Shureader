package corpex.shureader.utils;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.ArrayList;

import corpex.shureader.activities.MainActivity;
import corpex.shureader.dataModels.ContentItem;
import corpex.shureader.dataModels.ThreadItem;
import corpex.shureader.dataModels.ThreadPage;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created with love by Corpex on 02/04/2017.
 */

public class WebConnections {
    private OkHttpClient client;
    private Parser parser;

    //Constructor. Creates the client
    public WebConnections(MainActivity mainActivity) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mainActivity));
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        parser = new Parser();
    }

    //Login method. Pass a username and a password. It returns an integer with 3 options (logged, not logged or error)
    public int login(String username, String password) {
        int result;
        RequestBody formBody = new FormBody.Builder()
                .add("vb_login_username", username)
                .add("cookieuser", "1")
                .add("vb_login_password", password)
                .add("securitytoken", "guest")
                .add("do", "login")
                .build();
        final Request request = new Request.Builder()
                .url("http://www.forocoches.com/foro/login.php?do=login")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if(loginResult(response.body().string()))
                    result = Constants.RESULT_LOGGED;
                else
                    result = Constants.RESULT_NOT_LOGGED;
            }else
                result = Constants.RESULT_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            result = Constants.RESULT_ERROR;
        }
        return result;
    }

    private boolean loginResult(String html) {
        return html.contains("Bienvenido de nuevo");
    }

    //Pass a category forum and returns the data of that category parsed.
    public ArrayList<ContentItem> getCategoryData(String url) {

        ArrayList<ContentItem> result = null;
        Request request = new Request.Builder()
                                                .url(url)
                                                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            /**
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
             **/

            result =  parser.parseCategory(response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ThreadPage getThreadPage(String url) {
        ThreadPage result = null;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            result =  parser.parseThreadPage(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}

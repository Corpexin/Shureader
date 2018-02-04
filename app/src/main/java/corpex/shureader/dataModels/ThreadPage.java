package corpex.shureader.dataModels;

import java.util.ArrayList;

/**
 * Created by amadridn on 27/08/2017.
 */

public class ThreadPage {
    private  int currentPageNumber;
    private  int totalPageNumber;
    private  ArrayList<ThreadItem> posts;

    public ThreadPage(){}

    public ThreadPage(int currentPageNumber, int totalPageNumber, ArrayList<ThreadItem> posts) {
        this.currentPageNumber = currentPageNumber;
        this.totalPageNumber = totalPageNumber;
        this.posts = posts;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public int getTotalPageNumber() {
        return totalPageNumber;
    }

    public ArrayList<ThreadItem> getPosts() {
        return posts;
    }


    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public void setTotalPageNumber(int totalPageNumber) {
        this.totalPageNumber = totalPageNumber;
    }

    public void setPosts(ArrayList<ThreadItem> posts) {
        this.posts = posts;
    }
}

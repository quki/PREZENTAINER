package com.puregodic.android.prezentainer.network;

public class NetworkConfig {

    public static final String ROOT_URL = "http://www.qukihub.com:8080/prezentainer-server/";
    /***********
     * USER ACCOUNT
     *************/
    public static String URL_ACCOUNT = ROOT_URL + "account/index.php";

    /***********
     * PRESENTATION INFO
     *************/
    public static String URL_INSERT = ROOT_URL + "presentation/insert.php";

    public static String URL_FETCH_LIST = ROOT_URL + "presentation/select.php";

    public static String URL_FETCH_GRAPH = ROOT_URL + "presentation/select_this_pt.php";

    public static String URL_DELETE = ROOT_URL + "presentation/delete.php";


    /**
     * 요한이 서버
     * http://cyh1704.dothome.co.kr/prezentainer/select.php
     * http://cyh1704.dothome.co.kr/prezentainer/insert.php
     * http://cyh1704.dothome.co.kr/prezentainer/delete.php
     * http://cyh1704.dothome.co.kr/prezentainer/second_select.php
     * http://cyh1704.dothome.co.kr/prezentainer/logregi/index.php
     */


}

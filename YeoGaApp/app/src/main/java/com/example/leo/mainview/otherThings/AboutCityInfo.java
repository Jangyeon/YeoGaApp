package com.example.leo.mainview.otherThings;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.leo.mainview.R;

import java.util.ArrayList;

public class AboutCityInfo extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 팝업창 액션바 지우기
        setContentView(R.layout.activity_about_city_info);

        WebView WebView = (WebView) findViewById(R.id.Webview);
        WebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = WebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String _address =  getIntent().getStringExtra("address");
        String url;

        if(_address.equals("서울시") || _address.equals("서울특별시")){
            url = "http://www.visitseoul.net/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("인천광역시")){
            url = "http://www.travelicn.or.kr/open_content/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("광주광역시")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=5&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=";
            url = "http://www.gjtravel.or.kr/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("대구광역시")){
            url = "http://tour.daegu.go.kr/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("울산광역시")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=7&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://tour.ulsan.go.kr/index.ulsan";
            WebView.loadUrl(url);
        }
        else if(_address.equals("대전광역시")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=3&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://www.daejeon.go.kr/tou/index.do";
            WebView.loadUrl(url);
        }
        else if(_address.equals("부산광역시")){
            url = "http://bto.or.kr/renewal/main/main.php";
            WebView.loadUrl(url);
        }
        else if(_address == "경기도"){
            url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=31&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            WebView.loadUrl(url);
        }
        else if(_address.equals("강원도")){
            url = "http://www.gangwon.to/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("충청남도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=34&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://tour.chungnam.net/html/kr/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("충청북도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=33&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://www.chungbuknadri.net/home/main.do?pageUrl=";
            WebView.loadUrl(url);
        }
        else if(_address.equals("경상북도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=35&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://tour.gb.go.kr/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("경상남도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=36&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://www.gntour.com/html/kr/";
            WebView.loadUrl(url);
        }
        else if(_address.equals("전라북도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=37&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            url = "http://tour.jb.go.kr/index.do";
            WebView.loadUrl(url);
        }
        else if(_address.equals("전라남도")){
            url = "http://www.namdokorea.com/kr2/index.jsp";
            WebView.loadUrl(url);
        }
        else if(_address.equals("제주특별자치도")){
            //url = "http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?areaCode=39&sigunguCode=&catAll1=all&catAll3=all&catAll2=all&keyword=&gotoPage=&type=";
            //url = "http://www.ijto.or.kr/korean/";
            url = "http://www.visitjeju.or.kr/";
            WebView.loadUrl(url);
        }
    }
}

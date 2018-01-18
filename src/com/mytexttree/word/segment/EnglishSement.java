package com.mytexttree.word.segment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.mytexttree.utils.NetUtil;
import com.mytexttree.utils.TextFileUtil;


public class EnglishSement implements IWordSegment {

    private final String url = "http://fanyi.youdao.com/openapi.do";
    private final String keyfrom = "mytexttree";
    private final String key = "606471177";
    private final String type = "data";
    private final String doctype = "xml";
    private final String version = "1.1";
    private final String only = "dict";
    
    private final String format = "([a-zA-Z]+’[a-zA-Z]+)|([a-zA-Z]+'[a-zA-Z]+)|([a-zA-Z]+\\-[a-zA-Z]+)|([a-zA-Z]+)";
   
    public String parseResponseXML(String xmlStr){
        StringBuffer sb = new StringBuffer();
        String ret = null;
        Document document = null;
        try {
             document = DocumentHelper.parseText(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(null == document){
            return ret;
        }
        
        Node errorCode = document.selectSingleNode("/youdao-fanyi/errorCode");
        String code = errorCode.getText().trim();
        if(!"0".equals(code)){
            return ret;
        }
        
        
        //处理音标
        Node phonetic = document.selectSingleNode("/youdao-fanyi/basic/phonetic");
        if(phonetic!=null){
            sb.append(String.format("音标：[%s] ", phonetic.getText()));
        }
        
        Node us_phonetic = document.selectSingleNode("/youdao-fanyi/basic/us-phonetic");
        if(phonetic!=null){
            sb.append(String.format(" 美：[%s] ", us_phonetic.getText()));
        }
        
        Node uk_phonetic = document.selectSingleNode("/youdao-fanyi/basic/uk-phonetic");
        if(phonetic!=null){
            sb.append(String.format(" 英：[%s] ", uk_phonetic.getText()));
        }
        if(sb.toString().length()>0){
            sb.append("\r\n");
        }
        //处理基本释义
        List<?> explains = null;
        if(null != document.selectSingleNode("/youdao-fanyi/basic/explains")){
            explains = document.selectSingleNode("/youdao-fanyi/basic/explains").selectNodes("ex");
        }
        if(null != explains){
            sb.append("基本释义：\r\n");
            for(int i=0; i<explains.size();i++){
                Node ex = (Node)explains.get(i);
                sb.append("    "+ex.getText()+"\r\n");
            }
        }
        //处理网络释义
        List<?> webNodes = document.selectNodes("/youdao-fanyi/web/explain");
        if(null != webNodes){
            sb.append("网络释义：\r\n");
            for(int i=0;i<webNodes.size();i++){
                Node node = (Node)webNodes.get(i);
                Node key = node.selectSingleNode("key");
                sb.append("    "+key.getText() + ": ");
                List<?> webExplains = node.selectNodes("value/ex");
                int count = webExplains.size();
                for(int j=0;j<count;j++){
                    Node webExplain = (Node)webExplains.get(j);
                    if(j<count-1){
                        sb.append(webExplain.getText()+";");
                    }else{
                        sb.append(webExplain.getText());
                    }
                }
                sb.append("\r\n");
            }
        }
        
        String temp = sb.toString();
        if(temp.length()>0){
            ret = sb.toString();
        }
        return ret;
    }
    
    public String getTranslateText(String text, CloseableHttpClient httpClient){
        if(text==null || !text.matches(format)){
            return null;
        }
        String ret = null;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyfrom", keyfrom);
        params.put("key", key);
        params.put("type", type);
        params.put("doctype", doctype);
        params.put("version", version);
        params.put("only", only);
        params.put("q", text);
        String resp = NetUtil.doGetWithHttpClient(httpClient, url, params);
        ret = parseResponseXML(resp);
        return ret;
    }
    
    @Override
    public HashMap<String, Integer> countWord(File file) {
        HashMap<String, Integer> ret = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String message = null;
        try {
            br = new BufferedReader(new FileReader(file));
            while((message = br.readLine()) != null){
                sb.append(message);
                sb.append(" ");
            }
            
            String content = sb.toString();
            
            Pattern  pattern=Pattern.compile(format);  
            Matcher  ma=pattern.matcher(content); 
            ret = new HashMap<String, Integer>();
            while(ma.find()){  
                String word = ma.group();
                TextFileUtil.addEnglishTextMap(ret, word);
            }  
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(null != br){
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return ret;
    }
    
    
    public static void main(String[] args) throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        EnglishSement seg = new EnglishSement();
        String text = seg.getTranslateText("cloud", httpClient);
        System.out.println(text);
        httpClient.close();
    }

}

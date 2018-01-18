package com.mytexttree.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class NetUtil {
    
    public static String doPost(String url, HashMap<String, String> params) 
    {
        String ret = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        
        HttpPost httpPost = new HttpPost(url);  
        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                pairs.add(new BasicNameValuePair(key, value));
            }
            try {
                UrlEncodedFormEntity utfParam = new UrlEncodedFormEntity(pairs, 
                        "UTF-8");
                httpPost.setEntity(utfParam);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        
        try{
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(200 == statusCode)
            {
                HttpEntity result = httpResponse.getEntity(); 
                if(null != result)
                {
                    ret = EntityUtils.toString(result, "UTF-8");
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(null != httpResponse)
                {
                    httpResponse.close();
                }
                httpClient.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }
    
    public static String doPostWithHttpClient(CloseableHttpClient httpClient, String url, HashMap<String, String> params) 
    {
        String ret = null;
        CloseableHttpResponse httpResponse = null;
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                pairs.add(new BasicNameValuePair(key, value));
            }
            try {
                UrlEncodedFormEntity utfParam = new UrlEncodedFormEntity(pairs, 
                        "UTF-8");
                httpPost.setEntity(utfParam);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        
        try{
            //set proxy
            {
                HttpHost proxy = getHttpProcy("10.86.51.11", 3128);
                RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(6000).build();
                httpPost.setConfig(config);
            }
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(200 == statusCode)
            {
                HttpEntity result = httpResponse.getEntity(); 
                if(null != result)
                {
                    ret = EntityUtils.toString(result, "UTF-8");
                }
            }
            else{
                System.out.println("Ifly return error :" + statusCode);
                HttpEntity z = httpResponse.getEntity(); 
                String zz = EntityUtils.toString(z, "UTF-8");
                System.out.println(zz);
                System.out.println("System.exit(-1) !");
                System.exit(-1);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(null != httpResponse)
                {
                    httpResponse.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }  
    
    public static String doGet(String url, HashMap<String, String> params) 
    {
        String ret = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        
        HttpGet httpGet = null;
        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            int paramCount = 0;
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                paramCount++;
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(paramCount <=1 )
                {
                    sb.append("?"+key+"="+value);
                }
                else
                {
                    sb.append("&"+key+"="+value);
                }
            }
        }
        
        try{
            String urlGet = sb.toString();
            httpGet = new HttpGet(urlGet);
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(200 == statusCode)
            {
                HttpEntity result = httpResponse.getEntity(); 
                if(null != result)
                {
                    ret = EntityUtils.toString(result, "UTF-8");
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(null != httpResponse)
                {
                    httpResponse.close();
                }
                httpClient.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }
    
    public static String doGetWithHttpClient(CloseableHttpClient httpClient, 
            String url,  HashMap<String, String> params) 
    {
        String ret = null;
        CloseableHttpResponse httpResponse = null;
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        
        HttpGet httpGet = null;
        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            int paramCount = 0;
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                paramCount++;
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(paramCount <=1 )
                {
                    sb.append("?"+key+"="+value);
                }
                else
                {
                    sb.append("&"+key+"="+value);
                }
            }
        }
        
        try{
            String urlGet = sb.toString();
            System.out.println(urlGet);
            httpGet = new HttpGet(urlGet);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            //set proxy
            {
                HttpHost proxy = getHttpProcy("10.86.51.11", 3128);
                RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(60000).build();
                httpGet.setConfig(config);
            }
            
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(200 == statusCode)
            {
                HttpEntity result = httpResponse.getEntity(); 
                if(null != result)
                {
                    ret = EntityUtils.toString(result, "UTF-8");
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(null != httpResponse)
                {
                    httpResponse.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }
    
    
    public static String doGetHttpURLConnection(String url, HashMap<String, String> params)
    {
        String ret = null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        BufferedReader br = null;
        
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        
        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            int paramCount = 0;
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                paramCount++;
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(paramCount <=1 )
                {
                    sb.append("?"+key+"="+value);
                }
                else
                {
                    sb.append("&"+key+"="+value);
                }
            }
        }
        
        try{
            String urlGet = sb.toString();
            URL httpUrl = new URL(urlGet);
            urlConnection = (HttpURLConnection) httpUrl.openConnection();
            urlConnection.setConnectTimeout(6000);
            int statusCode = urlConnection.getResponseCode();
            if(200 == statusCode)
            {
                is = urlConnection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                StringBuffer xml = new StringBuffer();
                String content = null;
                while((content = br.readLine()) != null)
                {
                    xml.append(content+"\r\n");
                }
                ret = xml.toString();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            if(null != br)
            {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            if(null != is)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            if(null != urlConnection)
            {
                urlConnection.disconnect();
            }
        }
        return ret;
    }
    
    public static String doPostWithHttpURLConnection(String url, HashMap<String, String> params)
    {
        String ret = null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        BufferedReader br = null;
        
        StringBuffer sb = new StringBuffer();
        
        //set parameter
        if(null != params && params.size() > 0)
        {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            int paramCount = 0;
            while(iterator.hasNext())
            {
                Entry<String, String> paramEntry = iterator.next();
                String key = paramEntry.getKey();
                String value = paramEntry.getValue();
                if(null == key || null == value)
                {
                    continue;
                }
                if("".equals(key.trim()) || "".equals(value.trim()))
                {
                    continue;
                }
                paramCount++;
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(paramCount <=1 )
                {
                    sb.append(key+"="+value);
                }
                else
                {
                    sb.append("&"+key+"="+value);
                }
            }
        }
        
        try{
            URL httpUrl = new URL(url);
            urlConnection = (HttpURLConnection) httpUrl.openConnection();
            urlConnection.setConnectTimeout(6000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.connect();
            //set post param
            String paramContent = sb.toString();
            if(paramContent.length()>0)
            {
                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                out.writeBytes(paramContent);
                out.flush();
                out.close(); 
            }
            int statusCode = urlConnection.getResponseCode();
            if(200 == statusCode)
            {
                is = urlConnection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                StringBuffer xml = new StringBuffer();
                String content = null;
                while((content = br.readLine()) != null)
                {
                    xml.append(content+"\r\n");
                }
                ret = xml.toString();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            if(null != br)
            {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            if(null != is)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            if(null != urlConnection)
            {
                urlConnection.disconnect();
            }
        }
        return ret;
    }
    
    
    public static void main(String[] args) {
        String url = "http://ltpapi.voicecloud.cn/analysis/";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("api_key", "K1S4v8B001z6T200H115qy7eMOiUjKnMIDXmCylv");
        map.put("text", "我是中国人，我爱自己的祖国！");
        map.put("pattern", "ws");
        map.put("format", "xml");
        
        {
            String result = doGet(url, map);
            System.out.println(result);
        }
        
        {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String result = doGetWithHttpClient(httpClient, url, map);
            System.out.println(result);
        }
        
        {
            String result = doPost(url, map);
            System.out.println(result);
        }
        
        {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String result = doPostWithHttpClient(httpClient, url, map);
            System.out.println(result);
        }
        
        {
            String result = doGetHttpURLConnection(url, map);
            System.out.println(result);  
        }
        
        {
            String result = doPostWithHttpURLConnection(url, map);
            System.out.println(result);   
        }

    }
    
    public static HttpHost getHttpProcy(String host, int port){
        HttpHost proxy = new HttpHost(host, port);
        return proxy;
    }

}

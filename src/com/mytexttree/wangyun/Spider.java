package com.mytexttree.wangyun;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mytexttree.utils.Log;
import com.mytexttree.utils.NetUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Spider {
    public Log log;
    
    public Spider(String outPath){
        log = new Log(outPath);
        log.init(false);
    }
    
    @Override
    protected void finalize() throws Throwable {
        log.close();
        super.finalize();
    }
    
    class CompanyInfo{
        String id = "未知";
        String name = "未知";
        String address = "未知";
        String createDate = "未知";
        String stage = "未知";
        String number = "未知";
        String description = "";
        String tag = "";
        String url = "未知";
        String scale = "未知";
        
        @Override
        public String toString() {
            String res = String.format("%s\t%s\t%s\t%s\t%s\t%s", id, name, stage, number, description, tag);
            //String res = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", name, address, createDate, scale, stage, number, description, tag, url);
            return res;
        }
    }
    
    public ArrayList<CompanyInfo> getInfoFromJson(CloseableHttpClient httpClient, String json){
        ArrayList<CompanyInfo> ret = new ArrayList<CompanyInfo>();
        JSONObject jsonObj =  JSONObject.fromObject(json);
        JSONArray infoList = (JSONArray)jsonObj.get("ai_com_info");
        int size = infoList.size();
        for(int i = 0; i < size; i++)
        {
            CompanyInfo info = new CompanyInfo();
            try {
                JSONObject companyObject = infoList.getJSONObject(i);
                
                info.id = companyObject.getString("com_id");
                info.name = companyObject.getString("com_name");
                info.description = companyObject.getString("com_des");
                info.stage = companyObject.getString("com_fund_status_name");
                info.number = companyObject.getString("total_money");
                
                JSONArray tagList = (JSONArray)companyObject.get("tags");
                int tagCount = tagList.size();
                for(int j = 0; j < tagCount; j++){
                    JSONObject tagObject = tagList.getJSONObject(j);
                    String tag = tagObject.getString("tag_name");
                    if(j != tagCount)
                    {
                        info.tag = info.tag + tag + " ";  
                    }
                    else
                    {
                        info.tag = info.tag + tag;  
                    }
                }
                
                
                try{
                    //fillCompanyInfo(httpClient, info);
                }catch(Exception e){
                    System.out.println("error to fill:" + info.id);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("error to parse:" + infoList.getJSONObject(i));
                e.printStackTrace();
            }
            
            ret.add(info);
            System.out.println(info);
        }
        return ret;
    }
    
    public void fillCompanyInfo(CloseableHttpClient httpClient, CompanyInfo info){
        String url = String.format("https://www.itjuzi.com/company/%s", info.id);
        String html = NetUtil.doGetWithHttpClient(httpClient, url, null);
        parseHTML(info, html);
    }
    
    public void parseHTML(CompanyInfo info, String html){
        Document doc = Jsoup.parse(html);
        Elements e = doc.select("div.info-line");
        Elements hrefs = e.tagName("a");
        String[] arr = hrefs.text().split(" ");
        int len = arr.length;
        if(arr != null && len>=3)
        {
            info.address=arr[len-3]+arr[len-2]+arr[len-1];
        }
        else{
            info.address=hrefs.text();
        }
        System.out.println(info.address);
        
        info.url = doc.select("div.link-line").tagName("a").text();
        System.out.println(info.url);
        
        String[] moreDex = doc.select("div.des-more").select("h2.seo-second-title").text().split(" ");
        info.name = moreDex[0].split("：")[1];
        info.createDate = moreDex[1].split("：")[1];
        info.scale = moreDex[2].split("：")[1];
    }
    
    public void outPut(ArrayList<CompanyInfo> list){
        int size = list.size();
        for(int i = 0; i < size; i++){
            CompanyInfo info = list.get(i);
            log.writeLine(info.toString());
        }
    }
    
    public static void main(String[] args)throws Exception {
        
        Spider sp = new Spider("d:\\result.txt");
        //sp.getInfoFromJson(null, TextFileUtil.getFileText(new File("d:\\json.txt")));
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", "4");
        for(int i=1; i<=10000; i++)
        {
            map.put("page", String.valueOf(i));
            String content = NetUtil.doGetWithHttpClient(httpClient, "https://www.itjuzi.com/ai/index_ajax", map);
            System.out.println(content);
            if(content.length()<20)
            {
                break;
            }
            sp.outPut(sp.getInfoFromJson(httpClient, content));
        }
    }

}

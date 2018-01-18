package com.mytexttree.word.segment;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.mytexttree.utils.NetUtil;
import com.mytexttree.utils.Progress;
import com.mytexttree.utils.TextFileUtil;

public class WordSegmentIFly implements IWordSegment {
    
    private static final String URL = "http://ltpapi.voicecloud.cn/analysis/";
   
    private static final String KEY_api_key = "api_key";
    private static final String VALUE_api_key = "K1S4v8B001z6T200H115qy7eMOiUjKnMIDXmCylv";
   
    private static final String KEY_text = "text";
    
    private static final String KEY_pattern = "pattern";
    private static final String VALUE_pattern = "ws";
    
    private static final String KEY_format = "format";
    private static final String VALUE_format = "xml";
    private static final int MAX_TEXT_SIZE = 1000;

    @Override
    public HashMap<String, Integer> countWord(File file) {
        boolean status = true;
        HashMap<String, Integer> ret = null;
        LinkedList<String> textArray = null;

        if(null == file)
        {
            status = false;
        }
        
        //parse file into String array with limit size
        if(status)
        {
            textArray = TextFileUtil.getLimitedTextArray(file, MAX_TEXT_SIZE);
            if(null == textArray || textArray.size() < 1)
            {
                status = false;
            }
        }
        
        if(status)
        {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(KEY_api_key, VALUE_api_key);
            params.put(KEY_pattern, VALUE_pattern);
            params.put(KEY_format, VALUE_format);
            System.out.println("创建httpClient开始");
            long startTime = System.currentTimeMillis();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            System.out.println("创建httpClient结束，耗时：" 
            + (System.currentTimeMillis()-startTime) + "毫秒");
            ret  = new HashMap<String, Integer>();
            int listSize = textArray.size();
            Progress progress = new Progress(listSize, "联网解析");
            for(int i = 0; i < listSize; i++)
            {
                params.put(KEY_text, textArray.get(i));
                String responseStr = NetUtil.doPostWithHttpClient(httpClient,
                        URL, params);
                if(null != responseStr)
                {
                    TextFileUtil.combineTextMap(ret, parseResponseStr(responseStr));
                    progress.show(i + 1);
                }
                else
                {
                    System.exit(-1);
                	System.out.println("response str is null !");
                }
            } 
        }
        return ret;
    }

    private HashMap<String, Integer> parseResponseStr(String responseStr)
    {
        Document document = null;
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        
        try {
             document = DocumentHelper.parseText(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(null != document)
        {
            Node para = document.selectSingleNode("/xml4nlp/doc/para");
            List<?> sents = para.selectNodes("sent");
            for(int i = 0; i< sents.size(); i++)
            {
                Element sent = (Element)sents.get(i);
                List<?> words = sent.selectNodes("word");
                for(int j = 0; j < words.size(); j++)
                {
                    Element word = (Element)words.get(j);
                    String wordContent = word.attributeValue("cont");
                    if(TextFileUtil.isValidTextSegment(wordContent))
                    {
                        TextFileUtil.addTextMap(ret, wordContent.trim());
                    }
                }
            }
        }
        return ret;
    }
}

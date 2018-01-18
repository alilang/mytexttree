package com.mytexttree.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.mytexttree.word.segment.EnglishSement;

public class TextFileUtil {
    
    public static int readCharacthers(BufferedReader br, char[] buf, int fillIndex, int size) throws Exception
    {
        if(null == br || null == buf || size <= 0 || buf.length < size)
        {
            return 0;
        }
        
        Arrays.fill(buf, fillIndex, buf.length, '。');
        int count = 0;
        int character = 0;
        while((character = br.read()) != -1)
        {
        	if('\r' == (char)character || '\n' == (char)character 
        	        || ' ' == (char)character || '\t' == (char)character)
        	{
        		continue;
        	}
            buf[fillIndex + count] = (char)character;
            count++;
            if(count >= size - fillIndex)
            {
                break;
            }
        }
        return count;
    }
    
    public static int getEndIndex(char[] buf, int bufLen)
    {
    	int index = bufLen - 1;
    	if(null == buf || bufLen <= 0 || buf.length < bufLen)
    	{
    		return index;
    	}
    	for(int i = bufLen -1; i >= 0; i--)
    	{
    		if('！' == buf[i] || '。' == buf[i] || '，' == buf[i]
    			|| '!' == buf[i] || '、' == buf[i] || '？' == buf[i] || '?' == buf[i])
    		{
    			index = i;
    			break;
    		}
    	}
    	return index;
    }
    
    public static LinkedList<String> getLimitedTextArray (File file, int textSize)
    {
        System.out.println("文件拆分开始");
        long startTime = System.currentTimeMillis();
        LinkedList<String> list = new LinkedList<String>();
        char[] buf = new char[textSize];
        int realSize = 0;
        int endIndex = 0;
        int fillIndex = 0;
        BufferedReader br = null;
        try {
			br = new BufferedReader(new FileReader(file));
			realSize = readCharacthers(br, buf, fillIndex, textSize);
			while(realSize > 0)
			{
				int totalSize = realSize + fillIndex;
				endIndex = getEndIndex(buf, totalSize);
				list.add(new String(buf, 0, endIndex + 1));
				
				int endSize = totalSize - endIndex -1;
				for(int i = 0; i < endSize; i++)
				{
					buf[i] = buf[endIndex + i + 1];
				}
				fillIndex = endSize;
				realSize = readCharacthers(br, buf, fillIndex, textSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != br)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        System.out.println("文件拆分结束，耗时：" + (System.currentTimeMillis()-startTime) + "毫秒");
        return list;
    }
    
    public static void combineTextMap (HashMap<String, Integer> dest, HashMap<String, Integer> src)
    {
        if(null == dest || null == src || 0 == src.size())
        {
            return;
        }
        Iterator<Entry<String, Integer>> iterator = src.entrySet().iterator();
        while(iterator.hasNext())
        {
            Entry<String, Integer> e = iterator.next();
            String text = e.getKey();
            int count = e.getValue();
            if(null == dest.get(text))
            {
                dest.put(text, count);
            }
            else
            {
                int size = dest.get(text);
                dest.put(text, size + count);
            }
        }
    }
    
    public static void addTextMap (HashMap<String, Integer> dest, String text)
    {
        if(null != dest && null != text)
        {
            if(null == dest.get(text))
            {
                dest.put(text, 1);
            }
            else
            {
                int count = dest.get(text);
                dest.put(text, count + 1);
            }
        }
    }
    
    public static void addEnglishTextMap (HashMap<String, Integer> dest, String text)
    {
        if(null != dest && null != text)
        {
            text = text.toLowerCase();
            if(null == dest.get(text))
            {
                dest.put(text, 1);
            }
            else
            {
                int count = dest.get(text);
                dest.put(text, count + 1);
            }
        }
    }
    
    public static List<Map.Entry<String, Integer>> getCountSortedMapList(HashMap<String, Integer> map)
    {
        if(null == map)
        {
            return null;
        }
        
        List<Map.Entry<String, Integer>> ret =
                new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        
        Collections.sort(ret, new Comparator<Map.Entry<String, Integer>>() {   
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
                return (o2.getValue() - o1.getValue()); 
            }
        }); 
        
        return ret;
    }
    
    public static List<Map.Entry<String, Integer>> getKeySortedMapList(HashMap<String, Integer> map)
    {
        if(null == map)
        {
            return null;
        }
        
        List<Map.Entry<String, Integer>> ret =
                new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        
        Collections.sort(ret, new Comparator<Map.Entry<String, Integer>>() {   
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
                return o1.getKey().compareTo(o2.getKey()); 
            }
        }); 
        
        return ret;
    }
    
    public static void logSortedMapList(List<Map.Entry<String, Integer>> list, 
            String logPath, boolean isAppend)
    {
        if(null == list)
        {
            return;
        }
        System.out.println("正在输出结果");
        Log log = new Log(logPath);
        log.init(isAppend);
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < list.size(); i++)
        {
            Map.Entry<String, Integer> e = list.get(i);
            log.writeLine(e.getKey() + ":" + e.getValue());
        }
        log.close();
        System.out.println("结果输出完成，耗时："+ (System.currentTimeMillis() - startTime) + "毫秒！");
    }
    
    public static void translateSortedMapList(List<Map.Entry<String, Integer>> list, 
            String logPath, boolean isAppend)
    {
        if(null == list)
        {
            return;
        }
        System.out.println("正在翻译...");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        EnglishSement seg = new EnglishSement();
        
        Log log = new Log(logPath);
        log.init(isAppend);
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < list.size(); i++)
        {
            Map.Entry<String, Integer> e = list.get(i);
            long trStart = System.currentTimeMillis();
            String text = seg.getTranslateText(e.getKey(), httpClient);
            long trEnd = System.currentTimeMillis();
            long trTime = trEnd - trStart;
            System.out.println(text);
            System.out.println(String.format("耗时%d毫秒,等待%d毫秒翻译下一个", trTime,(3600-trTime)));
            try {
                Thread.sleep(3600-trTime);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            log.writeLine(e.getKey() + ":" + e.getValue()+" 翻译结果：\r\n"+(text==null?"":text));
        }
        log.close();
        try {
            httpClient.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("翻译完成，耗时："+ (System.currentTimeMillis() - startTime) + "毫秒！");
    }
    
    public static void logSortedMapListWithNoCount(List<Map.Entry<String, Integer>> list, 
            String logPath, boolean isAppend)
    {
        if(null == list)
        {
            return;
        }
        Log log = new Log(logPath);
        log.init(isAppend);
        for(int i = 0; i < list.size(); i++)
        {
            Map.Entry<String, Integer> e = list.get(i);
            String key = e.getKey();
            if(key.length()==1){
                key = "\r\n----"+key+"----";
            }
            log.writeLine(key);
        }
        log.close();
    }
    
    public static HashMap<String, Integer> getUnknownWorlds(HashMap<String, Integer> all, HashMap<String, Integer> filter){
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        Iterator<Map.Entry<String, Integer>> it = all.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Integer> e = it.next();
            if(null == filter.get(e.getKey().trim().toLowerCase())){
                ret.put(e.getKey(), e.getValue());
            }
        }
        return ret;
    }
        
    public static boolean isValidTextSegment(String text)
    {
        boolean ret = true;
        if(null == text || text.trim().length()<2)
        {
            return false;
        }
        
        char[] textChars = text.toCharArray();
        for(int i=0; i < textChars.length; i++)
        {
            if(textChars[i] < 0x4E00 || textChars[i]>0X9FA5)
            {
                ret=false;
                break;
            }
        }

        return ret;
    }
    
    public static String getFileText(File file){
        String ret = null;
        BufferedReader br = null;
        String message = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(file));
            while((message = br.readLine())!=null)
            {
                sb.append(message);
                sb.append(" ");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
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
                br = null;
            }
        }
        ret = sb.toString();
        return ret;
    }
}

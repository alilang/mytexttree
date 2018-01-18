package com.mytexttree.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mytexttree.utils.TextFileUtil;
import com.mytexttree.word.segment.EnglishSement;

public class EnglishWordTest {
    
    public static HashMap<String, Integer> updateKnowingList(File file){
        HashMap<String, Integer> ret = null;
        EnglishSement wordSegment = new EnglishSement();
        HashMap<String, Integer> result = wordSegment.countWord(file);
        TextFileUtil.logSortedMapListWithNoCount(TextFileUtil.getKeySortedMapList(result), file.getPath(), false);
        result = wordSegment.countWord(file);
        TextFileUtil.logSortedMapListWithNoCount(TextFileUtil.getKeySortedMapList(result), file.getPath(), false);
        if(null != result){
            ret = result;
        }else{
            ret = new HashMap<String, Integer>();
        }
        return ret;
    }
    
    public static void main(String[] args) {
//        String  str="Don’t you see how pretty they are, all these trees–my hawthorns, and my new pond, on which you have never congratulated me? You look as glum as a night-cap. Don’t you feel this";  
//        String s = "([a-zA-Z]+’[a-zA-Z]+)|([a-zA-Z]+'[a-zA-Z]+)|([a-zA-Z]+\\-[a-zA-Z]+)|([a-zA-Z]+)";
//        Pattern  pattern=Pattern.compile(s);  
//        Matcher  ma=pattern.matcher(str);  
//   
//        while(ma.find()){  
//            System.out.println(ma.group());  
//        }  
//        
//        System.out.println("–".equals("-"));

        HashMap<String, Integer> filter = updateKnowingList(new File("D://know.txt"));
        EnglishSement wordSegment = new EnglishSement();
        HashMap<String, Integer> result = wordSegment.countWord(new File("D://en.txt"));
        HashMap<String, Integer> unknown = TextFileUtil.getUnknownWorlds(result, filter);
        
        List<Map.Entry<String, Integer>> sortByCount = TextFileUtil.getCountSortedMapList(unknown);
        
        TextFileUtil.logSortedMapList(sortByCount, "D://temp.txt", false);
        TextFileUtil.translateSortedMapList(sortByCount, "D://translate.txt", false);
   
    }

}

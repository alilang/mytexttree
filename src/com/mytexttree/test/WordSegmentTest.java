package com.mytexttree.test;

import java.io.File;
import java.util.HashMap;

import com.mytexttree.utils.TextFileUtil;
import com.mytexttree.word.segment.IWordSegment;
import com.mytexttree.word.segment.WordSegmentIFly;

public class WordSegmentTest {

    public static void main(String[] args) {
        {
            IWordSegment wordSegment = new WordSegmentIFly();
            HashMap<String, Integer> result = wordSegment.countWord(new File("D://家有妖妻.txt"));
            TextFileUtil.logSortedMapList(TextFileUtil.getCountSortedMapList(result), "D://zzz.txt", false);
        }
    }
}

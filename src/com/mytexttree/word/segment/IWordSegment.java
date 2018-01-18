package com.mytexttree.word.segment;

import java.io.File;
import java.util.HashMap;

public interface IWordSegment {
    public HashMap<String, Integer> countWord(File file);
}

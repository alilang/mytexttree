package com.mytexttree.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    
    private BufferedWriter bw;
    private String logPath;
    
    public Log(String logPath)
    {
        this.logPath = logPath;
    }

    public void init(boolean isAppend)
    {
        try {
            if(isAppend)
            {
                bw = new BufferedWriter(new FileWriter(logPath, true));
            }
            else
            {
                bw = new BufferedWriter(new FileWriter(logPath));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void writeLine(String log)
    {
        if(bw == null)
        {
            System.out.println("Log 工具尚未初始化!");
        }
        try {
            bw.write(log+"\r\n");
            bw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void close()
    {
        if(null != bw)
        {
            try {
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bw = null;
        }
    }
}

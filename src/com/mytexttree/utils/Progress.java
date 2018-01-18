package com.mytexttree.utils;

public class  Progress{
    private int max;
    private int currentRate;
    private String tittle;
    
    public Progress(int max, String tittle)
    {
        this.max = max;
        this.tittle = tittle;
    }
    
    public void show(int index)
    {
        float rate = ((float)index/(float)max)*100;
        int tempRate = (int)rate;
        if(tempRate>currentRate)
        {
            currentRate = tempRate;
            System.out.println(tittle+" : " + currentRate + "%");
        }
    }
    
    
}

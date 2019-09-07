/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.funnyai.net;

import com.jcraft.jsch.SftpProgressMonitor;

/**
 *
 * @author happyli
 */

public class SystemOutProgressMonitor implements SftpProgressMonitor
{
    public SystemOutProgressMonitor() {;}
    private long max=0;
    private long size=0;
    
    @Override
    public void init(int op, String src, String dest, long max) 
    {
        this.max=max;
        System.out.println("STARTING: " + op + " " + src + " -> " + dest + " total: " + max);
    }

    @Override
    public boolean count(long bytes)
    {
        this.size+=bytes;
        System.out.println(":"+size);
        
        return(true);
    }

    @Override
    public void end()
    {
        System.out.println("\nFINISHED!");
    }
}


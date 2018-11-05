package com.meng.tencos.utils;

import com.meng.tencos.bean.*;
import java.util.*;

/**
 * Created by Gu on 2017/7/28.
 */

public class FileComparator implements Comparator<FileItem>{

    public int compare(FileItem file1,FileItem file2){
        if(file1.getType()>file2.getType()){
            return -1;
        }else if(file1.getType()==file2.getType()){
            if(file1.getCtime()>file2.getCtime())
                return -1;
        }else{
            return 1;
        }
        return 0;
    }

}

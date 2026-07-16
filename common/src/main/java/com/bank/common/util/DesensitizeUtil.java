package com.bank.common.util;

import org.springframework.util.StringUtils;

public class DesensitizeUtil {

    public static String desensitizeCertID(String certID){
        if (StringUtils.isEmpty(certID)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(certID.length()>2){
            sb.append(certID,0,1);
            for (int i=1; i<certID.length()-1; i++){
                sb.append("*");
            }
            sb.append(certID, certID.length()-1, certID.length());
        }else{
            for(int i=0; i<certID.length(); i++){
                sb.append("*");
            }
        }
        return sb.toString();
    }

    public static String desensitizeName(String name){
        if (StringUtils.isEmpty(name)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(name.length()>=3){
            sb.append(name,0,1);
            for (int i=1; i<name.length()-1; i++){
                sb.append("*");
            }
            sb.append(name, name.length()-1, name.length());
        }else{
            sb.append(name, 0, 1);
            for(int i=1; i<name.length(); i++){
                sb.append("*");
            }
        }
        return sb.toString();
    }

    public static String desensitizeCardNo(String CardNo){
        if (StringUtils.isEmpty(CardNo)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(CardNo.length()>=16){
            sb.append(CardNo,0,6);
            for (int i=6; i<CardNo.length()-4; i++){
                sb.append("*");
            }
            sb.append(CardNo, CardNo.length()-1, CardNo.length());
        }else if(CardNo.length()>=4){
            sb.append(CardNo, 0, 1);
            for(int i=1; i<CardNo.length()-1; i++){
                sb.append("*");
            }
            sb.append(CardNo, CardNo.length()-1, CardNo.length());
        }else{
            for(int i = 0; i<CardNo.length(); i++){
                sb.append("*");
            }
        }
        return sb.toString();
    }

    public static String desensitizeMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(mobile.length()>=11){
            sb.append(mobile,0,3);
            for (int i=3; i<mobile.length()-4; i++){
                sb.append("*");
            }
            sb.append(mobile, mobile.length()-4, mobile.length());
        }else if(mobile.length()>4){
            sb.append(mobile, 0, 2);
            for(int i=2; i<mobile.length()-2; i++){
                sb.append("*");
            }
            sb.append(mobile, mobile.length()-2, mobile.length());
        }else{
            for(int i = 0; i<mobile.length(); i++){
                sb.append("*");
            }
        }
        return sb.toString();
    }

    public static String desensitizeAddress(String address){
        if (StringUtils.isEmpty(address)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(address.length()>=10){
            sb.append(address,0,10);
            for (int i=10; i<address.length(); i++){
                sb.append("*");
            }
        }else if(address.length()>3){
            sb.append(address, 0, 3);
            for(int i=3; i<address.length(); i++){
                sb.append("*");
            }
        }else{
            for(int i = 0; i<address.length(); i++){
                sb.append("*");
            }
        }
        return sb.toString();
    }


}

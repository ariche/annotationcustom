package com.lagou.edu.mvcframework.servlet;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityManager {

    private static Map<Pattern, Set<String>> authorityDic=new LinkedHashMap<>();

    public static void addAuthRule(Pattern p, Set<String> s){
        if (!authorityDic.containsKey(p)){
            authorityDic.put(p,s);
        }else {
            Set<String> strings = authorityDic.get(p);
            if (!strings.containsAll(s)){
                for (String r:s){
                    if (!strings.contains(r)){
                        strings.add(r);
                    }
                }
            }
        }
    }

    public static boolean hasAuth(HttpServletRequest req){
        String userName = req.getParameter("userName");
        String url = req.getRequestURI();

        if (null==userName||"".equals(userName)){
            return true;
        }

        Set<String> allRule = new HashSet<>();
        for (Map.Entry<Pattern, Set<String>> ruleEntity:authorityDic.entrySet()){
            if (ruleEntity.getKey().matcher(url).matches()){
                if (!ruleEntity.getValue().contains(userName)){
                    return false;
                }
                return true;
            }

            if (ruleEntity.getKey().matcher(url).regionEnd()!=0){
                allRule.addAll(ruleEntity.getValue());
            }
        }

        if (allRule.contains(userName)){
            return true;
        }

       return false;
    }

}

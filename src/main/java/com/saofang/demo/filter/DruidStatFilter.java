package com.saofang.demo.filter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
  
import com.alibaba.druid.support.http.WebStatFilter;
  
/**
 * druid¹ıÂËÆ÷.
 * @author Administrator
 *
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
    initParams={
            @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//ºöÂÔ×ÊÔ´
     }
)
public class DruidStatFilter extends WebStatFilter{
  
}
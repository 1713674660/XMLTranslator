package com.zc.test;

import java.io.File;

import com.zc.method.BeanToXml;
import com.zc.model.User;

/**
 * 测试javaBean转换为XML文件
 * @author zc
 * 
 */
public class Test {

	public static void main(String[] args) {
		
		User u = new User(1001,"名字","男","湖南省长沙市","13679568812");
		BeanToXml btx = new BeanToXml(u);
		File f = new File("G:/java/xml/");
		btx.Tranformer(f);
		
		System.out.println("完成！");
		
	}

}

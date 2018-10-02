package com.zc.method;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 功能：利用反射机制解析javaBean，生成DOM树
 * @author zc
 * 2018年10月2日16:01:31
 */
public class BeanToXml {

	private Element ele = null;
	private Object obj = null;
	public BeanToXml(Object obj) {
		this.obj = obj;
	}

	/**
	 * 转换
	 * @param 目录
	 */
	public void Tranformer(File f) {
		if(!f.exists()) {
			f.mkdirs();
		}
		Document doc = getDocument();
		//获得class对象
		Class clazz = obj.getClass();
		//类名
		String className = clazz.getSimpleName();
		//创建文档根节点
		ele = doc.createElement(className);
		//写入属性信息
		createXmlField(clazz,doc,ele);
		//写入构造方法信息
		createXmlConstructor(clazz,doc,ele);
		//写入方法信息
		createXmlMethod(clazz,doc,ele);
		
		doc.appendChild(ele);
		
		// 创建XML转换器
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = null;
		try {
			tf = tff.newTransformer();
			// 设置转换器输出特性（字符编码和自动换行）
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			// 定义输入、输出对象
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);// 输入对象DOM
		Result target = new StreamResult(new File(f,className+".xml"));//输出对象
		// 写文件
		try {
			tf.transform(source, target);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	//将有关方法的信息写入DOM树
	private void createXmlMethod(Class clazz, Document doc, Element ele) {

		// 创建用来存储构造方法信息的节点
		Element methods = doc.createElement("methods");
		ele.appendChild(methods);
		// 遍历
		Method[] ms = clazz.getDeclaredMethods();
		for (Method mt : ms) {
			String modifier = Modifier.toString(mt.getModifiers());
			String rtnType = mt.getReturnType().getName();
			String name = mt.getName();
			int count = mt.getParameterCount();
			Class[] types = mt.getParameterTypes();
			
			//信息写入DOM树
			Element m = doc.createElement("method");
			methods.appendChild(m);
			
			Element mdf = doc.createElement("modifier");
			mdf.setTextContent(modifier);
			m.appendChild(mdf);
			
			Element rtn = doc.createElement("rtnType");
			rtn.setTextContent(rtnType);
			m.appendChild(rtn);
			
			Element nm = doc.createElement("name");
			nm.setTextContent(name);
			m.appendChild(nm);
			
			Element number = doc.createElement("count");
			number.setTextContent(String.valueOf(count));
			m.appendChild(number);
			
			createXmlType(doc,m,types);
		}
	}

	//将有关构造方法的信息写入dom树
	private void createXmlConstructor(Class clazz, Document doc, Element ele) {

		// 创建用来存储构造方法信息的节点
		Element constructors = doc.createElement("constructors");
		ele.appendChild(constructors);
		//遍历
		Constructor[] cts = clazz.getDeclaredConstructors();
		for (Constructor c : cts) {
			String modifier = Modifier.toString(c.getModifiers());
			String name = c.getName();
			int count = c.getParameterCount();
			Class[] types = c.getParameterTypes();
			
			//信息写入DOM树
			Element m = doc.createElement("constructor");
			constructors.appendChild(m);
			
			Element mdf = doc.createElement("modifier");
			mdf.setTextContent(modifier);
			m.appendChild(mdf);
			
			Element nm = doc.createElement("name");
			nm.setTextContent(name);
			m.appendChild(nm);
			
			Element number = doc.createElement("count");
			number.setTextContent(String.valueOf(count));
			m.appendChild(number);
			
			createXmlType(doc,m,types);
		}
	}

	//写入方法的参数类型的信息
	private void createXmlType(Document doc,Element m, Class[] types) {
		
		Element ts = doc.createElement("types");
		m.appendChild(ts);
		
		for (Class cla : types) {
			Element type = doc.createElement("type");
			ts.appendChild(type);
			
			type.setTextContent(cla.getSimpleName());
		}
		
	}

	//将有关属性字段的信息写入dom树
	private void createXmlField(Class cla,Document doc, Element ele) {
		
		//创建用来存储属性信息的节点
		Element fields = doc.createElement("fields");
		ele.appendChild(fields);
		//遍历
		Field[] fs = cla.getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			//访问修饰符
			String modifier = Modifier.toString(field.getModifiers());
			//返回类型
			String rtnType = field.getType().getSimpleName();
			//属性名称
			String name = field.getName();
			//属性值
			Object value = null;
			try {
				value = field.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			//信息写入DOM树
			Element f = doc.createElement("field");
			fields.appendChild(f);
			
			Element mdf = doc.createElement("modifier");
			mdf.setTextContent(modifier);
			f.appendChild(mdf);
			
			Element type = doc.createElement("type");
			type.setTextContent(rtnType);
			f.appendChild(type);
			
			Element nm = doc.createElement("name");
			nm.setTextContent(name);
			f.appendChild(nm);
			
			Element v = doc.createElement("value");
			v.setTextContent(String.valueOf(value));
			f.appendChild(v);
			
		}
		
		
	}

	private Document getDocument() {
		//创建解析器工厂
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			//创建解析器
			DocumentBuilder db = dbf.newDocumentBuilder();
			//创建文档对象
			doc = db.newDocument();
			//设置版本
			doc.setXmlVersion("1.0");
			//设置没有dtd或schema说明文档
	 		doc.setXmlStandalone(true);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
}

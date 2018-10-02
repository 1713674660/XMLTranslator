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
 * ���ܣ����÷�����ƽ���javaBean������DOM��
 * @author zc
 * 2018��10��2��16:01:31
 */
public class BeanToXml {

	private Element ele = null;
	private Object obj = null;
	public BeanToXml(Object obj) {
		this.obj = obj;
	}

	/**
	 * ת��
	 * @param Ŀ¼
	 */
	public void Tranformer(File f) {
		if(!f.exists()) {
			f.mkdirs();
		}
		Document doc = getDocument();
		//���class����
		Class clazz = obj.getClass();
		//����
		String className = clazz.getSimpleName();
		//�����ĵ����ڵ�
		ele = doc.createElement(className);
		//д��������Ϣ
		createXmlField(clazz,doc,ele);
		//д�빹�췽����Ϣ
		createXmlConstructor(clazz,doc,ele);
		//д�뷽����Ϣ
		createXmlMethod(clazz,doc,ele);
		
		doc.appendChild(ele);
		
		// ����XMLת����
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = null;
		try {
			tf = tff.newTransformer();
			// ����ת����������ԣ��ַ�������Զ����У�
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			// �������롢�������
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);// �������DOM
		Result target = new StreamResult(new File(f,className+".xml"));//�������
		// д�ļ�
		try {
			tf.transform(source, target);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	//���йط�������Ϣд��DOM��
	private void createXmlMethod(Class clazz, Document doc, Element ele) {

		// ���������洢���췽����Ϣ�Ľڵ�
		Element methods = doc.createElement("methods");
		ele.appendChild(methods);
		// ����
		Method[] ms = clazz.getDeclaredMethods();
		for (Method mt : ms) {
			String modifier = Modifier.toString(mt.getModifiers());
			String rtnType = mt.getReturnType().getName();
			String name = mt.getName();
			int count = mt.getParameterCount();
			Class[] types = mt.getParameterTypes();
			
			//��Ϣд��DOM��
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

	//���йع��췽������Ϣд��dom��
	private void createXmlConstructor(Class clazz, Document doc, Element ele) {

		// ���������洢���췽����Ϣ�Ľڵ�
		Element constructors = doc.createElement("constructors");
		ele.appendChild(constructors);
		//����
		Constructor[] cts = clazz.getDeclaredConstructors();
		for (Constructor c : cts) {
			String modifier = Modifier.toString(c.getModifiers());
			String name = c.getName();
			int count = c.getParameterCount();
			Class[] types = c.getParameterTypes();
			
			//��Ϣд��DOM��
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

	//д�뷽���Ĳ������͵���Ϣ
	private void createXmlType(Document doc,Element m, Class[] types) {
		
		Element ts = doc.createElement("types");
		m.appendChild(ts);
		
		for (Class cla : types) {
			Element type = doc.createElement("type");
			ts.appendChild(type);
			
			type.setTextContent(cla.getSimpleName());
		}
		
	}

	//���й������ֶε���Ϣд��dom��
	private void createXmlField(Class cla,Document doc, Element ele) {
		
		//���������洢������Ϣ�Ľڵ�
		Element fields = doc.createElement("fields");
		ele.appendChild(fields);
		//����
		Field[] fs = cla.getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			//�������η�
			String modifier = Modifier.toString(field.getModifiers());
			//��������
			String rtnType = field.getType().getSimpleName();
			//��������
			String name = field.getName();
			//����ֵ
			Object value = null;
			try {
				value = field.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			//��Ϣд��DOM��
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
		//��������������
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			//����������
			DocumentBuilder db = dbf.newDocumentBuilder();
			//�����ĵ�����
			doc = db.newDocument();
			//���ð汾
			doc.setXmlVersion("1.0");
			//����û��dtd��schema˵���ĵ�
	 		doc.setXmlStandalone(true);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
}

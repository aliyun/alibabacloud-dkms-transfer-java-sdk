package com.aliyun.kms.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class XmlUtil {

    public static String buildRequestXml(Object o) throws Exception {
        Document document = getDocument();
        Element element = document.createElement(o.getClass().getSimpleName());
        transObjectToXml(document, o, element);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMSource domSource = new DOMSource(element);
        try (ByteArrayOutputStream elementBos = new ByteArrayOutputStream()) {
            transformer.transform(domSource, new StreamResult(elementBos));
            domSource.setNode(document);
            return elementBos.toString();
        }
    }

    private static Document getDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        return document;
    }

    private static void transObjectToXml(Document document, Object obj, Element rootElement) throws IllegalAccessException {
        Class clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Element childElement = rootElement;
        for (Field field : declaredFields) {
            transPropertiesToXML(document, obj, field, childElement);
        }
    }

    private static void transPropertiesToXML(Document document, Object obj, Field field, Element childElement) throws IllegalAccessException {
        field.setAccessible(true);
        Object fieldValue = field.get(obj);
        if (null == fieldValue || fieldValue == "") {
            return;
        }
        Class fieldType = field.getType();
        if (isBaseType(fieldType)) {
            String name = field.getName();
            Element element = document.createElement(StringUtils.upperFirstChar(name));
            childElement.appendChild(element);
            element.setTextContent(String.valueOf(fieldValue));
            return;
        }
        if (fieldType.equals(List.class) && field.getGenericType() instanceof ParameterizedType) {
            List<Object> list = (List<Object>) fieldValue;
            for (Object subObj : list) {
                Class<?> subObjClass = subObj.getClass();
                if (isBaseType(subObjClass)) {
                    Element subObjElement = document.createElement(StringUtils.upperFirstChar(field.getName()));
                    childElement.appendChild(subObjElement);
                    subObjElement.setTextContent(String.valueOf(subObj));
                }
            }
            return;
        }
        String name = field.getName();
        Element element = document.createElement(StringUtils.upperFirstChar(name));
        childElement.appendChild(element);
        transObjectToXml(document, fieldValue, element);
    }

    public static boolean isBaseType(Class className) {
        if (className.equals(Integer.class) ||
                className.equals(Byte.class) ||
                className.equals(Long.class) ||
                className.equals(Double.class) ||
                className.equals(Float.class) ||
                className.equals(Character.class) ||
                className.equals(Short.class) ||
                className.equals(Boolean.class) ||
                className.equals(String.class)) {
            return true;
        }
        return false;
    }
}

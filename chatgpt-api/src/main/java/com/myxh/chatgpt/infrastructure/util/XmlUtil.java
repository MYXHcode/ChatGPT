package com.myxh.chatgpt.infrastructure.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.commons.lang3.StringUtils;

import java.io.Writer;

/**
 * @author MYXH
 * @date 2023/12/13
 * @description 微信公众号发送消息，解析工具类
 * @GitHub <a href="https://github.com/MYXHcode">MYXHcode</a>
 */
public class XmlUtil
{
    // xstream 扩展,bean 转 xml 自动加上 ![CDATA[]]
    public static XStream getMyXStream()
    {
        return new XStream(new XppDriver()
        {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out)
            {
                return new PrettyPrintWriter(out)
                {
                    // 对所有 xml 节点都增加 CDATA 标记
                    final boolean cdata = true;

                    @Override
                    public void startNode(String name, Class clazz)
                    {
                        super.startNode(name, clazz);
                    }

                    @Override
                    protected void writeText(QuickWriter writer, String text)
                    {
                        if (cdata && !StringUtils.isNumeric(text))
                        {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        }
                        else
                        {
                            writer.write(text);
                        }
                    }
                };
            }
        });
    }

    /**
     * bean 转成微信的 xml 消息格式
     */
    public static String beanToXml(Object object)
    {
        XStream xStream = getMyXStream();
        xStream.alias("xml", object.getClass());
        xStream.processAnnotations(object.getClass());
        String xml = xStream.toXML(object);

        if (!StringUtils.isEmpty(xml))
        {
            return xml;
        }
        else
        {
            return null;
        }
    }

    /**
     * xml 转成 bean 泛型方法
     */
    public static <T> T xmlToBean(String resultXml, Class clazz)
    {
        // XStream 对象设置默认安全防护，同时设置允许的类
        XStream stream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(stream);
        stream.allowTypes(new Class[]{clazz});
        stream.processAnnotations(new Class[]{clazz});
        stream.setMode(XStream.NO_REFERENCES);
        stream.alias("xml", clazz);

        return (T) stream.fromXML(resultXml);
    }
}

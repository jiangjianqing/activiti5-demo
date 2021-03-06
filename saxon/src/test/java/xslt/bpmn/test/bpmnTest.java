package xslt.bpmn.test;

import static org.junit.Assert.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import my.xslt.XsltUtil;
import net.sf.saxon.s9api.SaxonApiException;

public class bpmnTest {

	private XsltUtil xsltUtil=new XsltUtil();
	@Test
	public void test() throws SaxonApiException, TransformerException {
		//使用doExtendXslt会丢失<?xml version="1.0" encoding="UTF-8"?>
		xsltUtil.doExtendXslt("/xsl/bpmn.xsl", "/data/bpmn_example.bpmn", System.out);
		//xsltUtil.doSimpleXslt("/xsl/bpmn.xsl", "/data/bpmn_example.bpmn", System.out);

	}

}

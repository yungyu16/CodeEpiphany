package com.wenjunhuang.codeepiphany.model

import junit.framework.TestCase
import org.junit.Assert.*

import javax.xml.parsers.DocumentBuilderFactory
import scala.util.Using

class PluginDescriptorTest extends TestCase {
  def testCustomActionReferencesResolve(): Unit = {
    val document = Using.resource(getClass.getResourceAsStream("/META-INF/plugin.xml")) { stream =>
      DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream)
    }
    val elements = document.getElementsByTagName("*")
    val ids = (0 until elements.getLength).flatMap { index =>
      Option(elements.item(index).getAttributes.getNamedItem("id")).map(_.getNodeValue)
    }.toSet
    val references = document.getElementsByTagName("reference")
    val missing = (0 until references.getLength).map { index =>
      references.item(index).getAttributes.getNamedItem("ref").getNodeValue
    }.filter(_.startsWith("CodeEpiphany.")).filterNot(ids.contains)
    assertTrue(s"Missing actions: ${missing.mkString(", ")}", missing.isEmpty)
    assertTrue(ids.contains("CodeEpiphany.Actions.LeetCode.ChangeUI"))
    assertTrue(ids.contains("CodeEpiphany.Dojos.Actions.OpenChallengeGroup"))
  }
}

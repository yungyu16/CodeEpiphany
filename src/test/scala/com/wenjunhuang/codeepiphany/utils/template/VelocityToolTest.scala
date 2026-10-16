package com.wenjunhuang.codeepiphany.utils.template

import junit.framework.TestCase
import org.junit.Assert.*
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity

import java.io.StringWriter
import scala.io.Source
import scala.util.Using

class VelocityToolTest extends TestCase {
  private def render(resource: String, name: String): String = {
    val content = Using.resource(Source.fromInputStream(getClass.getResourceAsStream(s"/velocity/$resource.txt")))(_.mkString)
    val context = new VelocityContext()
    context.put("name", name)
    context.put("velocity", VelocityTool)
    val writer = new StringWriter()
    Velocity.evaluate(context, writer, "test", content)
    writer.toString
  }

  def testHelloworld(): Unit =
    assertEquals("Hello, world!", render("Helloworld", "world"))

  def testCamelCaseName(): Unit =
    assertEquals("HelloThereMyGoodFriend", render("CamelCaseName", "hello there my_good-friend"))
}

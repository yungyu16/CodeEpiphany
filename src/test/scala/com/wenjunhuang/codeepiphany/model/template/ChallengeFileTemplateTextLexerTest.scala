package com.wenjunhuang.codeepiphany.model.template

import com.wenjunhuang.codeepiphany.model.template.lexer.ChallengeFileTemplateTextLexer
import junit.framework.TestCase
import org.junit.Assert.*

class ChallengeFileTemplateTextLexerTest extends TestCase {
  def testNestedMethodReference(): Unit = {
    val input = "public ${velocity.camelCaseName($challenge.slug)} {}"
    val lexer = new ChallengeFileTemplateTextLexer()
    lexer.start(input)
    val tokens = scala.collection.mutable.ListBuffer.empty[(String, String)]
    while (lexer.getTokenType != null) {
      tokens += ((lexer.getTokenType.toString, lexer.getTokenText))
      lexer.advance()
    }
    assertEquals(input, tokens.map(_._2).mkString)
    assertEquals(
      List("TEXT", "START_REF_FORMAL", "IDENTIFIER", "START_PAREN", "START_REF", "IDENTIFIER", "END_PAREN", "END_REF_FORMAL", "TEXT"),
      tokens.map(_._1).toList
    )
  }

  def testResetClearsAnUnfinishedReference(): Unit = {
    val lexer = new ChallengeFileTemplateTextLexer()
    lexer.start("${unfinished")
    lexer.advance()
    lexer.start("plain text")
    assertEquals("TEXT", lexer.getTokenType.toString)
    assertEquals("plain text", lexer.getTokenText)
    lexer.advance()
    assertNull(lexer.getTokenType)
  }
}

package com.wenjunhuang.codeepiphany.model

import junit.framework.TestCase
import org.junit.Assert.*
import org.typelevel.ci.CIString

import java.net.HttpCookie

class CodeDojoTest extends TestCase {
  def testPersistedSiteIdentifiers(): Unit = {
    assertEquals(Set("leetcode", "leetcodecn"), CodeDojo.values.map(_.value).toSet)
    assertEquals(Some(CodeDojo.LeetCode), CodeDojo.fromCIString(CIString("leetcode")))
    assertEquals(Some(CodeDojo.LeetCodeCN), CodeDojo.fromCIString(CIString("LEETCODECN")))
    assertEquals(None, CodeDojo.fromCIString(CIString("unsupported-site")))
  }

  def testCookieHostsResolveToTheCorrectSite(): Unit = {
    assertEquals(Some(CodeDojo.LeetCode), CodeDojo.fromCIHostname(CIString(".leetcode.com")))
    assertEquals(Some(CodeDojo.LeetCodeCN), CodeDojo.fromCIHostname(CIString("WWW.LEETCODE.CN")))
    assertEquals(None, CodeDojo.fromCIHostname(CIString("leetcode.com.example.org")))
    assertEquals(None, CodeDojo.fromCIHostname(CIString("notleetcode.cn")))
  }

  def testLoginRequiresANonEmptySessionCookie(): Unit = {
    for (dojo <- CodeDojo.values) {
      assertFalse(dojo.loginCandidateCookies(List(new HttpCookie("LEETCODE_SESSION", "")), ""))
      assertFalse(dojo.loginCandidateCookies(List(new HttpCookie("csrftoken", "token")), ""))
      assertTrue(dojo.loginCandidateCookies(List(new HttpCookie("LEETCODE_SESSION", "session")), ""))
    }
  }
}

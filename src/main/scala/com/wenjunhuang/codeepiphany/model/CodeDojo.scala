package com.wenjunhuang.codeepiphany.model

import cats.Show
import cats.syntax.all.*
import com.wenjunhuang.codeepiphany.PluginBundle
import org.typelevel.ci.CIString

import java.net.HttpCookie
import javax.swing.Icon
import scala.annotation.static

enum CodeDojo(val domain: CIString, val value: String) {
  case LeetCode extends CodeDojo(CIString("leetcode.com"), "leetcode")
  case LeetCodeCN extends CodeDojo(CIString("leetcode.cn"), "leetcodecn")

  def getIcon: Option[Icon] = this match {
    case LeetCode => Some(icons.CodeEpiphanyIcons.Dojos.LEETCODE)
    case LeetCodeCN => Some(icons.CodeEpiphanyIcons.Dojos.LEETCODECN)
  }

  def getIconPath: Option[String] = this match {
    case LeetCode => Some(icons.CodeEpiphanyIcons.Dojos.LEETCODE_PATH)
    case LeetCodeCN => Some(icons.CodeEpiphanyIcons.Dojos.LEETCODECN_PATH)
  }

  def getLoginURL: String = s"https://${domain}/accounts/login/"

  def loginCandidateCookies(cookies: List[HttpCookie], content: String): Boolean =
    cookies.exists(cookie => cookie.getName == "LEETCODE_SESSION" && cookie.getValue.nonEmpty)

  def requiresCodeRegionEnclosure: Boolean = true

  def difficultyShow(difficulty: String): String = difficulty

  def difficultyShowAsHtml(difficulty: String): String = ""
}

object CodeDojo {
  implicit val codeDojoShow: Show[CodeDojo] = Show.show {
    case LeetCode => "LeetCode"
    case LeetCodeCN => PluginBundle.message("leetcodecn.settings.displayName")
  }

  @static
  def show(dojo: CodeDojo): String = dojo.show

  def fromCIHostname(host: CIString): Option[CodeDojo] =
    CodeDojo.values.find(dojo => host == dojo.domain || host.toString.toLowerCase.endsWith("." + dojo.domain))

  def fromCIShow(value: CIString): Option[CodeDojo] =
    CodeDojo.values.find(dojo => value == CIString(show(dojo)))

  private val ALL_DOJOS = CodeDojo.values.map(dojo => CIString(dojo.value) -> dojo).toMap

  def fromCIString(value: CIString): Option[CodeDojo] = ALL_DOJOS.get(value)
}

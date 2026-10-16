package com.wenjunhuang.codeepiphany.toolwindows.sidebar.description

import com.wenjunhuang.codeepiphany.model.CodeDojo
import com.wenjunhuang.codeepiphany.services.WebViewStyleProvider
import com.wenjunhuang.codeepiphany.utils.extensions.*

object ChallengeDescriptionStyle {
  def getStyle(styleProvider: WebViewStyleProvider, dojo: Option[CodeDojo]): String =
    styleProvider.baseStyle + "\n" + dojo.map(_ => getLeetcodeCNStyle(styleProvider)).getOrElse("")

  private def getLeetcodeCNStyle(styleProvider: WebViewStyleProvider): String =
    // language=CSS
    s"""
       |#container pre {
       |    background-color: unset;
       |    border-left: 2px solid ${styleProvider.contrastedForeground.webRgba()};
       |    margin-bottom: 1rem;
       |    margin-top: 1rem;
       |    padding-left: 1rem;
       |}
       |#container p {
       |  margin-bottom: 1rem;
       |}
       |
       |#container ul {
       |    list-style-type: disc;
       |    margin-bottom: 1rem;
       |    margin-left: 1rem;
       |    margin-right: 1rem;
       |}
       |
       |#container ul > li {
       |    margin-bottom: .75rem;
       |}
       |""".stripMargin

}

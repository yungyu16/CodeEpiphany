package com.wenjunhuang.codeepiphany.editor

import cats.effect.IO
import cats.syntax.all.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.wenjunhuang.codeepiphany.PluginBundle
import com.wenjunhuang.codeepiphany.leetcode.services.{ LeetCodeEvaluationService, LeetCodeSubmissionService }
import com.wenjunhuang.codeepiphany.model.CodeDojo
import com.wenjunhuang.codeepiphany.services.console
import com.wenjunhuang.codeepiphany.services.console.showConsole
import com.wenjunhuang.codeepiphany.settings.ChallengeSettings
import com.wenjunhuang.codeepiphany.utils.syntax.*
import org.typelevel.log4cats.LoggerFactory

import scala.jdk.CollectionConverters.*

object services {
  def runCode(vf: VirtualFile, codeDojo: CodeDojo, project: Project): IO[Unit] = {
    showConsole(project)
      *> console.info(project, codeDojo, PluginBundle.message("test.code.start", vf.getName))
      *> IO.delay {
        val settings = ChallengeSettings.getInstance(project)
        settings.findChallengeId(vf)
      }.flatMap {
        case Some(item) =>
          item.dojo match
            case CodeDojo.LeetCodeCN =>
              ChallengeSettings
                .getInstance(project)
                .findChallengeId(vf) match {
                case Some(challenge) =>
                  LeetCodeEvaluationService(project, CodeDojo.LeetCodeCN).evaluateCode(
                    vf,
                    challenge.testCases.asScala.toList.some
                  )
                case None =>
                  IO.raiseError(new IllegalStateException(PluginBundle.message("error.no.challenge", vf.getName)))
              }

            case CodeDojo.LeetCode =>
              LeetCodeEvaluationService(project, CodeDojo.LeetCode).evaluateCode(vf, item.testCases.asScala.toList.some)
        case None => IO.unit
      }.handleErrorWith { e =>
        console.error(project, PluginBundle.message("test.code.error", e.getMessage))
      }
  }

  def submitCode(vf: VirtualFile, codeDojo: CodeDojo, project: Project): IO[Unit] = {
    val logger   = LoggerFactory.getLogger[IO]
    val settings = ChallengeSettings.getInstance(project)
    (settings.findChallengeId(vf) match
      case Some(item) =>
        showConsole(project) *> console.info(
          project,
          codeDojo,
          PluginBundle.message("submit.code.start", vf.getName, item.dojo.show)
        ) >>
          (
            item.dojo match
              case CodeDojo.LeetCodeCN => LeetCodeSubmissionService(project, CodeDojo.LeetCodeCN).submitCode(vf)
              case CodeDojo.LeetCode   => LeetCodeSubmissionService(project, CodeDojo.LeetCode).submitCode(vf)
          )
      case None => IO.unit
    ).handleErrorWith { e =>
      console.error(project, PluginBundle.message("error.submit.code", e.getMessage)) >>
        logger.warn(e)("Error to submit code")
    }
  }
}

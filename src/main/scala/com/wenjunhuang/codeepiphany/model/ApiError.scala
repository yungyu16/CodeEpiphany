package com.wenjunhuang.codeepiphany.model

import cats.syntax.all.*

import scala.util.control.NoStackTrace

enum ApiError extends NoStackTrace {
  case NotFound(dojo: CodeDojo, message: String) extends ApiError

  case BadRequest(dojo: CodeDojo, message: String) extends ApiError

  case Unauthorized(dojo: CodeDojo, message: String) extends ApiError

  case Conflict(dojo: CodeDojo, message: String) extends ApiError

  case InvalidContent(dojo: CodeDojo, message: String) extends ApiError

  override def getMessage: String = this match
    case NotFound(dojo, message)       => s"${dojo.show} NotFound: $message"
    case BadRequest(dojo, message)     => s"${dojo.show} BadRequest: $message"
    case Unauthorized(dojo, message)   => s"${dojo.show} Unauthorized: $message"
    case Conflict(dojo, message)       => s"${dojo.show} Conflict: $message"
    case InvalidContent(dojo, message) => s"${dojo.show} InvalidContent: $message"

  override def toString: String = getMessage
}

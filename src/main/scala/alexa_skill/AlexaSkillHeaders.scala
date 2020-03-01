package alexa_skill
import org.http4s.HeaderKey
import org.http4s.dsl._
import org.http4s._
import org.http4s.util.{CaseInsensitiveString, Writer}
import scala.util.Try

object `Signature` extends HeaderKey.Singleton {
  type HeaderT = `Signature`
  def name = CaseInsensitiveString("Signature")
  def matchHeader(header: Header): Option[`Signature`] = {
    if (header.name == name)
      Try(`Signature`(header.value)).toOption
    else None
  }

  def parse(s: String): ParseResult[`Signature`] = {
    Right(`Signature`(s))
  }
}

final case class `SignatureCertChainUrl`(version: String) extends Header.Parsed {
  override def key = `SignatureCertChainUrl`
  override def renderValue(writer: Writer): writer.type =
    writer.append(version)
}

object `SignatureCertChainUrl` extends HeaderKey.Singleton {
  type HeaderT = `SignatureCertChainUrl`
  def name = CaseInsensitiveString("SignatureCertChainUrl")
  def matchHeader(header: Header): Option[`SignatureCertChainUrl`] = {
    if (header.name == name)
      Try(`SignatureCertChainUrl`(header.value)).toOption
    else None
  }

  def parse(s: String): ParseResult[`SignatureCertChainUrl`] = {
    Right(`SignatureCertChainUrl`(s))
  }
}

final case class `Signature`(version: String) extends Header.Parsed {
  override def key = `Signature`
  override def renderValue(writer: Writer): writer.type =
    writer.append(version)
}


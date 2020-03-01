import org.scalatest._

import org.http4s.Uri

import alexa_skill._

class CheckSignatureUriSpec extends FlatSpec with Matchers {

  def shouldPass(uri: Uri) = {
    (IntentHandlerEx.checkSignatureUri(uri)) should be (Right(uri))
  }

  def shouldFail(uri: Uri, expectedMessage: String) = {
    (IntentHandlerEx.checkSignatureUri(uri)) should be (Left(expectedMessage))
  }

  "checkSignatureUri" should "ensure the protocol is https (not case sensitive)" in {
    shouldPass(Uri.uri("https://s3.amazonaws.com/echo.api/echo-api-cert.pem"))
    shouldFail(Uri.uri("http://s3.amazonaws.com/echo.api/echo-api-cert.pem"), "invalid protocol")
  }

  it should "ensure the hostname is s3.amazonaws.com (not case sensitive)" in {
    shouldPass(Uri.uri("https://S3.amazonaws.Com/echo.api/echo-api-cert.pem"))
    shouldFail(Uri.uri("https://notamazon.com/echo.api/echo-api-cert.pem"), "invalid hostname")
  }

  it should " ensure the path begins with /echo.api/ (case sensitive)" in {
    shouldPass(Uri.uri("https://s3.amazonaws.com:443/echo.api/stuff/echo-api-cert.pem"))
    shouldPass(Uri.uri("https://s3.amazonaws.com/echo.api/../echo.api/echo-api-cert.pem"))
    shouldFail(Uri.uri("https://s3.amazonaws.com/EcHo.aPi/echo-api-cert.pem"), "invalid path")
    shouldFail(Uri.uri("https://s3.amazonaws.com/invalid.path/echo-api-cert.pem"), "invalid path")
  }

  it should "only allow allow connections on port 443" in {
    shouldPass(Uri.uri("https://s3.amazonaws.com:443/echo.api/echo-api-cert.pem"))
    shouldFail(Uri.uri("https://s3.amazonaws.com:563/echo.api/echo-api-cert.pem"), "invalid port")
  }

}

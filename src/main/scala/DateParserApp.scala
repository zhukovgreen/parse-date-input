import wvlet.log.{LogLevel, LogSupport, Logger}

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date
import scala.util.Try


/** Collection of utils to parse the date.
 *
 * @param log_level set logger level
 * */
class DateParserApp(log_level: LogLevel = LogLevel.INFO) extends LogSupport {

  // Prepare logger
  Logger.init
  Logger.setDefaultLogLevel(log_level)
  info(s"Logger set to ${Logger.getDefaultLogLevel} level")

  // set some constants
  val POSSIBLE_DATA_FMTS: List[String] = List("yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd", "dd-MM-yyyy", "dd/MM/yyyy", "dd.MM.yyyy")

  /** Parse the date with the given format.
   *
   * @param fmt  i.e. "dd-MM-yyyy"
   * @param date i.e. "20-12-2020"
   * */
  def parse_date(fmt: String, date: String): Date = {
    try {
      val date_formatter = new SimpleDateFormat(fmt)
      date_formatter.parse(date)
    } catch {
      case e: ParseException =>
        error(s"Can't parse $date for $fmt")
        throw e
      case e: IllegalArgumentException =>
        error(s"Wrong format: $fmt")
        throw e
    }
  }

  /** Guess the date from the given string
   *
   * Check some commonly used date formats and try to parse the string with it.
   *
   * @param date date represetned by a string, i.e. "12-30-2020"
   * @return first element
   * @throws IllegalArgumentException if string can't be parsed for all known formats
   * */
  def guess_date(date: String): Date = {
    val maybe_result = POSSIBLE_DATA_FMTS.view.map(fmt => Try(parse_date(fmt, date))).find(_.isSuccess).map(_.get)
    if (maybe_result.isEmpty) {
      error(s"Can't parse the $date with the available formats: $POSSIBLE_DATA_FMTS")
      throw new IllegalArgumentException
    }
    else maybe_result.get
  }
}
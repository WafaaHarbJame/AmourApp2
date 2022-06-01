package com.amour.shop.Utils

import android.app.Activity
import android.util.Log
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp.getLanguage
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateHandler {
    //    static String lang = UtilityApp.INSTANCE.getLanguage();
    fun GetDate(d: Any): Date {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        parser.timeZone = TimeZone.getTimeZone(Constants.TIME_ZONE)
        var date = Date()
        try {
            date = parser.parse(d.toString())
        } catch (e: Exception) {
        }
        return date
    }

    fun GetDateNow(): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        return calendar.time
    }

    fun GetDateFromDateHour(d: Any): Date {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var date = Date()
        try {
            date = parser.parse(d.toString())
        } catch (e: Exception) {
        }
        return date
    }

    fun GetLongToDateString(time_stamp: Long): String {
        try {
            val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val time = Date(time_stamp * 1000)
            return parser.format(time)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetDateOnlyString(time_stamp: Long): String {
        try {
            val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val time = Date(time_stamp * 1000)
            return parser.format(time)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetMonthOnlyString(): String {
        try {
            val parser: DateFormat = SimpleDateFormat("MM")
            val date = Date()
            return parser.format(date)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetYearOnlyString(): String {
        try {
            val parser: DateFormat = SimpleDateFormat("yy")
            val date = Date()
            return parser.format(date)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetDateTimeLong(data: String?): Long {
        try {
            val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            parser.timeZone = TimeZone.getTimeZone(Constants.TIME_ZONE)
            val dateLong = parser.parse(data).time
            return dateLong / 1000
        } catch (e: Exception) {
        }
        return 0
    }

    fun GetDateTimeLongCalender(year: Int, month: Int, day: Int, hour: Int, min: Int): Long {
        try {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
            calendar[year, month, day, hour, min] = 0

//            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            val dateLong = calendar.timeInMillis
            //            long dateLong = parser.parse(data).getTime();
            return dateLong / 1000
        } catch (e: Exception) {
        }
        return 0
    }

    fun GetDateOnlyLong(data: String?): Long {
        try {
            val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateLong = parser.parse(data).time
            return dateLong / 1000
        } catch (e: Exception) {
        }
        return 0
    }

    fun GetMonthName(d: Any): String {
        val calendar =
            Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        calendar.time = GetDate(d)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale(getLanguage()))
    }

    fun GetSyrianMonthName(d: Any): String {
        val monthesAr = arrayOf(
            "كانون الثاني",
            "شباط",
            "آذار",
            "نيسان",
            "أيار",
            "حزيران",
            "تموز",
            "آب",
            "أيلول",
            "تشرين الأول",
            "تشرين الثاني",
            "كانون الأول"
        )
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        calendar.time = GetDate(d)
        val monthName: String
        //        if (UtilityApp.INSTANCE.getLanguage().equals("ar")) {
        val monthInd = calendar[Calendar.MONTH]
        monthName = monthesAr[monthInd]
        //        } else {
//            monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en"));
//        }
        return monthName
    }

    fun GetYearNumber(d: Any): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        calendar.time = GetDate(d)
        val yearNumber = calendar[Calendar.YEAR]
        return yearNumber.toString() + ""
    }

    fun GetDayOfMonth(d: Any): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        calendar.time = GetDate(d)
        val monthNumber = calendar[Calendar.DAY_OF_MONTH]
        return monthNumber.toString() + ""
    }

    fun GetDayOfWeek(d: Any): String? {
        val dayAr = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayEn = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE))
        calendar.time = GetDate(d)

//        String[] days = new String[]{
//                RootApplication.getInstance().getResources().getString(R.string.sunday)
//                , RootApplication.getInstance().getResources().getString(R.string.monday)
//                , RootApplication.getInstance().getResources().getString(R.string.tuesday)
//                , RootApplication.getInstance().getResources().getString(R.string.wednesday)
//                , RootApplication.getInstance().getResources().getString(R.string.thursday)
//                , RootApplication.getInstance().getResources().getString(R.string.friday)
//                , RootApplication.getInstance().getResources().getString(R.string.saturday)
//        };
        var day: String? = null
        day = if (getLanguage() == Constants.Arabic) {
            dayAr[calendar[Calendar.DAY_OF_WEEK] - 1]
        } else {
            dayEn[calendar[Calendar.DAY_OF_WEEK] - 1]
        }
        return day
    }

    fun GetDateString(date: Date?, format: String?): String {
        val parser: DateFormat = SimpleDateFormat(format)
        try {
            return parser.format(date)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetDateAndTimeString(date: String): Array<String> {
        return date.split(" ").toTypedArray()
    }

    fun GetDateString(d: Date?): String {
        val parser: DateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        try {
            return parser.format(d)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetTimeString(d: Date?): String {
        val parser: DateFormat = SimpleDateFormat("hh:mm aa", Locale.US)
        try {
            return parser.format(d)
        } catch (e: Exception) {
        }
        return ""
    }

    fun GetDateNowString(): String {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        try {
            // Date s = parser.parse("");
            return parser.format(date)
        } catch (e: Exception) {
        }
        return ""
    }

    /**
     * format string date as yyyy-MM-dd
     *
     * @param o
     * @return
     */
    fun FormatDate(o: Any): String {
        val parser = SimpleDateFormat("yyyy-MM-dd")
        try {
            val date = parser.parse(o.toString())
            return parser.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * format date as yyyy-MM-dd hh:mm aa
     *
     * @param o
     * @return
     */
    fun FormatDate2(o: Any): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val formater = SimpleDateFormat("yyyy-MM-dd hh:mm aa")
        try {
            val date = parser.parse(o.toString())
            return formater.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * format date as MMM-dd
     *
     * @param o
     * @return
     */
    fun FormatDate3(o: Any): String {
        val parser = SimpleDateFormat("yyyy-MM")
        val formater = SimpleDateFormat("MMMM yyyy", Locale(getLanguage()))
        try {
            val date = parser.parse(o.toString())
            return formater.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun FormatDate4(o: Any, inPattern: String?, outPattern: String?, locale: String?): String {
        val parser = SimpleDateFormat(inPattern)
        val parser2 = SimpleDateFormat(
            outPattern,
            Locale(locale ?: getLanguage())
        )
        //        parser.setTimeZone(TimeZone.getDefault());
        parser.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date = parser.parse(o.toString())
            return NumberHandler.arabicToDecimal(parser2.format(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
    //    public static String GetDayOfMonth(Activity activity, Object d) {
    //
    //
    //        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE));
    //
    //        calendar.setTime(GetDate(d));
    //
    //        int monthNumber = calendar.get(Calendar.DAY_OF_MONTH);
    //        return monthNumber + "";
    //    }
    //
    //    public static String GetDayOfWeek(Activity activity, Object d) {
    //
    //
    //        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE));
    //
    //        calendar.setTime(GetDate(d));
    //
    //        String[] days = new String[]{
    //                activity.getString(R.string.sunday)
    //                , activity.getString(R.string.monday)
    //                , activity.getString(R.string.thusday)
    //                , activity.getString(R.string.wednesday)
    //                , activity.getString(R.string.thursday)
    //                , activity.getString(R.string.friday)
    //                , activity.getString(R.string.satday)
    //        };
    //
    //        String day = days[(calendar.get(Calendar.DAY_OF_WEEK) - 1)];
    //        return day;
    //    }
    /**
     * format time from HH:mm to hh:mm aa
     *
     * @param o
     * @return
     */
    fun FormatTime(o: Any): String {
        val parser = SimpleDateFormat("HH:mm")
        val formater = SimpleDateFormat("hh:mm aa")
        try {
            val date = parser.parse(o.toString())
            return formater.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * format time from HH:mm to HH:mm
     *
     * @param o
     * @return
     */
    fun FormatTime2(o: Any): String {
        val parser = SimpleDateFormat("HH:mm")
        val formater = SimpleDateFormat("HH:mm")
        try {
            val date = parser.parse(o.toString())
            return NumberHandler.arabicToDecimal(formater.format(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun GetTimeFromDateString(timeStamp: Long): String {
        val date1 = Date(timeStamp * 1000)
        val cal = Calendar.getInstance()
        cal.time = date1
        val hour = cal[Calendar.HOUR_OF_DAY]
        val minute = cal[Calendar.MINUTE]
        return FormatTime("$hour:$minute")
    }

    fun FormatHoursToString(activity: Activity?, hours: Int): String {
        val dayAr = "يوم"
        val dayEn = "Day"
        val hourAr = "ساعة"
        val hourEn = "Hour"
        val andAr = "و"
        val andEn = "And"
        val dayStr: String
        val hourStr: String
        val andStr: String
        var dayText = ""
        var hourText = ""
        val restHour: Double
        val day = hours / 24
        restHour = (hours % 24).toDouble()
        if (getLanguage() == Constants.English) {
            dayStr = dayEn
            hourStr = hourEn
            andStr = andEn
        } else {
            dayStr = dayAr
            hourStr = hourAr
            andStr = andAr
        }
        val hour = (restHour * 60).toInt()
        if (day > 0) {
            dayText = "$day $dayStr"
            //            dayText = day < 3 ? "" : (day + " ");
//            dayText += day == 1 ? "يوم" : day == 2 ? "يومان" : day < 11 ? "ايام" : "يوم";
        }
        if (hour > 0) {
            hourText = if (day > 0) " $andStr " else ""
            hourText = "$hour $hourStr"
            //            hourText += hour < 3 ? "" : (hour + " ");
//            hourText += hour == 1 ? "ساعة" : hour == 2 ? "ساعتين" : hour < 11 ? "ساعات" : "ساعة";
        }
        return dayText + hourText
    }

    fun ConvertMinutesToString(minutes: Double, IsHour: Boolean): String {
        var minutes = minutes
        if (IsHour) {
            minutes = minutes * 60
        }
        var dayText = ""
        var hourText = ""
        var minuteText = ""
        val day = minutes.toInt() / 1440
        minutes = minutes % 1440
        val hour = minutes.toInt() / 60
        minutes = minutes % 60
        if (day > 0) {
            dayText = if (day < 3) "" else "$day "
            dayText += if (day == 1) "يوم" else if (day == 2) "يومان" else if (day < 11) "ايام" else "يوم"
        }
        if (hour > 0) {
            hourText = if (day > 0) " و " else ""
            hourText += if (hour < 3) "" else "$hour "
            hourText += if (hour == 1) "ساعة" else if (hour == 2) "ساعتين" else if (hour < 11) "ساعات" else "ساعة"
        }
        if (minutes > 0) {
            minuteText = if (day > 0 || hour > 0) " و " else ""
            minuteText += if (minutes < 3) "" else "$minutes "
            minuteText += if (minutes == 1.0) "دقيقة" else if (minutes == 2.0) "دقيقتين" else if (minutes < 11) "دقائق" else "دقيقة"
        }
        return "$dayText $hourText $minuteText"
    }

    fun ConvertMinutesToString2(minutes: Double, IsHour: Boolean): String {
        var minutes = minutes
        if (IsHour) {
            minutes = minutes * 60
        }
        var dayText = ""
        var hourText = ""
        var minuteText = ""
        val day = minutes.toInt() / 1440
        minutes = minutes % 1440
        val hour = minutes.toInt() / 60
        minutes = minutes % 60
        val minutesInt = minutes.toInt()
        if (day > 0) {
            dayText = if (day < 1) "" else "$day ي"
        }
        if (hour > 0) {
            hourText = if (day > 0) " و " else ""
            hourText += if (hour < 1) "" else "$hour س"
        }
        if (minutes > 0) {
            minuteText = if (day > 0 || hour > 0) " و " else ""
            minuteText += if (minutesInt < 1) "" else "$minutesInt د"
        }
        return "$dayText $hourText $minuteText"
    }

    fun formatStringToAgo( /*Activity activity,*/
        dateText: String?
    ): String {
        val time = GetDateTimeLong(dateText)
        return formatToAgo( /*activity, */time)
    }

    fun formatToAgo( /*Activity activity,*/
        dateText: Long
    ): String {
        val yearInSec = 31104000
        val monthInSec = 2592000
        val weekInSec = 604800
        val dayInSec = 68400
        val hourInSec = 3600
        val minInSec = 60
        val sinceAr = "منذ"
        val sinceEn = "since"
        val sinceStr: String
        val currentTime = System.currentTimeMillis() / 1000

//        String lang = UtilityApp.INSTANCE.getLanguage();
//        System.out.println("Log diff "+ (currentTime - oldTime));
        sinceStr = if (getLanguage() == Constants.English) {
            sinceEn
        } else {
            sinceAr
        }
        val diff = currentTime - dateText

//        Log.e("DateHandler", "Log diff time " + diff);
        var yearText = ""
        var monthText = ""
        var weekText = ""
        var dayText = ""
        var hourText = ""
        var minuteText = ""
        val year = diff.toInt() / yearInSec
        //        diff = diff % yearInSec;
        if (year > 0) {
            yearText = getDateStrings(year, "year", getLanguage())
            //            if (year == 1) {
//                yearText = RootApplication.getInstance().getResources().getString(R.string.year);
//            } else if (year == 2) {
//                yearText = RootApplication.getInstance().getResources().getString(R.string.two_year);
//            } else if (year <= 10) {
//                yearText = year + " " + RootApplication.getInstance().getResources().getString(R.string.years);
//            } else {
//                yearText = year + " " + RootApplication.getInstance().getResources().getString(R.string.year);
//            }
            return "$sinceStr $yearText"
        }
        val month = diff.toInt() / monthInSec
        //        diff = diff % monthInSec;
        if (month > 0) {
            monthText = getDateStrings(month, "month", getLanguage())
            //            if (month == 1) {
//                monthText = RootApplication.getInstance().getResources().getString(R.string.month);
//            } else if (month == 2) {
//                monthText = RootApplication.getInstance().getResources().getString(R.string.two_month);
//            } else if (month <= 10) {
//                monthText = month + " " + RootApplication.getInstance().getResources().getString(R.string.months);
//            } else {
//                monthText = month + " " + RootApplication.getInstance().getResources().getString(R.string.month);
//            }
            return "$sinceStr $monthText"
        }
        val week = diff.toInt() / weekInSec
        //        diff = diff % weekInSec;
        if (week > 0) {
            weekText = getDateStrings(week, "week", getLanguage())
            //            if (week == 1) {
//                weekText = RootApplication.getInstance().getResources().getString(R.string.week);
//            } else if (week == 2) {
//                weekText = RootApplication.getInstance().getResources().getString(R.string.two_week);
//            } else if (week <= 10) {
//                weekText = week + " " + RootApplication.getInstance().getResources().getString(R.string.weeks);
//            } else {
//                weekText = week + " " + RootApplication.getInstance().getResources().getString(R.string.week);
//            }
            return "$sinceStr $weekText"
        }
        val day = diff.toInt() / dayInSec
        //        diff = diff % dayInSec;
        if (day > 0) {
            dayText = getDateStrings(day, "day", getLanguage())
            //            if (day == 1) {
//                dayText = RootApplication.getInstance().getResources().getString(R.string.day);
//            } else if (day == 2) {
//                dayText = RootApplication.getInstance().getResources().getString(R.string.two_day);
//            } else if (day <= 10) {
//                dayText = day + " " + RootApplication.getInstance().getResources().getString(R.string.days);
//            } else {
//                dayText = day + " " + RootApplication.getInstance().getResources().getString(R.string.day);
//            }
            return "$sinceStr $dayText"
        }
        val hour = diff.toInt() / hourInSec
        //        diff = diff % hourInSec;
        if (hour > 0) {
            hourText = getDateStrings(hour, "hour", getLanguage())
            //            if (hour == 1) {
//                hourText = RootApplication.getInstance().getResources().getString(R.string.hour);
//            } else if (hour == 2) {
//                hourText = RootApplication.getInstance().getResources().getString(R.string.two_hour);
//            } else if (hour <= 10) {
//                hourText = hour + " " + RootApplication.getInstance().getResources().getString(R.string.hours);
//            } else {
//                hourText = hour + " " + RootApplication.getInstance().getResources().getString(R.string.hour);
//            }
            return "$sinceStr $hourText"
        }
        val minutes = diff.toInt() / minInSec
        if (minutes > 0) {
            minuteText = getDateStrings(minutes, "minute", getLanguage())
            //            if (minutes == 1) {
//                minuteText = RootApplication.getInstance().getResources().getString(R.string.minute);
//            } else if (minutes == 2) {
//                minuteText = RootApplication.getInstance().getResources().getString(R.string.two_minute);
//            } else if (minutes <= 10) {
//                minuteText = minutes + " " + RootApplication.getInstance().getResources().getString(R.string.minutes);
//            } else {
//                minuteText = minutes + " " + RootApplication.getInstance().getResources().getString(R.string.minute);
//            }
            return "$sinceStr $minuteText"
        }
        return getDateStrings(0, "now", getLanguage())
    }

    private fun getDateStrings(count: Int, type: String, lang: String?): String {
        when (type) {
            "year" -> return if (count == 1) {
                if (lang == Constants.Arabic) "سنة" else "1 year"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "سنتين" else "2 years"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count سنوات" else "$count years"
            } else {
                if (lang == Constants.Arabic) "$count سنة" else "$count year"
            }
            "month" -> return if (count == 1) {
                if (lang == Constants.Arabic) "شهر" else "1 month"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "شهرين" else "2 months"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count شهور" else "$count months"
            } else {
                if (lang == Constants.Arabic) "$count شهر" else "$count month"
            }
            "week" -> return if (count == 1) {
                if (lang == Constants.Arabic) "إسبوع" else "1 week"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "اسبوعين" else "2 weeks"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count أسابيع" else "$count weeks"
            } else {
                if (lang == Constants.Arabic) "$count إسبوع" else "$count week"
            }
            "day" -> return if (count == 1) {
                if (lang == Constants.Arabic) "يوم" else "1 day"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "يومين" else "2 days"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count ايام" else "$count days"
            } else {
                if (lang == Constants.Arabic) "$count يوم" else "$count day"
            }
            "hour" -> return if (count == 1) {
                if (lang == Constants.Arabic) "ساعة" else "1 hour"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "ساعتين" else "2 hours"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count ساعات" else "$count hours"
            } else {
                if (lang == Constants.Arabic) "$count ساعة" else "$count hour"
            }
            "minute" -> return if (count == 1) {
                if (lang == Constants.Arabic) "دقيقة" else "1 min"
            } else if (count == 2) {
                if (lang == Constants.Arabic) "دقيقتان" else "2 mins"
            } else if (count <= 10) {
                if (lang == Constants.Arabic) "$count دقائق" else "$count mins"
            } else {
                if (lang == Constants.Arabic) "$count دقيقة" else "$count min"
            }
            "now" -> return if (lang == Constants.Arabic) "الأن" else "now"
        }
        return ""
    }

    fun ConvertSecondsToMinutes(dateText: Long): Double {
        val str = ""
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        var d: Date? = null
        try {
            d = dateFormat.parse(dateText.toString() + "")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val oldTime = d!!.time
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - oldTime
        val diffMinutes = diff / (60 * 1000)
        return diffMinutes.toDouble()
    }

    fun convertLongDateToString(date: String, format: String?, lang: String?): String {
        var stringDate = ""
        try {
            val millisecond = date.toLong()
            val dateString = android.text.format.DateFormat.format(format, Date(millisecond)).toString()
            val simpleDateFormat = SimpleDateFormat(format, Locale("en"))
            val dateTime = simpleDateFormat.parse(dateString)
            if (dateTime != null) {
                stringDate = simpleDateFormat.format(dateTime)
            }
            Log.d("TAG", "convertLongDate: $stringDate")
        } catch (e: ParseException) {
            e.printStackTrace()
            stringDate = ""
        }
        return stringDate
    }

    fun GetTimeString(d: Date?, lang: String?): String {
        val parser: DateFormat = SimpleDateFormat("hh:mm aa", Locale(lang))
        try {
            return parser.format(d)
        } catch (e: Exception) {
        }
        return ""
    }

    fun FormatDate4(o: Any, inPattern: String?, outPattern: String?): String {
        val locale: Locale
        locale = if (getLanguage() == "ar") {
            Locale("ar")
        } else {
            Locale("en")
        }
        val parser = SimpleDateFormat(inPattern)
        val parser2 = SimpleDateFormat(outPattern, locale)
        try {
            val date = parser.parse(o.toString())
            return parser2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}

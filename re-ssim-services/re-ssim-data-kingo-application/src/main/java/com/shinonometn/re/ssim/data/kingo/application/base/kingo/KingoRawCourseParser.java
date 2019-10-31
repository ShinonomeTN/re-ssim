package com.shinonometn.re.ssim.data.kingo.application.base.kingo;

import com.shinonometn.re.ssim.data.kingo.application.base.kingo.pojo.Course;
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.pojo.Lesson;
import com.shinonometn.re.ssim.data.kingo.application.base.kingo.pojo.TimePoint;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class KingoRawCourseParser {

    private KingoRawCourseParser() {
    }

    public static Course parseFromHTML(Document document) {
        Element root = document.select("table").first();
        Elements tables = root.select("table");
        tables.remove(0);

        Course subject = new Course();

        if (tables.size() <= 0) return subject;

        processSubjectTerm(subject, tables.get(0));
        processSubjectTitle(subject, tables.get(1));
        subject.setLessons(processLessonList(tables.get(2)));

        return subject;
    }

    private static void processSubjectTerm(Course subject, Element tableElement) {
        Elements elements = tableElement.select("tr");
        subject.setTerm(elements.get(1).children().text());
    }

    private static void processSubjectTitle(Course subject, Element tableElement) {
        Elements fields = tableElement.select("td");
        String text = fields.first().text();
        String[] split = text.split(new String(new char[]{160}));
        for (String s : split) {
            String[] fieldSplit = s.split("：");
            switch (fieldSplit[0]) {
                case "承担单位":
                    subject.setUnit(fieldSplit[1]);
                    break;
                case "课程":
                    subject.setNameWithCode(fieldSplit[1]);
                    break;
                case "总学时":
                    subject.setTimeSpend(Double.parseDouble(fieldSplit[1]));
                    break;
                case "学分":
                    subject.setPoint(Double.parseDouble(fieldSplit[1]));
                    break;
                default:
                    break;
            }
        }
    }

    private static List<Lesson> processLessonList(Element tableElement) {
        LinkedList<Lesson> lessons = new LinkedList<>();
        Lesson lessonLatest = null;

        Elements tableItems = tableElement.select("tr");
        tableItems.removeIf(element -> !element.child(0).attr("width").equals(""));

        for (Element items : tableItems) {
            Lesson lesson = new Lesson();

            String weekRange = "";
            String timePoints = "";

            Elements elements = items.getElementsByTag("td");
            for (int i = 0; i < elements.size(); i++) {
                Element contentChild = items.child(i);
                String c = contentChild.text().trim();
                boolean fillLatest = "".equals(c) && lessonLatest != null;
                switch (i) {
                    case 0:
                        lesson.setTeacher(fillLatest ? lessonLatest.getTeacher() : c);
                        break;
                    case 1:
                        lesson.setClassNumber(fillLatest ? lessonLatest.getClassNumber() : c);
                        break;
                    case 2:
                        if (fillLatest) lesson.setAttendAmount(lessonLatest.getAttendAmount());
                        else lesson.setAttendAmount(Integer.parseInt(c));
                        break;
                    case 3:
                        lesson.setClassType(fillLatest ? lessonLatest.getClassType() : c);
                        break;
                    case 4:
                        lesson.setAssessmentType(fillLatest ? lessonLatest.getAssessmentType() : c);
                        break;
                    case 5:
                        lesson.setClassAttend(fillLatest ? lessonLatest.getClassAttend() : c.split(" "));
                        break;
                    case 6:
                        weekRange = c;
                        break;
                    case 7:
                        timePoints = c;
                        break;
                    case 8:
                        lesson.setPosition(c);
                        break;
                    default:
                        break;
                }
            }
            lesson.setTimePoint(expandKingoWeekdays(weekRange, timePoints));
            lessons.add(lesson);
            lessonLatest = lessons.getLast();
        }
        return lessons;
    }

    /**
     * Chinese weekday mappings
     */
    private static Map<String, Integer> weekdayMap = new HashMap<String, Integer>() {
        {
            put("日", 0);
            put("一", 1);
            put("二", 2);
            put("三", 3);
            put("四", 4);
            put("五", 5);
            put("六", 6);
        }
    };

    /**
     * Expand course timepoints from a chinese form
     */
    public static List<TimePoint> expandKingoWeekdays(String weekRanges, String timePoints) {
        List<TimePoint> results = new LinkedList<>();

        List<Integer> weeks = expandWeeks(weekRanges);
        for (Integer week : weeks) {
            String[] strings = timePoints.replace("节", "").split("[\\[\\]]");
            if (strings.length >= 3) {
                switch (strings[2]) {
                    case "双":
                        if (week % 2 != 0) continue;
                        break;
                    case "单":
                        if (week % 2 == 0) continue;
                        break;
                    default:
                        break;
                }
            }

            String[] r = strings[1].split("-");
            if (r.length > 1) {
                for (int i = Integer.parseInt(r[0]); i <= Integer.parseInt(r[1]); i++) {
                    results.add(new TimePoint(week, weekdayMap.get(strings[0]), i));
                }
            } else {
                results.add(new TimePoint(week, weekdayMap.get(strings[0]), Integer.parseInt(r[0])));
            }
        }

        return results;
    }

    /**
     * Expand weekdays
     * <p>
     * input  : "1-6,9-14"
     * output : [1,2,3,4,5,6,9,10,11,12,13,14]
     *
     * @param weekRanges ranges
     * @return list of weeks
     */
    private static List<Integer> expandWeeks(String weekRanges) {
        String[] list = weekRanges.split(",");
        List<Integer> _weeks = new LinkedList<>();
        for (String s : list) {
            String[] range = s.split("-");
            if (range.length == 1) {
                _weeks.add(Integer.parseInt(range[0]));
            } else {
                int _startWeek = Integer.parseInt(range[0]);
                int _endWeek = Integer.parseInt(range[range.length - 1]);
                for (int i = _startWeek; i <= _endWeek; i++) {
                    _weeks.add(i);
                }
            }
        }
        return _weeks;
    }

}
